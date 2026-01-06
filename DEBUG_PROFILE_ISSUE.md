# Guide de Débogage - Problème d'Affichage du Profil

## 🔍 Logs Ajoutés pour le Diagnostic

J'ai ajouté des logs détaillés dans plusieurs fichiers pour identifier le problème:

### 1. LoginActivity
```kotlin
// Logs lors du login
android.util.Log.d("LoginActivity", "Login réussi - Token: ${response.token.take(20)}...")
android.util.Log.d("LoginActivity", "UserId: ${response.userId}, Email: ${response.adresseEmail}")
android.util.Log.d("LoginActivity", "Token sauvegardé: ${savedToken?.take(20)}...")
```

### 2. ProfileActivity
```kotlin
// Logs lors du chargement du profil
android.util.Log.d("ProfileActivity", "Token disponible: ${token != null}")
android.util.Log.d("ProfileActivity", "Token: ${token.take(20)}...")
```

### 3. ProfileViewModel
```kotlin
// Logs dans le ViewModel
android.util.Log.d("ProfileViewModel", "Chargement du profil...")
android.util.Log.d("ProfileViewModel", "Profil chargé: ${userData.nom} ${userData.prenom}")
android.util.Log.e("ProfileViewModel", "Erreur: ${exception.message}", exception)
```

### 4. UserRepository
```kotlin
// Logs dans le Repository
android.util.Log.d("UserRepository", "Appel API getProfile...")
android.util.Log.d("UserRepository", "Profil reçu: ${user.nom} ${user.prenom}")
android.util.Log.e("UserRepository", "Erreur getProfile: ${e.message}", e)
```

### 5. AuthInterceptor
```kotlin
// Logs détaillés de l'interceptor
Log.d("AuthInterceptor", "Token ajouté pour ${chain.request().url}")
Log.d("AuthInterceptor", "Token: ${token.take(20)}...")
Log.d("AuthInterceptor", "Requête: ${request.method} ${request.url}")
Log.d("AuthInterceptor", "Réponse: ${response.code} pour ${request.url}")
Log.e("AuthInterceptor", "Corps de l'erreur: $errorBody")
```

---

## 📱 Comment Déboguer

### Étape 1: Ouvrir Logcat dans Android Studio
1. Cliquez sur l'onglet "Logcat" en bas
2. Sélectionnez votre appareil/émulateur
3. Filtrez par "com.example.projetintegration"

### Étape 2: Tester le Login
1. Lancez l'application
2. Connectez-vous avec vos identifiants
3. Regardez les logs dans Logcat

**Logs attendus:**
```
D/LoginActivity: Login réussi - Token: eyJhbGciOiJIUzI1NiIs...
D/LoginActivity: UserId: 1, Email: user@example.com
D/LoginActivity: Token sauvegardé: eyJhbGciOiJIUzI1NiIs...
```

### Étape 3: Accéder au Profil
1. Cliquez sur "Profil" dans le dashboard
2. Regardez les logs

**Logs attendus:**
```
D/ProfileActivity: Token disponible: true
D/ProfileActivity: Token: eyJhbGciOiJIUzI1NiIs...
D/ProfileViewModel: Chargement du profil...
D/UserRepository: Appel API getProfile...
D/AuthInterceptor: Token ajouté pour http://10.0.2.2:8091/api/user/profile
D/AuthInterceptor: Requête: GET http://10.0.2.2:8091/api/user/profile
D/AuthInterceptor: Réponse: 200 pour http://10.0.2.2:8091/api/user/profile
D/UserRepository: Profil reçu: Doe John
D/ProfileViewModel: Profil chargé: Doe John
```

---

## 🚨 Scénarios d'Erreur Possibles

### Scénario 1: Token Non Sauvegardé
**Logs:**
```
D/ProfileActivity: Token disponible: false
W/AuthInterceptor: Aucun token disponible pour http://10.0.2.2:8091/api/user/profile
E/AuthInterceptor: Erreur d'authentification (401)
```

**Solution:**
- Vérifier que `saveAuthData()` est bien appelé après le login
- Vérifier que PreferencesManager sauvegarde correctement le token

### Scénario 2: Token Invalide ou Expiré
**Logs:**
```
D/ProfileActivity: Token disponible: true
D/AuthInterceptor: Token ajouté pour http://10.0.2.2:8091/api/user/profile
E/AuthInterceptor: Erreur d'authentification (403)
E/AuthInterceptor: Corps de l'erreur: {"timestamp":"...","status":403,"error":"Forbidden",...}
```

**Solution:**
- Se reconnecter pour obtenir un nouveau token
- Vérifier que le token n'est pas expiré côté backend

### Scénario 3: Backend Non Accessible
**Logs:**
```
D/UserRepository: Appel API getProfile...
E/UserRepository: Erreur getProfile: Failed to connect to /10.0.2.2:8091
```

