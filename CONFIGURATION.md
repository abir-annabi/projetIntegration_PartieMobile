# 🔧 Guide de Configuration - FitLife Android

## 📡 Configuration de l'URL du Backend

### Étape 1: Localiser le fichier

Ouvrez le fichier: `app/src/main/java/com/example/projetintegration/data/api/RetrofitClient.kt`

### Étape 2: Modifier l'URL selon votre environnement

#### Option A: Émulateur Android (par défaut)
```kotlin
private const val BASE_URL = "http://10.0.2.2:8081/"
```
✅ Utilisez cette configuration si vous testez sur l'émulateur Android Studio

#### Option B: Appareil physique
```kotlin
private const val BASE_URL = "http://192.168.X.X:8081/"
```
⚠️ Remplacez `192.168.X.X` par votre adresse IP locale

### Étape 3: Trouver votre IP locale

#### Windows
```cmd
ipconfig
```
Cherchez la ligne "Adresse IPv4" sous votre connexion WiFi/Ethernet

#### Mac
```bash
ifconfig | grep "inet "
```

#### Linux
```bash
hostname -I
```

### Exemple complet

Si votre IP est `192.168.1.105`:
```kotlin
private const val BASE_URL = "http://192.168.1.105:8081/"
```

## ✅ Vérification

### 1. Backend accessible
Testez depuis votre navigateur ou terminal:
```bash
curl http://VOTRE_IP:8081/api/auth/authentification
```

### 2. Même réseau WiFi
- Votre ordinateur (backend) et votre téléphone doivent être sur le même réseau WiFi
- Désactivez les VPN si nécessaire

### 3. Firewall
Assurez-vous que le port 8081 est autorisé dans votre firewall

## 🎨 Personnalisation du thème

### Modifier les couleurs

Fichier: `app/src/main/res/values/colors.xml`

```xml
<color name="fitness_primary">#FF6B35</color>        <!-- Couleur principale -->
<color name="fitness_accent">#FFA726</color>         <!-- Couleur d'accent -->
<color name="fitness_background">#1A1A1A</color>    <!-- Fond -->
```

### Modifier les textes

Fichier: `app/src/main/res/values/strings.xml`

```xml
<string name="app_name">FitLife</string>
<string name="login_title">Bienvenue</string>
<!-- etc. -->
```

## 🔐 Configuration de sécurité

### Production

Avant de publier l'application, modifiez `AndroidManifest.xml`:

```xml
<!-- RETIRER cette ligne en production -->
android:usesCleartextTraffic="true"
```

Et utilisez HTTPS:
```kotlin
private const val BASE_URL = "https://votre-domaine.com/"
```

## 📱 Tests

### Test sur émulateur
1. Lancez le backend: `./gradlew bootRun` (ou depuis votre IDE)
2. Vérifiez que le backend écoute sur le port 8081
3. Lancez l'émulateur Android
4. Installez et lancez l'application

### Test sur appareil physique
1. Trouvez votre IP locale
2. Modifiez `BASE_URL` dans `RetrofitClient.kt`
3. Connectez votre téléphone en USB ou utilisez le WiFi debugging
4. Installez et lancez l'application

## 🐛 Problèmes courants

### "Unable to resolve host"
- Vérifiez l'URL dans `RetrofitClient.kt`
- Vérifiez que le backend est lancé
- Vérifiez la connexion réseau

### "Connection refused"
- Le backend n'est pas lancé
- Mauvais port (vérifiez 8081)
- Firewall bloque la connexion

### "Cette adresse email est déjà utilisée"
- C'est normal ! L'email existe déjà dans la base de données
- Utilisez un autre email ou connectez-vous

### Validation échoue
- Email: doit être un format valide (ex: user@example.com)
- Téléphone: format français 06XXXXXXXX ou 07XXXXXXXX
- Mot de passe: minimum 6 caractères
- Date: doit être dans le passé

## 📊 Logs de débogage

Les logs Retrofit sont activés. Consultez Logcat dans Android Studio:

```
Filtre: "OkHttp"
```

Vous verrez:
- Les requêtes HTTP envoyées
- Les réponses reçues
- Les erreurs réseau

## 🚀 Build de production

### Générer un APK
```bash
./gradlew assembleRelease
```

### Générer un AAB (Google Play)
```bash
./gradlew bundleRelease
```

L'APK/AAB sera dans: `app/build/outputs/`
