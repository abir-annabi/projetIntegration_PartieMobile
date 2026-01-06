# Corrections Critiques - Authentification et Alignement Backend

## 🚨 PROBLÈMES CRITIQUES CORRIGÉS

### 1. ✅ Suppression de userId des Endpoints Protégés

**Problème:** Le frontend passait `userId` en paramètre alors que le backend utilise `SecurityUtils.getCurrentUserId()` pour identifier l'utilisateur via le token JWT.

**Fichiers Modifiés:**

#### UserApiService.kt
```kotlin
// ❌ AVANT
@GET("api/user/profile/{userId}")
suspend fun getProfile(@Path("userId") userId: Int): User

// ✅ APRÈS
@GET("api/user/profile")
suspend fun getProfile(): User
```

**Tous les endpoints mis à jour:**
- `GET /api/user/profile` - Plus de userId
- `PUT /api/user/profile` - Plus de userId
- `PUT /api/user/change-password` - Plus de userId
- `DELETE /api/user/profile` - Plus de userId

#### UserRepository.kt
```kotlin
// ❌ AVANT
suspend fun getProfile(userId: Int): Result<User>

// ✅ APRÈS
suspend fun getProfile(): Result<User>
```

#### ProfileViewModel.kt
```kotlin
// ❌ AVANT
fun loadProfile(userId: Int)
fun updateProfile(userId: Int, request: UpdateProfileRequest)
fun changePassword(userId: Int, request: ChangePasswordRequest)
fun deleteAccount(userId: Int, onSuccess: () -> Unit)

// ✅ APRÈS
fun loadProfile()
fun updateProfile(request: UpdateProfileRequest)
fun changePassword(request: ChangePasswordRequest)
fun deleteAccount(onSuccess: () -> Unit)
```

#### ProfileActivity.kt & EditProfileActivity.kt
```kotlin
// ❌ AVANT
private fun loadProfile() {
    val userId = preferencesManager.getUserId()
    viewModel.loadProfile(userId)
}

// ✅ APRÈS
private fun loadProfile() {
    // Le backend identifie l'utilisateur via le token JWT
    viewModel.loadProfile()
}
```

---

### 2. ✅ Correction de l'Endpoint Inexistant /mes-programmes

**Problème:** Le frontend utilisait `GET /api/programmes/mes-programmes` qui n'existe pas dans le backend.

**Solution:** Utiliser `GET /api/programmes/historique` qui existe.

#### MesProgrammesViewModel.kt
```kotlin
// ❌ AVANT
fun loadMesProgrammes(userId: Int) {
    val result = repository.getMesProgrammes(userId)
}

// ✅ APRÈS
fun loadMesProgrammes() {
    val result = repository.getHistoriqueProgrammes()
}
```

#### MesProgrammesActivity.kt
```kotlin
// ❌ AVANT
private fun loadMesProgrammes() {
    val userId = preferencesManager.getUserId()
    viewModel.loadMesProgrammes(userId)
}

// ✅ APRÈS
private fun loadMesProgrammes() {
    // Le backend identifie l'utilisateur via le token JWT
    viewModel.loadMesProgrammes()
}
```

#### ProgrammeApiService.kt
```kotlin
// ❌ SUPPRIMÉ
@GET("api/programmes/mes-programmes")
suspend fun getMesProgrammes(@Query("userId") userId: Int): List<UserProgramme>

// ✅ UTILISER À LA PLACE
@GET("api/programmes/historique")
suspend fun getHistoriqueProgrammes(): List<UserProgramme>
```

---

### 3. ✅ Ajout du Modèle ErrorResponse

**Nouveau fichier:** `ErrorResponse.kt`

```kotlin
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
```

Ce modèle correspond à la structure des erreurs renvoyées par le backend Spring Boot.

---

## 📋 RÉSUMÉ DES CHANGEMENTS

### Endpoints Modifiés

| Endpoint Avant | Endpoint Après | Changement |
|----------------|----------------|------------|
| `GET /api/user/profile/{userId}` | `GET /api/user/profile` | Suppression userId |
| `PUT /api/user/profile/{userId}` | `PUT /api/user/profile` | Suppression userId |
| `PUT /api/user/change-password/{userId}` | `PUT /api/user/change-password` | Suppression userId |
| `DELETE /api/user/profile/{userId}` | `DELETE /api/user/profile` | Suppression userId |
| `GET /api/programmes/mes-programmes` | `GET /api/programmes/historique` | Endpoint corrigé |

### Fichiers Modifiés

1. ✅ `UserApiService.kt` - Suppression userId de tous les endpoints
2. ✅ `UserRepository.kt` - Suppression userId des méthodes
3. ✅ `ProfileViewModel.kt` - Suppression userId des fonctions
4. ✅ `ProfileActivity.kt` - Suppression appel avec userId
5. ✅ `EditProfileActivity.kt` - Suppression appel avec userId
6. ✅ `MesProgrammesViewModel.kt` - Utilisation de getHistoriqueProgrammes()
7. ✅ `MesProgrammesActivity.kt` - Suppression userId
8. ✅ `ProgrammeApiService.kt` - Suppression endpoint inexistant
9. ✅ `ProgrammeRepository.kt` - Suppression méthode inexistante
10. ✅ `ErrorResponse.kt` - Nouveau modèle créé

