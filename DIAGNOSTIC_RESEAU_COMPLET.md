# 🔍 DIAGNOSTIC RÉSEAU COMPLET - GUIDE DE RÉSOLUTION

## 🚨 Problème Identifié

```
SocketTimeoutException: failed to connect to /10.0.2.2 (port 8100) from /10.0.2.16 (port 51386) after 59999ms
```

## 📊 Analyse de l'Erreur

### **Type d'Erreur :** `SocketTimeoutException`
- **Signification :** La connexion a été tentée mais a échoué après 60 secondes
- **Cause Probable :** Le serveur backend n'est pas accessible sur le port 8100

### **Adresses Impliquées :**
- **Client (Android)** : `/10.0.2.16:51386` (port dynamique)
- **Serveur (Backend)** : `/10.0.2.2:8100` (port fixe)

---

## 🔧 ÉTAPES DE DIAGNOSTIC

### **ÉTAPE 1 : Vérifier le Backend Spring Boot**

#### ✅ **1.1 - Vérifier que le Backend est Démarré**
```bash
# Dans le terminal du projet backend
./mvnw spring-boot:run

# OU avec Gradle
./gradlew bootRun

# OU avec Java
java -jar target/votre-app.jar
```

#### ✅ **1.2 - Vérifier les Logs de Démarrage**
Recherchez ces messages dans les logs :
```
Started Application in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8100 (http)
```

#### ✅ **1.3 - Vérifier la Configuration du Port**
Dans `application.properties` ou `application.yml` :
```properties
# application.properties
server.port=8100

# OU application.yml
server:
  port: 8100
```

---

### **ÉTAPE 2 : Tests de Connectivité Réseau**

#### ✅ **2.1 - Test Local (Sur la Machine Backend)**
```bash
# Test simple
curl http://localhost:8100/api/auth/test

# Test avec détails
curl -v http://localhost:8100/api/auth/test

# Test de port
telnet localhost 8100
```

#### ✅ **2.2 - Test depuis l'Émulateur Android**
```bash
# Depuis l'émulateur (adb shell)
adb shell
curl http://10.0.2.2:8100/api/auth/test

# Test de connectivité
ping 10.0.2.2
telnet 10.0.2.2 8100
```

#### ✅ **2.3 - Vérifier les Ports Ouverts**
```bash
# Windows
netstat -an | findstr 8100

# Linux/Mac
netstat -an | grep 8100
lsof -i :8100
```

---

### **ÉTAPE 3 : Configuration Réseau**

#### ✅ **3.1 - Vérifier l'IP de l'Émulateur**
```bash
# Dans l'émulateur Android
adb shell ip route show

# Résultat attendu : 10.0.2.2 (gateway vers l'hôte)
```

#### ✅ **3.2 - Tester avec un Appareil Physique**
Si vous utilisez un appareil physique, changez l'IP :
```kotlin
// Dans RetrofitClient.kt
private const val BASE_URL = "http://192.168.1.XXX:8100/"  // IP locale de votre PC
```

#### ✅ **3.3 - Vérifier le Firewall**
```bash
# Windows - Autoriser le port 8100
netsh advfirewall firewall add rule name="Spring Boot 8100" dir=in action=allow protocol=TCP localport=8100

# Linux - UFW
sudo ufw allow 8100

# Mac - Vérifier les préférences système
```

---

### **ÉTAPE 4 : Tests Backend Spécifiques**

#### ✅ **4.1 - Créer un Endpoint de Test**
Ajoutez dans votre contrôleur backend :
```java
@RestController
@RequestMapping("/api")
public class TestController {
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Backend accessible - " + LocalDateTime.now());
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("port", 8100);
        return ResponseEntity.ok(status);
    }
}
```

#### ✅ **4.2 - Tester les Endpoints**
```bash
# Test de base
curl http://localhost:8100/api/test

# Test de santé
curl http://localhost:8100/api/health

# Test depuis l'émulateur
curl http://10.0.2.2:8100/api/test
```

---

### **ÉTAPE 5 : Configuration Android**

#### ✅ **5.1 - Vérifier les Permissions Réseau**
Dans `AndroidManifest.xml` :
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Pour HTTP en clair (développement uniquement) -->
<application
    android:usesCleartextTraffic="true"
    ... >
