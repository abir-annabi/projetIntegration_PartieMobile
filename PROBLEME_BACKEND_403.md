# Problème Backend - Erreur 403 Forbidden

## 🔴 Problème Identifié

Lorsque l'utilisateur accède aux écrans "Programmes" ou "Mes Programmes", l'application reçoit une erreur **403 Forbidden** du backend.

### Logs Android:
```
--> GET http://10.0.2.2:8086/api/programmes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
<-- 403 http://10.0.2.2:8086/api/programmes (35ms)
```

### Logs Backend:
```
ERROR: column "date_fin_prevue" of relation "user_programmes" contains null values
ERROR: column "type" of relation "activites_sportives" contains null values
ERROR: column "duree_minutes" of relation "activites_sportives" contains null values
```

## 🔍 Causes Possibles

### 1. **Token JWT Invalide ou Expiré**
- Le token est envoyé mais rejeté par le backend
- Possible expiration du token
- Configuration de sécurité Spring Security incorrecte

### 2. **Problèmes de Migration Hibernate**
- Des colonnes NOT NULL sont ajoutées à des tables contenant déjà des données NULL
- Cela peut causer des problèmes de démarrage du backend

### 3. **Configuration CORS**
- Possible problème de configuration CORS dans Spring Security

## ✅ Solutions Appliquées (Frontend Android)

### 1. **Amélioration de l'AuthInterceptor**
```kotlin
// Détection automatique des erreurs 401/403
if (response.code == 403 || response.code == 401) {
    preferencesManager.clearToken()
    preferencesManager.clearUser()
}
```

### 2. **Gestion des Erreurs dans ProgrammeRepository**
```kotlin
catch (e: retrofit2.HttpException) {
    when (e.code()) {
        401, 403 -> Result.failure(Exception("Session expirée. Veuillez vous reconnecter."))
        else -> Result.failure(Exception("Erreur serveur: ${e.message()}"))
    }
}
```

### 3. **Redirection Automatique vers Login**
Les activités ProgrammesActivity et MesProgrammesActivity redirigent maintenant automatiquement vers l'écran de login si la session est expirée.

## 🔧 Solutions à Appliquer (Backend)

### 1. **Corriger les Migrations Hibernate**

#### Option A: Ajouter des valeurs par défaut
```sql
-- Pour user_programmes
ALTER TABLE user_programmes 
ADD COLUMN date_fin_prevue DATE DEFAULT CURRENT_DATE;

-- Puis retirer le défaut et mettre NOT NULL
ALTER TABLE user_programmes 
ALTER COLUMN date_fin_prevue DROP DEFAULT;

-- Pour activites_sportives
ALTER TABLE activites_sportives 
ADD COLUMN type VARCHAR(255) DEFAULT 'AUTRE';

ALTER TABLE activites_sportives 
ADD COLUMN duree_minutes INTEGER DEFAULT 30;

-- Puis retirer les défauts
ALTER TABLE activites_sportives 
ALTER COLUMN type DROP DEFAULT,
ALTER COLUMN duree_minutes DROP DEFAULT;
```

#### Option B: Mettre à jour les données existantes
```sql
-- Mettre à jour les enregistrements existants
UPDATE user_programmes 
SET date_fin_prevue = date_debut + INTERVAL '30 days' 
WHERE date_fin_prevue IS NULL;

UPDATE activites_sportives 
SET type = 'AUTRE' 
WHERE type IS NULL;

UPDATE activites_sportives 
SET duree_minutes = 30 
WHERE duree_minutes IS NULL;
```

#### Option C: Modifier les entités JPA
```java
// Dans UserProgramme.java
@Column(name = "date_fin_prevue", nullable = true) // Temporairement nullable
private LocalDate dateFinPrevue;

// Dans ActiviteSportive.java
@Column(name = "type", nullable = true)
private String type;

@Column(name = "duree_minutes", nullable = true)
private Integer dureeMinutes;
```

### 2. **Vérifier la Configuration Spring Security**

Assurez-vous que l'endpoint `/api/programmes` est accessible avec un token JWT valide:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/programmes/**").authenticated() // Vérifier cette ligne
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 3. **Vérifier le JwtAuthFilter**

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        try {
            String token = extractToken(request);
            if (token != null && jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                // Charger l'utilisateur et définir l'authentification
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.error("Erreur d'authentification JWT", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 4. **Ajouter des Logs de Débogage**

```java
@RestController
@RequestMapping("/api/programmes")
public class ProgrammeController {
    
    @GetMapping
    public ResponseEntity<List<Programme>> getAllProgrammes(Authentication authentication) {
        logger.info("Requête getAllProgrammes par: {}", authentication.getName());
        logger.info("Authorities: {}", authentication.getAuthorities());
        // ...
    }
}
```

## 🧪 Tests à Effectuer

1. **Tester le token JWT**
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8086/api/programmes
   ```

2. **Vérifier les logs du backend**
   - Chercher les erreurs d'authentification
   - Vérifier si le filtre JWT est appelé

3. **Tester avec Postman**
   - Login pour obtenir un nouveau token
   - Utiliser ce token pour appeler `/api/programmes`

## 📝 Prochaines Étapes

1. ✅ Frontend: Gestion des erreurs 403 et redirection vers login (FAIT)
2. ⏳ Backend: Corriger les migrations Hibernate
3. ⏳ Backend: Vérifier la configuration Spring Security
4. ⏳ Backend: Ajouter des logs de débogage
5. ⏳ Tester l'authentification end-to-end