**Solution:**
- Vérifier que le backend est démarré sur le port 8091
- Vérifier l'URL dans RetrofitClient: `http://10.0.2.2:8091/`
- Pour un appareil physique, utiliser l'IP de votre machine au lieu de 10.0.2.2

### Scénario 4: Endpoint Incorrect
**Logs:**
```
D/AuthInterceptor: Requête: GET http://10.0.2.2:8091/api/user/profile
E/AuthInterceptor: Erreur d'authentification (404)
```

**Solution:**
- Vérifier que l'endpoint `/api/user/profile` existe dans le backend
- Vérifier que le contrôleur UserController est bien configuré

### Scénario 5: Erreur de Parsing JSON
**Logs:**
```
D/AuthInterceptor: Réponse: 200 pour http://10.0.2.2:8091/api/user/profile
E/UserRepository: Erreur getProfile: Expected BEGIN_OBJECT but was STRING
```

**Solution:**
- Vérifier que le backend renvoie bien un objet User en JSON
- Vérifier que les champs du modèle User correspondent au JSON du backend

---

## 🔧 Vérifications à Faire

### 1. Vérifier le Backend
```bash
# Tester l'endpoint avec curl
curl -X GET http://localhost:8091/api/user/profile \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

**Réponse attendue:**
```json
{
  "id": 1,
  "nom": "Doe",
  "prenom": "John",
  "adresseEmail": "john@example.com",
  "numTel": "0123456789",
  "dateNaissance": "1990-01-01",
  "taille": 1.75,
  "poids": 75.0,
  "sexe": "HOMME",
  "objectif": "PERTE_POIDS",
  "niveauActivite": "MODERE",
  "imc": 24.49,
  "age": 34
}
```

### 2. Vérifier le Token
Dans Logcat, copiez le token et décodez-le sur https://jwt.io

**Vérifier:**
- Le token n'est pas expiré (champ `exp`)
- Le token contient bien le `userId`
- Le token est signé avec la bonne clé

### 3. Vérifier PreferencesManager
Ajoutez un log temporaire:
```kotlin
// Dans ProfileActivity.onCreate()
val token = preferencesManager.getToken()
val userId = preferencesManager.getUserId()
android.util.Log.d("ProfileActivity", "Token: $token")
android.util.Log.d("ProfileActivity", "UserId: $userId")
```

---

## 📊 Checklist de Débogage

- [ ] Le backend est démarré et accessible
- [ ] Le login fonctionne et retourne un token
- [ ] Le token est sauvegardé dans PreferencesManager
- [ ] Le token est ajouté dans les headers par AuthInterceptor
- [ ] L'endpoint `/api/user/profile` existe dans le backend
- [ ] Le backend renvoie un code 200
- [ ] Le JSON de réponse correspond au modèle User
- [ ] Les logs montrent le profil chargé avec succès

---

## 🎯 Prochaines Étapes

1. **Lancez l'application** avec les nouveaux logs
2. **Connectez-vous** et notez les logs du login
3. **Accédez au profil** et notez les logs
4. **Copiez les logs** et partagez-les pour analyse

### Format des Logs à Partager
```
=== LOGIN ===
[Coller les logs du LoginActivity]

=== PROFILE ===
[Coller les logs du ProfileActivity, ProfileViewModel, UserRepository, AuthInterceptor]
```

---

## 💡 Solutions Rapides

### Si le token n'est pas sauvegardé:
```kotlin
// Vérifier dans PreferencesManager.kt
fun saveAuthToken(token: String) {
    sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    android.util.Log.d("PreferencesManager", "Token sauvegardé: ${token.take(20)}...")
}
```

### Si le backend n'est pas accessible depuis l'émulateur:
```kotlin
// Dans RetrofitClient.kt, changer l'URL
private const val BASE_URL = "http://10.0.2.2:8091/"  // Pour émulateur
// OU
private const val BASE_URL = "http://192.168.1.X:8091/"  // Pour appareil physique (remplacer X)
```

### Si l'erreur est 403 Forbidden:
Vérifier dans le backend que l'endpoint est bien protégé et accessible:
```java
@GetMapping("/profile")
public ResponseEntity<User> getProfile() {
    Long userId = SecurityUtils.getCurrentUserId();
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(user);
}
```

---

## 📝 Notes Importantes

1. **10.0.2.2** est l'adresse spéciale pour accéder à localhost depuis l'émulateur Android
2. Pour un **appareil physique**, utilisez l'IP de votre machine (ex: 192.168.1.10)
3. Le **token JWT** expire généralement après 24h (vérifier la config backend)
4. L'**AuthInterceptor** doit être ajouté **avant** le LoggingInterceptor pour voir les headers

---

Avec ces logs détaillés, vous pourrez identifier exactement où le problème se situe!
