# Diagnostic - Erreur HTTP 500 sur /api/user/profile

## Problème Identifié
```
HTTP 500 - "No static resource api/user/profile."
```

## Analyse de l'Erreur

### Message d'Erreur Backend
```json
{
  "message": "Une erreur interne s'est produite: No static resource api/user/profile.",
  "errors": null,
  "timestamp": "2026-01-02T00:28:43.0657595"
}
```

### Cause Probable
Le backend Spring Boot traite la requête `GET /api/user/profile` comme une demande de **ressource statique** au lieu d'un **endpoint API**.

## Causes Possibles Côté Backend

### 1. **Contrôleur manquant ou mal configuré**
```java
// Le contrôleur UserController pourrait être manquant ou mal annoté
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/profile")  // ← Cet endpoint pourrait être manquant
    public ResponseEntity<User> getProfile() {
        // Implementation manquante
    }
}
```

### 2. **Configuration Spring Security incorrecte**
```java
// Dans SecurityConfig, l'endpoint pourrait être mal configuré
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/api/user/**").authenticated()  // ← Vérifier cette ligne
        // ...
}
```

### 3. **Problème de mapping des contrôleurs**
- Le contrôleur n'est pas scanné par Spring
- Annotation `@ComponentScan` manquante
- Package du contrôleur non inclus

### 4. **Conflit avec configuration de ressources statiques**
```java
// Dans WebConfig, conflit possible avec:
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/api/**")  // ← Problématique si présent
        .addResourceLocations("classpath:/static/");
}
```

## Solutions Backend Recommandées

### Solution 1: Vérifier le Contrôleur
```java
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ErrorResponse("Erreur lors de la récupération du profil"));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
        @RequestBody UpdateProfileRequest request,
        Authentication authentication) {
        // Implementation...
    }
}
```

### Solution 2: Vérifier la Configuration Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/user/**").authenticated()  // ← Vérifier
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
```

### Solution 3: Vérifier le ComponentScan
```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.yourpackage.controller",  // ← Inclure le package des contrôleurs
    "com.yourpackage.service",
    "com.yourpackage.repository"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Solution de Contournement Frontend

En attendant la correction backend, nous pouvons ajouter une gestion d'erreur plus robuste :

### Dans UserRepository
```kotlin
suspend fun getProfile(): Result<User> {
    return try {
        android.util.Log.d("UserRepository", "Appel API getProfile...")
        val user = userApiService.getProfile()
        android.util.Log.d("UserRepository", "Profil reçu: ${user.nom} ${user.prenom}")
        Result.success(user)
    } catch (e: retrofit2.HttpException) {
        when (e.code()) {
            500 -> {
                android.util.Log.e("UserRepository", "Erreur serveur 500 - Endpoint profile non configuré")
                Result.failure(Exception("Service temporairement indisponible. Veuillez réessayer plus tard."))
            }
            else -> {
                android.util.Log.e("UserRepository", "Erreur HTTP ${e.code()}: ${e.message()}")
                Result.failure(e)
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("UserRepository", "Erreur getProfile: ${e.message}", e)
        Result.failure(e)
    }
}
```

## Test de Diagnostic

### Vérifier les autres endpoints user
Testez si les autres endpoints fonctionnent :
- `PUT /api/user/profile` (updateProfile)
- `PUT /api/user/change-password` (changePassword)
- `DELETE /api/user/profile` (deleteAccount)

### Vérifier les logs backend
Regardez les logs Spring Boot pour voir :
- Si le contrôleur est bien chargé au démarrage
- Si la requête arrive au contrôleur
- Quelle est l'erreur exacte côté serveur

## Actions Immédiates

1. **Côté Backend** : Vérifier que le UserController existe et est correctement configuré
2. **Côté Frontend** : Appliquer la gestion d'erreur améliorée
3. **Test** : Vérifier les autres endpoints user pour isoler le problème

## Priorité
🔴 **HAUTE** - L'endpoint profile est essentiel pour l'affichage des informations utilisateur.