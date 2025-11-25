# 🚀 Guide de Démarrage Rapide - FitLife

## ⚡ Démarrage en 5 minutes

### Prérequis
- ✅ Android Studio installé
- ✅ Backend Spring Boot lancé sur le port 8081
- ✅ Émulateur Android ou appareil physique

### Étape 1: Ouvrir le projet
```bash
# Ouvrez Android Studio
# File → Open → Sélectionnez le dossier du projet
```

### Étape 2: Synchroniser Gradle
```
Android Studio va automatiquement synchroniser les dépendances
Attendez que "Gradle sync" se termine
```

### Étape 3: Configurer l'URL du backend

**Pour émulateur (par défaut):**
Rien à faire ! L'URL est déjà configurée sur `http://10.0.2.2:8081/`

**Pour appareil physique:**
1. Trouvez votre IP locale:
   ```cmd
   ipconfig  # Windows
   ```
2. Modifiez `app/src/main/java/com/example/projetintegration/data/api/RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://VOTRE_IP:8081/"
   ```

### Étape 4: Lancer l'application
1. Sélectionnez votre émulateur ou appareil
2. Cliquez sur le bouton ▶️ Run
3. Attendez l'installation

### Étape 5: Tester l'application

#### Test d'inscription
1. Sur l'écran de login, cliquez sur "S'inscrire"
2. Remplissez le formulaire:
   - **Nom**: Dupont
   - **Prénom**: Jean
   - **Téléphone**: 0612345678
   - **Email**: jean.dupont@test.com
   - **Mot de passe**: test123
   - **Date**: Cliquez et sélectionnez une date
3. Cliquez sur "S'inscrire"
4. ✅ Vous devriez voir le Dashboard

#### Test de connexion
1. Déconnectez-vous
2. Sur l'écran de login:
   - **Email**: jean.dupont@test.com
   - **Mot de passe**: test123
3. Cliquez sur "Se connecter"
4. ✅ Vous devriez voir le Dashboard

## 🎨 Aperçu des écrans

### 1. Écran de Login
```
┌─────────────────────────┐
│          💪             │
│      Bienvenue          │
│ Connectez-vous pour...  │
│                         │
│  ┌─────────────────┐   │
│  │ Email           │   │
│  └─────────────────┘   │
│                         │
│  ┌─────────────────┐   │
│  │ Mot de passe    │   │
│  └─────────────────┘   │
│                         │
│  ┌─────────────────┐   │
│  │ Se connecter    │   │
│  └─────────────────┘   │
│                         │
│ Pas de compte? S'inscrire│
└─────────────────────────┘
```

### 2. Écran d'Inscription
```
┌─────────────────────────┐
│          🏋️             │
│   Créer un compte       │
│ Rejoignez la communauté │
│                         │
│  ┌─────────────────┐   │
│  │ Nom             │   │
│  └─────────────────┘   │
│  ┌─────────────────┐   │
│  │ Prénom          │   │
│  └─────────────────┘   │
│  ┌─────────────────┐   │
│  │ Téléphone       │   │
│  └─────────────────┘   │
│  ┌─────────────────┐   │
│  │ Email           │   │
│  └─────────────────┘   │
│  ┌─────────────────┐   │
│  │ Mot de passe    │   │
│  └─────────────────┘   │
│  ┌─────────────────┐   │
│  │ Date naissance  │   │
│  └─────────────────┘   │
│                         │
│  ┌─────────────────┐   │
│  │ S'inscrire      │   │
│  └─────────────────┘   │
│                         │
│ Déjà un compte? Se connecter│
└─────────────────────────┘
```

### 3. Dashboard
```
┌─────────────────────────┐
│  Bienvenue, Jean !      │
│                         │
│          ✅             │
│                         │
│  ┌─────────────────┐   │
│  │ Jean Dupont     │   │
│  │ jean@test.com   │   │
│  └─────────────────┘   │
│                         │
│  ┌─────────────────┐   │
│  │ Déconnexion     │   │
│  └─────────────────┘   │
└─────────────────────────┘
```

## 🧪 Tests de Validation

### Test 1: Email invalide
1. Entrez "test" dans le champ email
2. Cliquez sur "Se connecter"
3. ✅ Message d'erreur: "Email invalide"

