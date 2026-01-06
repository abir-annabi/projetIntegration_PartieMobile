# 🔧 Guide de Résolution - Erreur de Connexion Backend

## 🚨 Problème Identifié
```
failed to connect to /10.0.2.2 (port 8086) after 29999ms
```

## 🔍 Étapes de Diagnostic

### 1. Vérifier que le Backend est Démarré

**Sur votre machine (pas l'émulateur) :**

```bash
# Vérifier si quelque chose écoute sur le port 8086
netstat -an | findstr :8086

# Ou avec PowerShell
Get-NetTCPConnection -LocalPort 8086

# Ou tester avec telnet
telnet localhost 8086
```

**Résultat attendu :**
- ✅ Si le backend est démarré : vous verrez une ligne avec `:8086`
- ❌ Si rien : le backend n'est pas démarré

### 2. Démarrer le Backend

**Si vous utilisez Spring Boot :**

```bash
# Avec Maven
./mvnw spring-boot:run

# Avec Gradle
./gradlew bootRun

# Ou directement avec Java
java -jar votre-backend.jar
```

**Vérifiez les logs de démarrage :**
```
Started Application in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8086 (http)
```

### 3. Vérifier le Port dans le Backend

**Fichier `application.properties` ou `application.yml` :**

```properties
# application.properties
server.port=8086
```

```yaml
# application.yml
server:
  port: 8086
```

### 4. Tester la Connexion Directement

**Depuis votre navigateur :**
```
http://localhost:8086
```

**Avec curl :**
```bash
curl http://localhost:8086
curl http://localhost:8086/api/auth/login
```

## 🔧 Solutions Alternatives

### Solution A: Changer le Port dans l'App Android

Si votre backend tourne sur un autre port (ex: 8080, 8091), modifiez :

```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://10.0.2.2:8080/"  // Changez le port
```

### Solution B: Utiliser l'IP de votre Machine

Si `10.0.2.2` ne fonctionne pas, utilisez l'IP réelle :

```bash
# Trouver votre IP
ipconfig
# Cherchez "IPv4 Address" de votre carte réseau active
```

```kotlin
// RetrofitClient.kt
private const val BASE_URL = "http://192.168.1.XXX:8086/"  // Votre IP réelle
```

### Solution C: Tester avec un Serveur Simple

**Créez un serveur de test simple :**

```python
# test_server.py
from http.server import HTTPServer, BaseHTTPRequestHandler
import json

class TestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(b'{"message": "Server is running!"}')
    
    def do_POST(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(b'{"message": "POST received!"}')

if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 8086), TestHandler)
    print("Server running on port 8086...")
    server.serve_forever()
```

```bash
python test_server.py
```

## 🧪 Test avec l'Activité Diagnostic

1. **Ouvrez l'app Android**
2. **Allez au Dashboard**
3. **Cliquez sur "🔧 Diagnostic"**
4. **Analysez les résultats :**
   - 📶 Connexion réseau
   - 🌐 Serveur backend
   - 🔑 Token JWT

## 📋 Checklist de Vérification

### Backend :
- [ ] Backend démarré et accessible sur localhost:8086
- [ ] Logs de démarrage sans erreur
- [ ] Endpoints `/api/auth/login` et `/api/auth/register` disponibles
- [ ] CORS configuré pour accepter les requêtes Android

### Android :
- [ ] Port correct dans RetrofitClient (8086)
- [ ] Permissions INTERNET dans AndroidManifest
- [ ] `usesCleartextTraffic="true"` dans AndroidManifest
- [ ] Émulateur connecté au même réseau

### Réseau :
- [ ] Firewall Windows n'bloque pas le port 8086
- [ ] Antivirus ne bloque pas les connexions
- [ ] Émulateur peut accéder à internet

## 🚀 Actions Immédiates

1. **Vérifiez si le backend tourne :**
   ```bash
   netstat -an | findstr :8086
   ```

2. **Si pas de backend, démarrez-le :**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Testez depuis le navigateur :**
   ```
   http://localhost:8086
   ```

4. **Utilisez l'activité Diagnostic dans l'app**

5. **Si ça ne marche toujours pas, essayez un autre port :**
   ```kotlin
   private const val BASE_URL = "http://10.0.2.2:8080/"
   ```

## 💡 Conseils Supplémentaires

- **Émulateur Android :** `10.0.2.2` = `localhost` de votre machine
- **Appareil physique :** Utilisez l'IP réelle de votre machine
- **Ports communs :** 8080, 8086, 8090, 8091, 3000, 5000

## 🆘 Si Rien ne Marche

1. **Créez un serveur de test simple** (Python ci-dessus)
2. **Testez avec Postman** sur votre machine
3. **Vérifiez les logs détaillés** du backend
4. **Contactez-moi avec les logs complets** du backend