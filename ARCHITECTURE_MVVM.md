# Architecture MVVM - FitLife App

## 📐 Structure de l'Architecture

L'application suit le pattern **MVVM (Model-View-ViewModel)** pour une séparation claire des responsabilités.

```
┌─────────────────────────────────────────────────────────┐
│                        VIEW                              │
│                    (Activities)                          │
│  - PlatsActivity                                         │
│  - PlatDetailActivity                                    │
│  - ProgrammesActivity                                    │
│  - ProgrammeDetailActivity                               │
│  - MesProgrammesActivity                                 │
│  - LoginActivity                                         │
│  - SignupActivity                                        │
│  - DashboardActivity                                     │
└────────────────┬────────────────────────────────────────┘
                 │ observe LiveData
                 ▼
┌─────────────────────────────────────────────────────────┐
│                     VIEWMODEL                            │
│  - PlatViewModel                                         │
│  - PlatDetailViewModel                                   │
│  - ProgrammeViewModel                                    │
│  - ProgrammeDetailViewModel                              │
│  - MesProgrammesViewModel                                │
│  - LoginViewModel                                        │
│  - SignupViewModel                                       │
└────────────────┬────────────────────────────────────────┘
                 │ calls methods
                 ▼
┌─────────────────────────────────────────────────────────┐
│                    REPOSITORY                            │
│  - PlatRepository                                        │
│  - ProgrammeRepository                                   │
│  - AuthRepository                                        │
└────────────────┬────────────────────────────────────────┘
                 │ uses
                 ▼
┌─────────────────────────────────────────────────────────┐
│                   API SERVICE                            │
│  - PlatApiService                                        │
│  - ProgrammeApiService                                   │
│  - AuthApiService                                        │
└────────────────┬────────────────────────────────────────┘
                 │ HTTP requests
                 ▼
┌─────────────────────────────────────────────────────────┐
│                  RETROFIT CLIENT                         │
│  - RetrofitClient (with AuthInterceptor)                │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
              Backend API
```

---

## 📦 Composants par Fonctionnalité

### 1. 🍽️ Plats (Nutrition)

#### Models
- `Plat.kt` - Modèle de données pour un plat

#### API Service
```kotlin
interface PlatApiService {
    @GET("api/plats")
    suspend fun getAllPlats(): List<Plat>
    
    @GET("api/plats/{id}")
    suspend fun getPlatById(@Path("id") id: Int): Plat
    
    @GET("api/plats/categorie/{categorie}")
    suspend fun getPlatsByCategorie(@Path("categorie") categorie: String): List<Plat>
}
```

#### Repository
```kotlin
class PlatRepository {
    suspend fun getAllPlats(): Result<List<Plat>>
    suspend fun getPlatById(id: Int): Result<Plat>
    suspend fun getPlatsByCategorie(categorie: String): Result<List<Plat>>
}
```

#### ViewModels
- **PlatViewModel** - Gère la liste des plats
  - `plats: LiveData<List<Plat>>`
  - `isLoading: LiveData<Boolean>`
  - `error: LiveData<String?>`
  - `loadAllPlats()`
  - `loadPlatsByCategorie(categorie: String)`

- **PlatDetailViewModel** - Gère le détail d'un plat
  - `plat: LiveData<Plat>`
  - `isLoading: LiveData<Boolean>`
  - `error: LiveData<String?>`
  - `loadPlat(platId: Int)`

#### Activities
- **PlatsActivity** - Affiche la liste des plats avec filtres
- **PlatDetailActivity** - Affiche le détail d'un plat (recette)

---

### 2. 💪 Programmes

#### Models
- `Programme.kt` - Modèle de données pour un programme
- `UserProgramme.kt` - Modèle pour l'inscription d'un utilisateur à un programme
- `ActiviteSportive.kt` - Modèle pour une activité sportive

