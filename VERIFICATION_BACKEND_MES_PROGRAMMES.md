# 🔍 Vérification Backend - Endpoint "Mes Programmes"

## 🚨 Problème Confirmé
- ✅ Filtre favoris désactivé
- ✅ Utilisateur inscrit à plus de 2 programmes  
- ✅ Application redémarrée
- ❌ **Seulement 2 programmes affichés** → Problème backend confirmé

## 🎯 Endpoint à Vérifier

### **URL**: `GET /api/programmes/historique`
**Description**: Retourne la liste des programmes assignés à l'utilisateur connecté

## 🧪 Tests Backend à Effectuer

### 1. **Test Direct avec cURL/Postman**

```bash
# Remplacez YOUR_JWT_TOKEN par le token de l'utilisateur
curl -X GET "http://localhost:8100/api/programmes/historique" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json"
```

**Résultat attendu**: Liste de TOUS les programmes de l'utilisateur (pas seulement 2)

### 2. **Vérification Base de Données**

```sql
-- 1. Vérifier l'utilisateur connecté
SELECT id, email, nom, prenom FROM users WHERE email = 'email_utilisateur';

-- 2. Vérifier TOUS les programmes assignés à cet utilisateur
SELECT up.id, up.date_debut, up.statut, p.nom as programme_nom 
FROM user_programmes up 
JOIN programmes p ON up.programme_id = p.id 
WHERE up.user_id = USER_ID_FROM_STEP_1;

-- 3. Compter le nombre total
SELECT COUNT(*) as total_programmes 
FROM user_programmes 
WHERE user_id = USER_ID_FROM_STEP_1;
```

### 3. **Vérification Logs Backend**

Cherchez ces logs dans votre serveur backend :

```
GET /api/programmes/historique
User ID from JWT: [user_id]
Found X programmes for user [user_id]
```

## 🔧 Causes Possibles Backend

### 1. **Problème de Pagination** 📄
```java
// ❌ MAUVAIS: Pagination par défaut limitée
@GetMapping("/api/programmes/historique")
public Page<UserProgramme> getHistoriqueProgrammes(Pageable pageable) {
    // Si pageable a une taille par défaut de 2...
}

// ✅ BON: Pas de pagination ou pagination explicite
@GetMapping("/api/programmes/historique")
public List<UserProgramme> getHistoriqueProgrammes() {
    return userProgrammeService.findAllByUserId(getCurrentUserId());
}
```

### 2. **Problème de Filtrage par Statut** 🔍
```java
// ❌ MAUVAIS: Filtre seulement certains statuts
public List<UserProgramme> findByUserId(Long userId) {
    return repository.findByUserIdAndStatut(userId, "ACTIF"); // Seulement les actifs!
}

// ✅ BON: Tous les programmes
public List<UserProgramme> findByUserId(Long userId) {
    return repository.findByUserId(userId); // Tous les statuts
}
```

### 3. **Problème de Décodage JWT** 🔑
```java
// Vérifiez que l'ID utilisateur est correctement extrait du JWT
@GetMapping("/api/programmes/historique")
public List<UserProgramme> getHistoriqueProgrammes(Authentication auth) {
    Long userId = extractUserIdFromJWT(auth);
    System.out.println("User ID from JWT: " + userId); // LOG IMPORTANT
    
    List<UserProgramme> programmes = service.findByUserId(userId);
    System.out.println("Found " + programmes.size() + " programmes"); // LOG IMPORTANT
    
    return programmes;
}
```

### 4. **Problème de Requête JPA** 🗄️
```java
// ❌ MAUVAIS: Requête limitée
@Query("SELECT up FROM UserProgramme up WHERE up.user.id = :userId ORDER BY up.dateDebut DESC")
List<UserProgramme> findByUserId(@Param("userId") Long userId, Pageable pageable);

// ✅ BON: Requête complète
@Query("SELECT up FROM UserProgramme up WHERE up.user.id = :userId ORDER BY up.dateDebut DESC")
List<UserProgramme> findByUserId(@Param("userId") Long userId);
```

## 🔍 Checklist de Vérification Backend

### ✅ À Vérifier dans le Code Backend:

1. **Controller**:
   - [ ] Pas de pagination forcée
   - [ ] Pas de limite hardcodée (LIMIT 2)
   - [ ] Logs pour l'ID utilisateur extrait du JWT

2. **Service**:
   - [ ] Méthode retourne tous les programmes (pas de filtre par statut)
   - [ ] Pas de limitation dans la logique métier

3. **Repository**:
   - [ ] Requête JPA sans LIMIT
   - [ ] Pas de Pageable avec taille fixe

4. **Base de Données**:
   - [ ] L'utilisateur a bien plus de 2 programmes assignés
   - [ ] Pas de contraintes ou triggers qui limitent les résultats

### 🧪 Tests de Validation:

1. **Test SQL Direct**:
```sql
SELECT COUNT(*) FROM user_programmes WHERE user_id = [ID_UTILISATEUR];
```
**Résultat attendu**: > 2

2. **Test API Direct**:
```bash
curl -H "Authorization: Bearer [TOKEN]" http://localhost:8100/api/programmes/historique
```
**Résultat attendu**: Array avec > 2 éléments

3. **Test Logs Backend**:
Ajoutez ces logs temporaires dans votre controller:
```java
@GetMapping("/api/programmes/historique")
public List<UserProgramme> getHistoriqueProgrammes(Authentication auth) {
    Long userId = getCurrentUserId(auth);
    System.out.println("=== DEBUG MES PROGRAMMES ===");
    System.out.println("User ID: " + userId);
    
    List<UserProgramme> programmes = service.findAllByUserId(userId);
    System.out.println("Programmes trouvés: " + programmes.size());
    
    for (UserProgramme up : programmes) {
        System.out.println("- " + up.getProgramme().getNom() + " (Statut: " + up.getStatut() + ")");
    }
    System.out.println("=============================");
    
    return programmes;
}
```

## 🎯 Actions Immédiates

1. **Ajoutez les logs de debug** dans votre controller
2. **Testez l'endpoint directement** avec cURL/Postman
3. **Vérifiez la base de données** avec les requêtes SQL
4. **Redémarrez le serveur backend** après les modifications

## 📊 Résultats Attendus

- **Base de données**: > 2 programmes pour l'utilisateur
- **API directe**: > 2 programmes dans la réponse JSON
- **Logs backend**: "Programmes trouvés: X" avec X > 2

Si un de ces tests échoue, vous avez trouvé la source du problème ! 🎯