### Test 2: Téléphone invalide
1. Sur l'inscription, entrez "123" dans téléphone
2. Cliquez sur "S'inscrire"
3. ✅ Message d'erreur: "Numéro de téléphone invalide"

### Test 3: Mot de passe court
1. Entrez "123" dans mot de passe
2. Cliquez sur "Se connecter"
3. ✅ Message d'erreur: "Le mot de passe doit contenir au moins 6 caractères"

### Test 4: Champs vides
1. Laissez tous les champs vides
2. Cliquez sur "Se connecter"
3. ✅ Messages d'erreur sur tous les champs

### Test 5: Email déjà utilisé
1. Inscrivez-vous avec un email
2. Déconnectez-vous
3. Essayez de vous inscrire avec le même email
4. ✅ Message: "Cette adresse email est déjà utilisée"

## 🐛 Résolution de problèmes

### Problème: "Unable to resolve host"
**Solution:**
1. Vérifiez que le backend est lancé
2. Testez l'URL dans un navigateur: `http://localhost:8081`
3. Pour émulateur, utilisez `10.0.2.2` au lieu de `localhost`

### Problème: "Connection refused"
**Solution:**
1. Le backend n'est pas lancé → Lancez-le
2. Mauvais port → Vérifiez le port 8081
3. Firewall → Autorisez le port 8081

### Problème: Gradle sync failed
**Solution:**
1. File → Invalidate Caches → Invalidate and Restart
2. Vérifiez votre connexion Internet
3. Supprimez le dossier `.gradle` et resynchronisez

### Problème: App crash au démarrage
**Solution:**
1. Vérifiez Logcat pour voir l'erreur
2. Clean Project: Build → Clean Project
3. Rebuild Project: Build → Rebuild Project

## 📱 Raccourcis Android Studio

- **Run**: Shift + F10
- **Debug**: Shift + F9
- **Logcat**: Alt + 6
- **Build**: Ctrl + F9
- **Clean Project**: Build → Clean Project

## 🔍 Vérifier les logs

### Ouvrir Logcat
1. Cliquez sur l'onglet "Logcat" en bas
2. Filtrez par "OkHttp" pour voir les requêtes HTTP
3. Filtrez par "Error" pour voir les erreurs

### Logs importants
```
# Requête HTTP envoyée
--> POST http://10.0.2.2:8081/api/auth/authentification

# Réponse reçue
<-- 200 OK

# Erreur réseau
Unable to resolve host

# Erreur serveur
HTTP 400 Bad Request
```

## 📊 Checklist de vérification

Avant de tester, vérifiez:
- [ ] Backend lancé sur port 8081
- [ ] URL correcte dans RetrofitClient.kt
- [ ] Permissions Internet dans AndroidManifest.xml
- [ ] Gradle sync terminé
- [ ] Émulateur/appareil connecté
- [ ] Même réseau WiFi (pour appareil physique)

## 🎯 Prochaines étapes

Une fois l'application fonctionnelle:
1. ✅ Testez tous les scénarios de validation
2. ✅ Testez la persistance (fermez et rouvrez l'app)
3. ✅ Testez la déconnexion
4. ✅ Personnalisez les couleurs et textes
5. ✅ Ajoutez vos propres fonctionnalités

## 📚 Documentation complète

- **README_FRONTEND.md**: Vue d'ensemble complète
- **CONFIGURATION.md**: Guide de configuration détaillé
- **ARCHITECTURE.md**: Architecture technique
- **Ce fichier**: Guide de démarrage rapide

## 💡 Conseils

1. **Utilisez l'émulateur** pour les premiers tests (plus simple)
2. **Consultez Logcat** en cas de problème
3. **Testez d'abord avec Postman** pour vérifier le backend
4. **Gardez le backend lancé** pendant les tests
5. **Utilisez des emails différents** pour chaque test d'inscription

## 🎉 Félicitations !

Vous avez maintenant une application Android complète avec:
- ✅ Authentification JWT
- ✅ Validation des formulaires
- ✅ Design moderne fitness
- ✅ Gestion des erreurs
- ✅ Architecture MVVM

Bon développement ! 💪🏋️
