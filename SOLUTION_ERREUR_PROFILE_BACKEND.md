# Solution - Erreur HTTP 500 sur /api/user/profile

## ✅ Frontend Corrigé
J'ai amélioré la gestion d'erreur côté frontend pour des messages plus clairs.

## 🔧 Actions Requises Côté Backend

### 1. **Vérifier le Contrôleur UserController**

Assurez-vous que ce contrôleur existe dans votre backend Spring Boot :

```java
package com.yourpackage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.yourpackage.model.User;
import com.yourpackage.service.UserService;

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
                .body(null);
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
        @RequestBody UpdateProfileRequest request,
        Authentication authentication) {
        try {
            String email = authentication.getName();
            User updatedUser = userService.updateProfile(email, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
        @RequestBody ChangePasswordRequest request,
        Authentication authentication) {
        try {
            String email = authentication.getName();
            userService.changePassword(email, request);
            return ResponseEntity.ok(new MessageResponse("Mot de passe modifié avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                .body(new MessageResponse("Erreur lors du changement de mot de passe"));
        }
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<MessageResponse> deleteAccount(Authentication authentication) {
        try {
            String email = authentication.getName();
            userService.deleteAccount(email);
            return ResponseEntity.ok(new MessageResponse("Compte supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new MessageResponse("Erreur lors de la suppression"));
        }
    }
}
```

### 2. **Vérifier la Configuration Security**

Dans votre `SecurityConfig.java` :

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/user/profile").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/user/profile").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/user/change-password").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/user/profile").authenticated()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

### 3. **Vérifier le Service UserService**

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User findByEmail(String email) {
        return userRepository.findByAdresseEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    
    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setNumTel(request.getNumTel());
        user.setAdresseEmail(request.getAdresseEmail());
        user.setDateNaissance(request.getDateNaissance());
        user.setTaille(request.getTaille());
        user.setPoids(request.getPoids());
        user.setSexe(request.getSexe());
        user.setObjectif(request.getObjectif());
        user.setNiveauActivite(request.getNiveauActivite());
        
        return userRepository.save(user);
    }
    
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), user.getMotDePasse())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }
        
        // Encoder et sauvegarder le nouveau mot de passe
        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        userRepository.save(user);
    }
    
    public void deleteAccount(String email) {
        User user = findByEmail(email);
        userRepository.delete(user);
    }
}
```

### 4. **Vérifier le ComponentScan**

Dans votre classe principale `Application.java` :

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.yourpackage.controller",  // ← Important !
    "com.yourpackage.service",
    "com.yourpackage.repository",
    "com.yourpackage.config"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 5. **Vérifier les Logs de Démarrage**

Au démarrage de Spring Boot, vous devriez voir :
```
Mapped "{[/api/user/profile],methods=[GET]}" onto public org.springframework.http.ResponseEntity<User> com.yourpackage.controller.UserController.getProfile(Authentication)
```

## 🧪 Test de Diagnostic

### Test avec curl
```bash
# Tester l'endpoint directement
curl -X GET http://localhost:8100/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Réponse Attendue
```json
{
  "id": 1,
  "nom": "Doe",
  "prenom": "John",
  "adresseEmail": "john@example.com",
  "numTel": "0123456789",
  "dateNaissance": "1990-01-01",
  "taille": 175.0,
  "poids": 70.0,
  "sexe": "M",
  "objectif": "maintien",
  "niveauActivite": "modere",
  "imc": 22.86,
  "age": 34
}
```

## 📱 Côté Frontend - Améliorations Appliquées

- ✅ **Messages d'erreur plus clairs** selon le code HTTP
- ✅ **Détection spécifique** de l'erreur "No static resource"
- ✅ **Gestion des tokens expirés** (401)
- ✅ **Gestion des permissions** (403)
- ✅ **Logs détaillés** pour le debugging

## 🎯 Priorité des Actions

1. **URGENT** : Vérifier que UserController existe et est scanné
2. **IMPORTANT** : Vérifier la configuration Security
3. **MOYEN** : Tester avec curl pour isoler le problème
4. **FAIBLE** : Optimiser les messages d'erreur backend

Une fois le backend corrigé, l'erreur HTTP 500 disparaîtra et le profil utilisateur s'affichera correctement.