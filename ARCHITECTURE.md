# 🏗️ Architecture de l'Application FitLife

## 📐 Pattern Architecture: MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────────────┐
│                        View Layer                        │
│  (Activities + XML Layouts + ViewBinding)               │
│  - LoginActivity                                         │
│  - SignupActivity                                        │
│  - DashboardActivity                                     │
└────────────────────┬────────────────────────────────────┘
                     │ observe LiveData
                     ↓
┌─────────────────────────────────────────────────────────┐
│                    ViewModel Layer                       │
│  (Business Logic + State Management)                    │
│  - LoginViewModel                                        │
│  - SignupViewModel                                       │
└────────────────────┬────────────────────────────────────┘
                     │ calls
                     ↓
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│  (Data Source Abstraction)                              │
│  - AuthRepository                                        │
└────────────────────┬────────────────────────────────────┘
                     │ uses
                     ↓
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                           │
│  - API Service (Retrofit)                               │
│  - Models (Request/Response)                            │
│  - PreferencesManager (Local Storage)                   │
└─────────────────────────────────────────────────────────┘
```

## 📦 Composants Principaux

### 1. View Layer (UI)

#### Activities
- **LoginActivity**: Gère l'interface de connexion
- **SignupActivity**: Gère l'interface d'inscription
- **DashboardActivity**: Affiche les informations utilisateur
- **MainActivity**: Point d'entrée, redirige selon l'état de connexion

#### Responsabilités
- Afficher l'interface utilisateur
- Capturer les interactions utilisateur
- Observer les LiveData du ViewModel
- Afficher les erreurs et messages

### 2. ViewModel Layer

#### LoginViewModel
```kotlin
- emailError: LiveData<String?>
- passwordError: LiveData<String?>
- isLoading: LiveData<Boolean>
- loginResult: LiveData<Result<AuthenticationResponse>>
- login(email, password)
```

#### SignupViewModel
```kotlin
- nomError, prenomError, phoneError, etc.: LiveData<String?>
- isLoading: LiveData<Boolean>
- signupResult: LiveData<Result<AuthenticationResponse>>
- signup(nom, prenom, phone, email, password, date)
```

#### Responsabilités
- Validation des données
- Gestion de l'état de l'UI
- Appel au Repository
- Exposition des données via LiveData

### 3. Repository Layer

#### AuthRepository
```kotlin
- inscription(request): Result<AuthenticationResponse>
- authentification(request): Result<AuthenticationResponse>
```

#### Responsabilités
- Abstraction de la source de données
- Gestion des appels API
- Transformation des erreurs
- Parsing des réponses

### 4. Data Layer

#### API Service (Retrofit)
```kotlin
interface AuthApiService {
    @POST("api/auth/inscription")
    suspend fun inscription(@Body request: InscriptionRequest)
    
    @POST("api/auth/authentification")
    suspend fun authentification(@Body request: AuthenticationRequest)
}
```

#### Models
- **InscriptionRequest**: Données d'inscription
- **AuthenticationRequest**: Données de connexion
- **AuthenticationResponse**: Réponse avec token JWT
- **MessageResponse**: Messages d'erreur

#### PreferencesManager
```kotlin
- saveAuthData(token, userId, email, nom, prenom)
- getToken(): String?
- isLoggedIn(): Boolean
- clearAuthData()
```

## 🔄 Flux de Données

### Inscription
```
1. User remplit le formulaire
   ↓
2. SignupActivity capture les données
   ↓
3. SignupViewModel valide les données
   ↓
4. Si valide → AuthRepository.inscription()
   ↓
5. Retrofit envoie la requête HTTP
   ↓
6. Backend répond (succès/erreur)
   ↓
7. Repository parse la réponse
   ↓
8. ViewModel met à jour LiveData
   ↓
9. Activity observe et réagit
   ↓
10. Si succès → PreferencesManager stocke le token
    ↓
11. Navigation vers Dashboard
```

### Connexion
```
1. User entre email/password
   ↓
2. LoginActivity capture les données
   ↓
3. LoginViewModel valide
   ↓
4. AuthRepository.authentification()
   ↓
5. Retrofit → Backend
   ↓
6. Réponse parsée
   ↓
7. Token stocké
   ↓
8. Navigation vers Dashboard
```

## 🛠️ Technologies Utilisées

### Networking
- **Retrofit**: Client HTTP type-safe
- **Gson**: Parsing JSON
- **OkHttp**: Client HTTP sous-jacent
- **Logging Interceptor**: Debug des requêtes

### Asynchrone
- **Kotlin Coroutines**: Gestion asynchrone
- **suspend functions**: Fonctions suspendables
- **viewModelScope**: Scope lié au ViewModel

### Architecture Components
- **ViewModel**: Survit aux changements de configuration
- **LiveData**: Observable data holder
- **ViewBinding**: Liaison type-safe des vues

### Storage
- **SharedPreferences**: Stockage local du token

### UI
- **Material Components**: Design moderne
- **ConstraintLayout**: Layouts flexibles
- **ScrollView**: Défilement du contenu

## 🎯 Principes de Design

### 1. Separation of Concerns
Chaque couche a une responsabilité unique et bien définie.

### 2. Single Source of Truth
Le ViewModel est la source unique de vérité pour l'état de l'UI.

### 3. Unidirectional Data Flow
Les données circulent dans une seule direction: View → ViewModel → Repository → API

### 4. Reactive Programming
L'UI réagit automatiquement aux changements de données via LiveData.

### 5. Testability
Chaque couche peut être testée indépendamment.

## 🔐 Gestion de la Sécurité

### Token JWT
```
1. Reçu après login/signup
2. Stocké dans SharedPreferences
3. Utilisé pour les requêtes authentifiées
4. Supprimé lors de la déconnexion
```

### Validation
```
Client-side (ValidationUtils):
- Format email
- Format téléphone
- Longueur mot de passe
- Date valide

Server-side (Backend):
- Validation complète
- Unicité email/téléphone
- Hachage mot de passe
```

## 📊 Gestion des Erreurs

### Niveaux d'erreur
1. **Validation UI**: Affichage immédiat sous les champs
2. **Erreur réseau**: Toast avec message
3. **Erreur serveur**: Toast avec message du backend
4. **Erreur inconnue**: Message générique

### Flow d'erreur
```
try {
    API Call
} catch (NetworkException) {
    "Erreur réseau"
} catch (HttpException) {
    Parse error body → Message du backend
} catch (Exception) {
    "Erreur inconnue"
}
```

## 🚀 Optimisations

### Performance
- ViewBinding (pas de findViewById)
- Coroutines (pas de threads manuels)
- LiveData (pas de memory leaks)

### UX
- Validation en temps réel
- Loading states
- Messages d'erreur clairs
- Désactivation des boutons pendant le chargement

### Maintenance
- Code modulaire
- Séparation des responsabilités
- Nommage clair
- Documentation

## 📈 Évolutions Possibles

### Architecture
- [ ] Ajouter Room pour cache local
- [ ] Implémenter Repository Pattern complet
- [ ] Ajouter UseCase layer
- [ ] Migration vers Jetpack Compose

### Features
- [ ] Refresh token automatique
- [ ] Interceptor pour ajouter le token
- [ ] Gestion du mode hors ligne
- [ ] Synchronisation des données

### Tests
- [ ] Unit tests (ViewModels)
- [ ] Integration tests (Repository)
- [ ] UI tests (Espresso)
- [ ] Mock server (MockWebServer)
