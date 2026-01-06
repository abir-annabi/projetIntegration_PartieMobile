# Test de Connexion Backend

## 🧪 Tests Rapides à Effectuer

### Test 1: Backend Accessible
```bash
# Depuis votre terminal
curl http://localhost:8091/api/plats

# Réponse attendue: Liste des plats (endpoint public)
```

### Test 2: Login Fonctionne
```bash
curl -X POST http://localhost:8091/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "adresseEmail": "votre@email.com",
    "motDePasse": "votreMotDePasse"
  }'

# Réponse attendue:
# {
#   "token": "eyJhbGciOiJIUzI1NiIs...",
#   "userId": 1,
#   "adresseEmail": "votre@email.com",
#   "nom": "Votre Nom",
#   "prenom": "Votre Prénom"
# }
```

### Test 3: Profil Accessible avec Token
```bash
# Remplacez VOTRE_TOKEN par le token obtenu au Test 2
curl -X GET http://localhost:8091/api/user/profile \
  -H "Authorization: Bearer VOTRE_TOKEN"

# Réponse attendue: Objet User complet
```

---

## 🔍 Vérifications dans l'Application

### Vérification 1: Token Sauvegardé
Ajoutez temporairement dans `ProfileActivity.onCreate()`:
```kotlin
val token = preferencesManager.getToken()
Toast.makeText(this, "Token: ${token?.take(20) ?: "NULL"}", Toast.LENGTH_LONG).show()
```

### Vérification 2: Requête Envoyée
Regardez dans Logcat avec le filtre "OkHttp" pour voir toutes les requêtes HTTP.

### Vérification 3: Réponse Reçue
Les logs de `HttpLoggingInterceptor` montreront le corps de la réponse.

---

## 🐛 Problèmes Courants

### Problème: "Failed to connect"
**Cause:** Backend non démarré ou mauvaise URL

**Solution:**
1. Vérifier que le backend Spring Boot est démarré
2. Vérifier les logs du backend pour voir s'il écoute sur le port 8091
3. Pour émulateur: utiliser `10.0.2.2`
4. Pour appareil physique: utiliser l'IP de votre machine

### Problème: "401 Unauthorized"
**Cause:** Token manquant ou invalide

**Solution:**
1. Se reconnecter pour obtenir un nouveau token
2. Vérifier que le token est bien sauvegardé
3. Vérifier que AuthInterceptor ajoute bien le header

### Problème: "403 Forbidden"
**Cause:** Token valide mais utilisateur non autorisé

**Solution:**
1. Vérifier que l'endpoint est bien configuré dans le backend
2. Vérifier que SecurityUtils.getCurrentUserId() fonctionne
3. Vérifier les rôles/permissions de l'utilisateur

### Problème: "404 Not Found"
**Cause:** Endpoint inexistant

**Solution:**
1. Vérifier l'URL de l'endpoint
2. Vérifier que le contrôleur est bien annoté avec @RestController
3. Vérifier le mapping: @GetMapping("/profile")

---

## 📱 Test Complet dans l'Application

1. **Désinstaller l'application** (pour nettoyer les préférences)
2. **Réinstaller** l'application
3. **S'inscrire** avec un nouveau compte
4. **Se connecter** avec ce compte
5. **Aller dans Profil**
6. **Regarder les logs** dans Logcat

---

## 🎯 Logs à Rechercher

Filtrez Logcat avec ces tags:
- `LoginActivity`
- `ProfileActivity`
- `ProfileViewModel`
- `UserRepository`
- `AuthInterceptor`
- `OkHttp`

---

## 💡 Astuce: Activer les Logs Détaillés

Dans `RetrofitClient.kt`, le `HttpLoggingInterceptor` est déjà configuré avec `Level.BODY`.

Cela affichera:
- L'URL complète de la requête
- Les headers (dont Authorization)
- Le corps de la requête
- Le code de réponse
- Les headers de réponse
- Le corps de la réponse

Exemple de log:
```
D/OkHttp: --> GET http://10.0.2.2:8091/api/user/profile
D/OkHttp: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
D/OkHttp: --> END GET
D/OkHttp: <-- 200 OK http://10.0.2.2:8091/api/user/profile (123ms)
D/OkHttp: Content-Type: application/json
D/OkHttp: {"id":1,"nom":"Doe","prenom":"John",...}
D/OkHttp: <-- END HTTP
```