#### API Service
```kotlin
interface ProgrammeApiService {
    @GET("api/programmes")
    suspend fun getAllProgrammes(): List<Programme>
    
    @GET("api/programmes/{id}")
    suspend fun getProgrammeById(@Path("id") id: Int): Programme
    
    @POST("api/programmes/{programmeId}/inscrire/{userId}")
    suspend fun inscrireUserAuProgramme(
        @Path("programmeId") programmeId: Int,
        @Path("userId") userId: Int
    ): UserProgramme
    
    @GET("api/programmes/user/{userId}")
    suspend fun getProgrammesUser(@Path("userId") userId: Int): List<UserProgramme>
    
    @PUT("api/programmes/user-programme/{id}/progression")
    suspend fun updateProgression(
        @Path("id") userProgrammeId: Int,
        @Query("progression") progression: Int
    ): UserProgramme
}
```

#### Repository
```kotlin
class ProgrammeRepository {
    suspend fun getAllProgrammes(): Result<List<Programme>>
    suspend fun getProgrammeById(id: Int): Result<Programme>
    suspend fun inscrireUserAuProgramme(programmeId: Int, userId: Int): Result<UserProgramme>
    suspend fun getProgrammesUser(userId: Int): Result<List<UserProgramme>>
    suspend fun updateProgression(userProgrammeId: Int, progression: Int): Result<UserProgramme>
}
```

#### ViewModels
- **ProgrammeViewModel** - Gère la liste des programmes
  - `programmes: LiveData<List<Programme>>`
  - `isLoading: LiveData<Boolean>`
  - `error: LiveData<String?>`
  - `loadAllProgrammes()`

- **ProgrammeDetailViewModel** - Gère le détail d'un programme
  - `programme: LiveData<Programme>`
  - `isLoading: LiveData<Boolean>`
  - `error: LiveData<String?>`
  - `inscriptionResult: LiveData<Result<UserProgramme>>`
  - `loadProgramme(programmeId: Int)`
  - `inscrireAuProgramme(programmeId: Int, userId: Int)`

- **MesProgrammesViewModel** - Gère les programmes de l'utilisateur
  - `mesProgrammes: LiveData<List<UserProgramme>>`
  - `isLoading: LiveData<Boolean>`
  - `error: LiveData<String?>`
  - `updateSuccess: LiveData<Boolean>`
  - `loadMesProgrammes(userId: Int)`
  - `updateProgression(userProgrammeId: Int, progression: Int)`

#### Activities
- **ProgrammesActivity** - Affiche la liste des programmes
- **ProgrammeDetailActivity** - Affiche le détail d'un programme
- **MesProgrammesActivity** - Affiche les programmes de l'utilisateur avec suivi

---

### 3. 🔐 Authentification

#### Models
- `AuthenticationRequest.kt`
- `AuthenticationResponse.kt`
- `InscriptionRequest.kt`
- `User.kt`

#### API Service
```kotlin
interface AuthApiService {
    @POST("api/auth/connexion")
    suspend fun login(@Body request: AuthenticationRequest): AuthenticationResponse
    
    @POST("api/auth/inscription")
    suspend fun signup(@Body request: InscriptionRequest): AuthenticationResponse
}
```

#### Repository
```kotlin
class AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthenticationResponse>
    suspend fun signup(request: InscriptionRequest): Result<AuthenticationResponse>
}
```

#### ViewModels
- **LoginViewModel**
- **SignupViewModel**

#### Activities
- **LoginActivity**
- **SignupActivity**

---

## 🔄 Flux de Données

### Exemple: Chargement de la liste des plats

```
1. PlatsActivity
   └─> viewModel.loadAllPlats()

2. PlatViewModel
   └─> repository.getAllPlats()

3. PlatRepository
   └─> platApiService.getAllPlats()

4. PlatApiService (Retrofit)
   └─> HTTP GET /api/plats

5. Backend API
   └─> Retourne List<Plat>

6. PlatRepository
   └─> Result.success(plats)

7. PlatViewModel
   └─> _plats.value = platsList

8. PlatsActivity (observe)
   └─> platsAdapter.submitList(plats)
```