```

#### ✅ **5.2 - Configuration Réseau de Sécurité**
Créez `res/xml/network_security_config.xml` :
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">192.168.1.0/24</domain>
    </domain-config>
</network-security-config>
```

Et dans `AndroidManifest.xml` :
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```

---

## 🧪 SCRIPT DE DIAGNOSTIC AUTOMATIQUE

### **Script Backend (test-backend.sh)**
```bash
#!/bin/bash
echo "🔍 DIAGNOSTIC BACKEND"
echo "===================="

echo "1. Test port 8100..."
if nc -z localhost 8100; then
    echo "✅ Port 8100 ouvert"
else
    echo "❌ Port 8100 fermé"
fi

echo "2. Test endpoint..."
if curl -s http://localhost:8100/api/test > /dev/null; then
    echo "✅ Backend accessible"
else
    echo "❌ Backend inaccessible"
fi

echo "3. Processus Java..."
ps aux | grep java | grep -v grep
```

### **Script Android (test-android.sh)**
```bash
#!/bin/bash
echo "🔍 DIAGNOSTIC ANDROID"
echo "===================="

echo "1. Test connectivité émulateur..."
adb shell ping -c 3 10.0.2.2

echo "2. Test port depuis émulateur..."
adb shell "echo 'GET /api/test HTTP/1.1\r\nHost: 10.0.2.2:8100\r\n\r\n' | nc 10.0.2.2 8100"

echo "3. Logs réseau Android..."
adb logcat -s "AuthInterceptor" -d
```

---

## 📋 CHECKLIST DE RÉSOLUTION

### **🔴 CRITIQUE (À VÉRIFIER EN PREMIER)**
- [ ] Backend Spring Boot démarré
- [ ] Port 8100 configuré et ouvert
- [ ] Endpoint `/api/test` accessible en local
- [ ] Firewall autorise le port 8100

### **🟠 IMPORTANT**
- [ ] IP correcte dans RetrofitClient (10.0.2.2 pour émulateur)
- [ ] Permissions réseau dans AndroidManifest.xml
- [ ] Configuration réseau de sécurité
- [ ] Tests depuis l'émulateur Android

### **🟡 OPTIONNEL**
- [ ] Logs détaillés activés
- [ ] Tests avec appareil physique
- [ ] Monitoring réseau
- [ ] Tests de performance

---

## 🎯 SOLUTIONS PAR SCÉNARIO

### **Scénario 1 : Backend Non Démarré**
```bash
# Solution
cd /path/to/backend
./mvnw spring-boot:run
```

### **Scénario 2 : Port Incorrect**
```java
// Dans application.properties
server.port=8100
```

### **Scénario 3 : Firewall Bloque**
```bash
# Windows
netsh advfirewall firewall add rule name="Spring Boot" dir=in action=allow protocol=TCP localport=8100
```

### **Scénario 4 : IP Incorrecte**
```kotlin
// Pour émulateur
private const val BASE_URL = "http://10.0.2.2:8100/"

// Pour appareil physique
private const val BASE_URL = "http://192.168.1.XXX:8100/"
```

---

## 📊 MONITORING CONTINU

### **Logs à Surveiller**

#### **Backend (Spring Boot)**
```
INFO  o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8100 (http)
INFO  com.example.Application : Started Application in 3.456 seconds
```

#### **Android (AuthInterceptor)**
```
D/AuthInterceptor: ✅ Requête: GET http://10.0.2.2:8100/api/test
D/AuthInterceptor: ✅ Réponse: 200 pour http://10.0.2.2:8100/api/test
```

### **Métriques de Performance**
- **Temps de connexion** : < 5 secondes
- **Temps de réponse** : < 2 secondes
- **Taux de succès** : > 95%

---

## 🚀 RÉSUMÉ EXÉCUTIF

### **Problème Principal**
Connexion impossible entre l'application Android et le backend Spring Boot sur le port 8100.

### **Cause Probable**
Backend Spring Boot non démarré ou inaccessible.

### **Solution Immédiate**
1. Démarrer le backend : `./mvnw spring-boot:run`
2. Vérifier le port : `netstat -an | grep 8100`
3. Tester : `curl http://localhost:8100/api/test`

### **Prévention**
- Script de démarrage automatique
- Monitoring de santé
- Tests de connectivité réguliers

**Le diagnostic réseau est maintenant complet et actionnable ! 🎯**