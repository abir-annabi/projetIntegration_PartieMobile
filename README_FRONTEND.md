# FitLife - Application Android Fitness

Application Android avec authentification complète (Login/Signup) connectée au backend Spring Boot.

## 🎨 Fonctionnalités

- ✅ Interface de connexion moderne avec thème fitness
- ✅ Interface d'inscription complète
- ✅ Validation en temps réel des champs de saisie
- ✅ Gestion des erreurs avec messages personnalisés
- ✅ Stockage sécurisé du token JWT
- ✅ Dashboard utilisateur
- ✅ Déconnexion
- ✅ Design responsive avec ScrollView

## 🎯 Contrôles de saisie implémentés

### Email
- Format email valide
- Champ obligatoire

### Mot de passe
- Minimum 6 caractères
- Champ obligatoire

### Téléphone
- Format français: 06XXXXXXXX ou 07XXXXXXXX
- Validation regex
- Champ obligatoire

### Date de naissance
- Sélecteur de date (DatePicker)
- Date dans le passé uniquement
- Format: YYYY-MM-DD pour l'API

### Nom et Prénom
- Champs obligatoires
- Validation de non-vide

## 🚀 Configuration

### 1. Configuration de l'URL du backend

Dans `RetrofitClient.kt`, modifiez l'URL selon votre environnement:

```kotlin
// Pour émulateur Android
private const val BASE_URL = "http://10.0.2.2:8081/"

// Pour appareil physique, remplacez par votre IP locale
private const val BASE_URL = "http://192.168.X.X:8081/"
```

### 2. Trouver votre IP locale

**Windows:**
```cmd
ipconfig
```
Cherchez "Adresse IPv4"

**Mac/Linux:**
```bash
ifconfig
```
Cherchez "inet"

### 3. Lancer le backend

Assurez-vous que votre backend Spring Boot est lancé sur le port 8081.

## 📱 Structure du projet

```
app/src/main/java/com/example/projetintegration/
├── data/
│   ├── api/
│   │   ├── AuthApiService.kt          # Interface Retrofit
│   │   └── RetrofitClient.kt          # Configuration Retrofit
│   ├── models/
│   │   ├── AuthenticationRequest.kt
│   │   ├── AuthenticationResponse.kt
│   │   ├── InscriptionRequest.kt
│   │   └── MessageResponse.kt
│   ├── preferences/
│   │   └── PreferencesManager.kt      # Gestion du token
│   └── repository/
│       └── AuthRepository.kt          # Logique métier
├── ui/
│   ├── activities/
│   │   ├── LoginActivity.kt
│   │   ├── SignupActivity.kt
│   │   └── DashboardActivity.kt
│   └── viewmodel/
│       ├── LoginViewModel.kt
│       └── SignupViewModel.kt
├── utils/
│   └── ValidationUtils.kt             # Utilitaires de validation
└── MainActivity.kt                     # Point d'entrée

app/src/main/res/
├── drawable/
│   ├── bg_button_gradient.xml         # Bouton avec gradient
│   ├── bg_edit_text.xml               # Champ de texte normal
│   └── bg_edit_text_error.xml         # Champ de texte en erreur
├── layout/
│   ├── activity_login.xml
│   ├── activity_signup.xml
│   └── activity_dashboard.xml
└── values/
    ├── colors.xml                      # Thème fitness
    └── strings.xml                     # Textes de l'app
```

## 🎨 Thème Fitness

### Couleurs principales
- **Primary**: #FF6B35 (Orange énergique)
- **Accent**: #FFA726 (Orange clair)
- **Background**: #1A1A1A (Noir profond)
- **Surface**: #2D2D2D (Gris foncé)
- **Text Primary**: #FFFFFF (Blanc)
- **Text Secondary**: #B0B0B0 (Gris clair)

## 🔐 Sécurité

- Token JWT stocké dans SharedPreferences
- Validation côté client avant envoi
- Gestion des erreurs réseau
- Cleartext traffic autorisé pour le développement (à désactiver en production)

## 📝 Utilisation

### 1. Inscription
1. Lancez l'application
2. Cliquez sur "S'inscrire"
3. Remplissez tous les champs
4. Cliquez sur le champ date pour ouvrir le sélecteur
5. Cliquez sur "S'inscrire"

### 2. Connexion
1. Entrez votre email et mot de passe
2. Cliquez sur "Se connecter"

### 3. Dashboard
- Affiche les informations de l'utilisateur
- Bouton de déconnexion

## 🐛 Dépannage

### Erreur de connexion réseau

**Émulateur Android:**
- Utilisez `10.0.2.2` au lieu de `localhost`

**Appareil physique:**
- Assurez-vous d'être sur le même réseau WiFi
- Utilisez votre IP locale (ex: 192.168.1.X)
- Vérifiez que le firewall autorise les connexions

### Backend non accessible

Vérifiez que le backend est lancé:
```bash
curl http://localhost:8081/api/auth/authentification
```

### Erreurs de validation

Les messages d'erreur s'affichent en rouge sous chaque champ:
- Email invalide
- Téléphone invalide (format: 06XXXXXXXX)
- Mot de passe trop court (min 6 caractères)
- Date de naissance invalide

## 📦 Dépendances

- Retrofit 2.9.0 (Appels API)
- Gson (Parsing JSON)
- OkHttp Logging Interceptor (Debug)
- Coroutines (Asynchrone)
- ViewModel & LiveData (Architecture)
- Material Components (UI)
- ViewBinding (Liaison de vues)

## 🚀 Build et Run

1. Ouvrez le projet dans Android Studio
2. Synchronisez Gradle
3. Lancez le backend sur le port 8081
4. Lancez l'application sur émulateur ou appareil physique

## 📸 Captures d'écran

L'application comprend:
- Écran de connexion avec logo fitness 💪
- Écran d'inscription avec logo haltères 🏋️
- Dashboard avec icône de succès ✅
- Design sombre avec accents orange
- Boutons avec gradient
- Champs de texte arrondis
- Messages d'erreur en rouge

## 🔄 Flux de l'application

```
MainActivity (Splash)
    ↓
[Token existe?]
    ↓ Non          ↓ Oui
LoginActivity   DashboardActivity
    ↓
SignupActivity
    ↓
DashboardActivity
```

## 💡 Améliorations futures

- [ ] Mot de passe oublié
- [ ] Validation email par code
- [ ] Biométrie (empreinte digitale)
- [ ] Refresh token automatique
- [ ] Mode hors ligne
- [ ] Animations de transition
- [ ] Splash screen personnalisé
