# 🔧 GUIDE DE RÉSOLUTION - PROBLÈME DE CONNEXION BACKEND

## 🚨 **ERREUR IDENTIFIÉE**
```
SocketTimeoutException: failed to connect to /10.0.2.2 (port 8100) after 59999ms
```

---

## 📋 **CHECKLIST DE DIAGNOSTIC**

### **1. ✅ Vérifier que le Backend est Démarré**

#### **Commandes à exécuter :**
```bash
# Vérifier si le port 8100 est utilisé
netstat -an | grep 8100
# ou
lsof -i :8100

# Démarrer le backend Spring Boot
cd /chemin/vers/backend
./mvnw spring-boot:run
# ou
java -jar target/votre-app.jar
```

#### **Vérification dans les logs backend :**
```
Started Application in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8100 (http)
```

---

### **2. ✅ Tester la Connexion Backend**

#### **Test depuis le navigateur :**
```
http://localhost:8100/api/programmes
```

#### **Test avec curl :**
```bash
curl -X GET http://localhost:8100/api/programmes
```

#### **Réponse attendue :**
```json
[
  {
    "id": 1,
    "nom": "Programme Test",
    "description": "...",
    ...
  }
]
```

---

### **3. ✅ Configuration IP pour Émulateur Android**

#### **IP Correctes selon l'environnement :**

| Environnement | IP à utiliser | Explication |
|---------------|---------------|-------------|
| **Émulateur Android** | `10.0.2.2` | IP spéciale émulateur → localhost |
| **Appareil Physique** | `192.168.x.x` | IP locale de votre machine |
| **Localhost (tests)** | `127.0.0.1` | Boucle locale |

#### **Comment trouver votre IP locale :**
```bash
# Windows
ipconfig

# Mac/Linux
ifconfig
# ou
ip addr show
```

---

### **4. ✅ Solutions par Ordre de Priorité**

#### **SOLUTION 1 - Redémarrer le Backend**
```bash
# Arrêter le processus existant
pkill -f "spring-boot" 
# ou Ctrl+C dans le terminal du backend

# Redémarrer
./mvnw spring-boot:run
```

#### **SOLUTION 2 - Vérifier le Port**
```bash
# Voir qui utilise le port 8100
netstat -tulpn | grep 8100

# Si occupé, tuer le processus
kill -9 <PID>
```

#### **SOLUTION 3 - Changer l'IP dans l'App Android**

**Pour Appareil Physique :**
```kotlin
private const val BASE_URL = "http://192.168.1.100:8100/"  // Votre IP locale
```

**Pour Tests Locaux :**
```kotlin
private const val BASE_URL = "http://127.0.0.1:8100/"
```

#### **SOLUTION 4 - Désactiver le Firewall (Temporaire)**
```bash
# Windows
netsh advfirewall set allprofiles state off

# Mac
sudo pfctl -d

# Linux (Ubuntu)
sudo ufw disable
```

#### **SOLUTION 5 - Changer le Port Backend**

**Dans application.properties :**
```properties
server.port=8080
```

**Dans RetrofitClient.kt :**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

---

## 🔧 **CONFIGURATION RECOMMANDÉE**

### **RetrofitClient.kt Amélioré :**

```kotlin
object RetrofitClient {
    
    // Configuration flexible selon l'environnement
    private const val BASE_URL = when {
        BuildConfig.DEBUG -> "http://10.0.2.2:8100/"  // Émulateur
        else -> "https://votre-api-prod.com/"          // Production
    }
    
    // Alternative avec détection automatique
    private fun getBaseUrl(): String {
        return if (isEmulator()) {
            "http://10.0.2.2:8100/"
        } else {
            "http://192.168.1.100:8100/"  // Remplacer par votre IP
        }
    }
    
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"))
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)  // Retry automatique
        .build()
}
```

---

## 🧪 **TESTS DE VALIDATION**

### **1. Test de Connectivité Basique**
```kotlin
// Dans une Activity de test
private fun testConnection() {
    lifecycleScope.launch {
        try {
            val response = RetrofitClient.programmeApiService.getAllProgrammes()
            Log.d("TEST", "✅ Connexion OK: ${response.size} programmes")
        } catch (e: Exception) {
            Log.e("TEST", "❌ Erreur connexion: ${e.message}")
        }
    }
}
```

### **2. Test avec Timeout Court**
```kotlin
// Pour diagnostiquer rapidement
val testClient = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)  // Timeout court pour test
    .build()
```

---

## 📱 **CONFIGURATION SELON L'ENVIRONNEMENT**

### **Développement Local :**
```kotlin
private const val BASE_URL = "http://10.0.2.2:8100/"
```

### **Tests sur Appareil Physique :**
```kotlin
private const val BASE_URL = "http://192.168.1.100:8100/"  // Votre IP
```

### **Production :**
```kotlin
private const val BASE_URL = "https://api.votre-app.com/"
```

---

## 🎯 **COMMANDES DE DIAGNOSTIC RAPIDE**

```bash
# 1. Vérifier le backend
curl http://localhost:8100/api/programmes

# 2. Vérifier le port
netstat -an | grep 8100

# 3. Ping depuis l'émulateur (adb shell)
adb shell
ping 10.0.2.2

# 4. Vérifier les logs Android
adb logcat | grep "AuthInterceptor\|RetrofitClient"
```

---

## ✅ **SOLUTION IMMÉDIATE**

**1. Redémarrez le backend Spring Boot**
**2. Vérifiez que le port 8100 est libre**
**3. Testez avec curl : `curl http://localhost:8100/api/programmes`**
**4. Si ça marche en curl, le problème vient de l'app Android**
**5. Si ça ne marche pas en curl, le problème vient du backend**

---

## 🚀 **RÉSULTAT ATTENDU**

Après correction, vous devriez voir dans les logs :
```
I/okhttp.OkHttpClient: --> GET http://10.0.2.2:8100/api/programmes
I/okhttp.OkHttpClient: <-- 200 OK (XXXms)
```

Au lieu de :
```
E/AuthInterceptor: Erreur réseau: failed to connect to /10.0.2.2
```