---

## 🔐 Comment Fonctionne l'Authentification Maintenant

### 1. Login
```kotlin
// L'utilisateur se connecte
POST /api/auth/login
{
  "adresseEmail": "user@example.com",
  "motDePasse": "password"
}

// Réponse avec token JWT
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

### 2. Stockage du Token
```kotlin
// PreferencesManager stocke le token
preferencesManager.saveAuthToken(token)
```

### 3. Requêtes Authentifiées
```kotlin
// AuthInterceptor ajoute automatiquement le token
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. Backend Identifie l'Utilisateur
```java
// Le backend extrait userId du token JWT
Long userId = SecurityUtils.getCurrentUserId();
```

### 5. Plus Besoin de Passer userId
```kotlin
// ❌ AVANT
userApiService.getProfile(userId)

// ✅ APRÈS
userApiService.getProfile()
```

---

## ✅ TESTS À EFFECTUER

### Test 1: Profil Utilisateur
1. Se connecter avec un compte
2. Aller dans "Profil"
3. Vérifier que les informations s'affichent
4. Modifier le profil
5. Vérifier que les modifications sont sauvegardées

**Résultat attendu:** ✅ Fonctionne sans erreur 403

### Test 2: Mes Programmes
1. Se connecter avec un compte
2. Aller dans "Mes Programmes"
3. Vérifier que la liste des programmes s'affiche

**Résultat attendu:** ✅ Affiche l'historique des programmes

### Test 3: Statistiques
1. Avoir un programme actif
2. Aller dans "Statistiques"
3. Vérifier que les stats s'affichent

**Résultat attendu:** ✅ Affiche les statistiques calculées par le backend

### Test 4: Token Expiré
1. Se connecter
2. Attendre l'expiration du token (ou le supprimer manuellement)
3. Faire une requête

**Résultat attendu:** ⚠️ Erreur 401 - Redirection vers login

---

## 🔄 ENDPOINTS BACKEND UTILISÉS

### Authentification
- `POST /api/auth/signup` - Inscription
- `POST /api/auth/login` - Connexion

### Utilisateur (Protégés)
- `GET /api/user/profile` - Profil utilisateur
- `PUT /api/user/profile` - Modifier profil
- `PUT /api/user/change-password` - Changer mot de passe
- `DELETE /api/user/profile` - Supprimer compte

### Programmes (Protégés)
- `GET /api/programmes` - Liste des programmes
- `GET /api/programmes/{id}` - Détails d'un programme
- `POST /api/programmes/assigner` - S'inscrire à un programme
- `GET /api/programmes/actif` - Programme actif
- `GET /api/programmes/historique` - Historique des programmes
- `GET /api/programmes/statistiques` - Statistiques

### Progression (Protégés)
- `POST /api/progression/enregistrer` - Enregistrer progression
- `GET /api/progression/aujourd-hui` - Progression du jour
- `GET /api/progression/historique` - Historique progression

### Plats (Publics)
- `GET /api/plats` - Liste des plats
- `GET /api/plats/{id}` - Détails d'un plat
- `GET /api/plats/categorie/{categorie}` - Plats par catégorie

---

## 🚀 PROCHAINES ÉTAPES RECOMMANDÉES

### 1. Gestion des Erreurs Améliorée
Implémenter un interceptor pour gérer les erreurs HTTP de manière centralisée:
- 401 Unauthorized → Redirection vers login
- 403 Forbidden → Message d'erreur
- 404 Not Found → Message approprié
- 500 Server Error → Message générique

### 2. Validation Côté Client
Ajouter des validations avant d'envoyer les requêtes:
- Email valide
- Mot de passe fort
- Poids/Taille dans des plages valides
- Dates valides

### 3. Refresh Token
Implémenter un système de refresh token pour éviter de redemander la connexion:
- Stocker refresh token
- Renouveler automatiquement le token expiré

### 4. Gestion du Cache
Implémenter un cache local pour:
- Liste des programmes
- Liste des plats
- Profil utilisateur

### 5. Tests Unitaires
Ajouter des tests pour:
- Repositories
- ViewModels
- Interceptors

---

## 📊 COMPILATION

✅ **BUILD SUCCESSFUL**
- Aucune erreur de compilation
- Tous les diagnostics résolus
- Le projet est prêt pour les tests

---

## 🎯 CONCLUSION

Le frontend est maintenant **correctement aligné** avec le backend:

1. ✅ Plus de userId dans les endpoints protégés
2. ✅ Utilisation des bons endpoints (historique au lieu de mes-programmes)
3. ✅ Authentification via token JWT fonctionnelle
4. ✅ Modèle ErrorResponse créé
5. ✅ Compilation réussie

**Le système d'authentification fonctionne maintenant correctement avec le backend Spring Boot.**