---

## 🎯 Avantages de cette Architecture

### ✅ Séparation des Responsabilités
- **View (Activity)**: Affichage UI uniquement
- **ViewModel**: Logique de présentation
- **Repository**: Logique métier et accès aux données
- **API Service**: Communication réseau

### ✅ Testabilité
- Chaque couche peut être testée indépendamment
- Mock facile des repositories dans les ViewModels
- Tests unitaires simplifiés

### ✅ Réutilisabilité
- Les repositories peuvent être utilisés par plusieurs ViewModels
- Les ViewModels survivent aux changements de configuration

### ✅ Gestion d'État
- LiveData pour observer les changements
- Gestion automatique du cycle de vie
- Pas de memory leaks

### ✅ Gestion des Erreurs
- Centralisation dans les repositories
- Propagation via Result<T>
- Affichage cohérent dans les Activities

---

## 📂 Structure des Dossiers

```
app/src/main/java/com/example/projetintegration/
├── data/
│   ├── api/
│   │   ├── AuthApiService.kt
│   │   ├── PlatApiService.kt
│   │   ├── ProgrammeApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── AuthInterceptor.kt
│   ├── models/
│   │   ├── Plat.kt
│   │   ├── Programme.kt
│   │   ├── UserProgramme.kt
│   │   ├── ActiviteSportive.kt
│   │   ├── User.kt
│   │   ├── AuthenticationRequest.kt
│   │   ├── AuthenticationResponse.kt
│   │   └── InscriptionRequest.kt
│   ├── repository/
│   │   ├── PlatRepository.kt
│   │   ├── ProgrammeRepository.kt
│   │   └── AuthRepository.kt
│   └── preferences/
│       └── PreferencesManager.kt
├── ui/
│   ├── activities/
│   │   ├── PlatsActivity.kt
│   │   ├── PlatDetailActivity.kt
│   │   ├── ProgrammesActivity.kt
│   │   ├── ProgrammeDetailActivity.kt
│   │   ├── MesProgrammesActivity.kt
│   │   ├── LoginActivity.kt
│   │   ├── SignupActivity.kt
│   │   └── DashboardActivity.kt
│   ├── adapters/
│   │   ├── PlatsAdapter.kt
│   │   ├── ProgrammesAdapter.kt
│   │   └── MesProgrammesAdapter.kt
│   └── viewmodel/
│       ├── PlatViewModel.kt
│       ├── PlatDetailViewModel.kt
│       ├── ProgrammeViewModel.kt
│       ├── ProgrammeDetailViewModel.kt
│       ├── MesProgrammesViewModel.kt
│       ├── LoginViewModel.kt
│       └── SignupViewModel.kt
├── utils/
└── FitLifeApplication.kt
```

---

## 🔧 Configuration Requise

### Dépendances Gradle

```gradle
// ViewModel et LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.2'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// OkHttp
implementation 'com.squareup.okhttp3:okhttp:4.11.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
```

---

## 🚀 Bonnes Pratiques Implémentées

1. ✅ **Single Responsibility**: Chaque classe a une seule responsabilité
2. ✅ **Dependency Injection**: Les dépendances sont injectées (repositories dans ViewModels)
3. ✅ **Error Handling**: Gestion centralisée des erreurs avec Result<T>
4. ✅ **Coroutines**: Opérations asynchrones avec suspend functions
5. ✅ **LiveData**: Observation réactive des données
6. ✅ **ViewModelScope**: Gestion automatique du cycle de vie des coroutines
7. ✅ **Repository Pattern**: Abstraction de la source de données
8. ✅ **API Interceptor**: Ajout automatique du token JWT

---

**Version**: 1.0  
**Dernière mise à jour**: 2024
