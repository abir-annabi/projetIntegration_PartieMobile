# Diagnostic Erreur 403 - Accès Refusé

## 🔴 Symptômes

- ❌ Impossible d'accéder aux Programmes
- ❌ Impossible d'accéder à Mes Programmes  
- ❌ Impossible d'accéder aux Plats
- ✅ Le login fonctionne (token JWT reçu)

## 🔍 Analyse

### Le problème n'est PAS dans le frontend Android

Le code frontend est correct:
- ✅ Le token est bien stocké après le login
- ✅ Le token est bien envoyé dans les requêtes (header Authorization)
- ✅ Les requêtes sont bien formées

### Le problème EST dans le backend

Le backend **rejette systématiquement** toutes les requêtes authentifiées avec **403 Forbidden**.

## 🎯 Cause Racine

Le backend a un problème de configuration Spring Security ou JWT. Voici les causes possibles:

### 1. **Erreurs de Migration Hibernate au Démarrage**

Les logs backend montrent:
```
ERROR: column "date_fin_prevue" of relation "user_programmes" contains null values
ERROR: column "type" of relation "activites_sportives" contains null values
ERROR: column "duree_minutes" of relation "activites_sportives" contains null values
```

**Impact**: Ces erreurs peuvent empêcher le backend de démarrer correctement, causant des problèmes d'authentification.

### 2. **Configuration Spring Security Incorrecte**

Le filtre JWT ou la configuration des endpoints peut être mal configuré.

### 3. **Token JWT Non Validé**

Le JwtAuthFilter ne valide peut-être pas correctement le token ou ne définit pas l'authentification dans le SecurityContext.

## ✅ Solutions Backend à Appliquer

### Solution 1: Corriger les Migrations Hibernate (PRIORITAIRE)

#### Étape 1: Se connecter à PostgreSQL
```bash
psql -U postgres -d votre_base_de_donnees
```

#### Étape 2: Mettre à jour les données existantes
```sql
-- Pour user_programmes
UPDATE user_programmes 
SET date_fin_prevue = date_debut + INTERVAL '30 days' 
WHERE date_fin_prevue IS NULL;

-- Pour activites_sportives
UPDATE activites_sportives 
SET type = 'AUTRE' 
WHERE type IS NULL;

UPDATE activites_sportives 
SET duree_minutes = 30 
WHERE duree_minutes IS NULL;
```

#### Étape 3: Redémarrer le backend
```bash
./mvnw spring-boot:run
# ou
./gradlew bootRun
```

### Solution 2: Vérifier la Configuration Spring Security

#### Fichier: SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/programmes/**").authenticated()
                .requestMatchers("/api/plats/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Solution 3: Vérifier le JwtAuthFilter

#### Fichier: JwtAuthFilter.java

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader("Authorization");
            
            logger.info("=== JWT Filter ===");
            logger.info("Request URI: {}", request.getRequestURI());
            logger.info("Auth Header: {}", authHeader != null ? "Present" : "Missing");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No Bearer token found");
                filterChain.doFilter(request, response);
                return;
            }
            
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            
            logger.info("Token username: {}", username);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtService.validateToken(token, userDetails)) {
                    logger.info("Token valid, setting authentication");
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.info("Authentication set successfully");
                } else {
                    logger.error("Token validation failed");
                }
            }
        } catch (Exception e) {
            logger.error("JWT Filter error: ", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Solution 4: Vérifier le JwtService

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.info("Token validation for {}: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation error: ", e);
            return false;
        }
    }
    
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());
        logger.info("Token expiration: {}, Expired: {}", expiration, expired);
        return expired;
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

## 🧪 Tests à Effectuer

### Test 1: Vérifier que le backend démarre sans erreur
```bash
# Chercher dans les logs:
# ✅ "Started Application in X seconds"
# ❌ "ERROR" ou "WARN" liés à Hibernate
```

### Test 2: Tester l'authentification avec curl
```bash
# 1. Login
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"aida@gmail.com","password":"votre_password"}'

# Copier le token reçu

# 2. Tester l'accès aux programmes
curl -X GET http://localhost:8086/api/programmes \
  -H "Authorization: Bearer VOTRE_TOKEN"

# Résultat attendu: 200 OK avec la liste des programmes
# Résultat actuel: 403 Forbidden
```

### Test 3: Vérifier les logs du backend
Chercher dans les logs:
- Messages du JwtAuthFilter
- Erreurs de validation du token
- Problèmes de SecurityContext

## 📋 Checklist de Résolution

- [ ] Corriger les migrations Hibernate (UPDATE des colonnes NULL)
- [ ] Redémarrer le backend
- [ ] Vérifier que le backend démarre sans erreur
- [ ] Ajouter des logs dans JwtAuthFilter
- [ ] Tester avec curl
- [ ] Vérifier la configuration Spring Security
- [ ] Tester depuis l'application Android

## 🎓 Explication Technique

Le problème est un **cycle vicieux**:

1. Les migrations Hibernate échouent au démarrage
2. Le backend démarre en mode dégradé
3. La configuration Spring Security ne s'initialise pas correctement
4. Le JwtAuthFilter ne valide pas les tokens
5. Toutes les requêtes authentifiées retournent 403

**La solution**: Corriger les migrations Hibernate en premier, puis tout le reste fonctionnera.
