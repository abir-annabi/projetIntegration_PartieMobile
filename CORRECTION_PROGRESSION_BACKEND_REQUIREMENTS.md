# 🔧 Corrections Requises Côté Backend - Progression

## 🎯 Problème Principal
La progression automatique n'est pas marquée correctement car le frontend ne reçoit pas les bonnes données du backend.

## 📋 Vérifications Backend Requises

### 1. **Endpoint `/api/programmes/statistiques`**

**Vérifier que cet endpoint retourne bien :**
```json
{
  "progressionGlobale": 75,        // ✅ CRITIQUE: Progression calculée (0-100)
  "tauxCompletion": 80,           // Pourcentage de jours complétés
  "tauxRepas": 70,                // Pourcentage de repas consommés
  "tauxActivites": 85,            // Pourcentage d'activités réalisées
  "evolutionPhysique": 60,        // Progression vers objectif poids
  "streakActuel": 5,              // Jours consécutifs actifs
  "meilleurStreak": 12,
  "joursActifs": 15,
  "jourActuel": 18,               // ✅ CRITIQUE: Jour actuel du programme
  "joursTotal": 30,               // ✅ CRITIQUE: Durée totale du programme
  "joursRestants": 12,
  "poidsDebut": 85.0,
  "poidsActuel": 82.5,
  "poidsObjectif": 75.0,
  "evolutionPoids": -2.5,
  "caloriesMoyennes": 1800,
  "totalPlatsConsommes": 45,
  "totalActivitesRealisees": 20,
  "badges": []
}
```

### 2. **Calcul de `progressionGlobale`**

**Vérifier que le backend calcule bien :**
```
progressionGlobale = (tauxCompletion × 40%) + 
                     (tauxRepas × 30%) + 
                     (tauxActivites × 20%) + 
                     (evolutionPhysique × 10%)
```

### 3. **Endpoint `/api/programmes/historique`**

**Vérifier que chaque UserProgramme contient :**
```json
{
  "id": 1,
  "programme": {
    "id": 1,
    "nom": "Programme Perte de Poids",
    "dureeJours": 30,              // ✅ CRITIQUE: Durée pour calcul progression
    "plats": [...],                // ✅ CRITIQUE: Doit être rempli
    "activites": [...]             // ✅ CRITIQUE: Doit être rempli
  },
  "dateDebut": "2025-01-01",
  "statut": "EN_COURS",
  "poidsDebut": 85.0,
  "poidsActuel": 82.5,
  "poidsObjectif": 75.0
}
```

### 4. **Endpoint `/api/progression/enregistrer`**

**Vérifier que après enregistrement :**
- Les statistiques sont recalculées automatiquement
- `progressionGlobale` est mise à jour
- `tauxRepas` et `tauxActivites` sont recalculés
- `jourActuel` est incrémenté si nécessaire

## 🔍 Tests Backend Recommandés

### Test 1: Vérifier les Statistiques
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/programmes/statistiques
```

**Résultat attendu :** JSON avec `progressionGlobale` > 0

### Test 2: Vérifier l'Historique
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/programmes/historique
```

**Résultat attendu :** Programmes avec `plats` et `activites` non vides

### Test 3: Enregistrer une Progression
```bash
curl -X POST \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "date": "2025-01-04",
       "platIds": [1, 2],
       "activiteIds": [1],
       "poidsJour": 82.0
     }' \
     http://localhost:8080/api/progression/enregistrer
```

**Puis vérifier :** Les statistiques sont mises à jour

## 🚨 Problèmes Fréquents Backend

### 1. **Programmes sans Contenu**
```sql
-- Vérifier que les programmes ont des plats et activités
SELECT p.id, p.nom, 
       COUNT(DISTINCT pp.plat_id) as nb_plats,
       COUNT(DISTINCT pa.activite_id) as nb_activites
FROM programmes p
LEFT JOIN programme_plats pp ON p.id = pp.programme_id
LEFT JOIN programme_activites pa ON p.id = pa.programme_id
GROUP BY p.id, p.nom;
```

### 2. **Statistiques Non Calculées**
```sql
-- Vérifier les progressions enregistrées
SELECT up.id, up.statut, 
       COUNT(pj.id) as nb_progressions,
       MAX(pj.date) as derniere_progression
FROM user_programmes up
LEFT JOIN progressions_journalieres pj ON up.id = pj.user_programme_id
GROUP BY up.id, up.statut;
```

### 3. **Dates Incorrectes**
```sql
-- Vérifier les dates des programmes
SELECT id, date_debut, date_fin_prevue, statut,
       DATEDIFF(CURDATE(), date_debut) as jours_ecoules,
       duree_jours
FROM user_programmes
WHERE statut = 'EN_COURS';
```

## 🔧 Corrections Backend Suggérées

### 1. **Dans le Service de Statistiques**
```java
@Service
public class StatistiquesService {
    
    public Statistiques calculerStatistiques(Long userProgrammeId) {
        UserProgramme up = userProgrammeRepository.findById(userProgrammeId);
        
        // ✅ CORRECTION: Calculer le jour actuel
        int jourActuel = (int) ChronoUnit.DAYS.between(
            up.getDateDebut(), LocalDate.now()) + 1;
        
        // ✅ CORRECTION: Calculer les taux réels
        int tauxRepas = calculerTauxRepas(userProgrammeId);
        int tauxActivites = calculerTauxActivites(userProgrammeId);
        int tauxCompletion = Math.min(100, (jourActuel * 100) / up.getProgramme().getDureeJours());
        
        // ✅ CORRECTION: Calculer la progression globale
        int progressionGlobale = (int) (
            (tauxCompletion * 0.4) +
            (tauxRepas * 0.3) +
            (tauxActivites * 0.2) +
            (evolutionPhysique * 0.1)
        );
        
        return Statistiques.builder()
            .progressionGlobale(progressionGlobale)
            .tauxCompletion(tauxCompletion)
            .tauxRepas(tauxRepas)
            .tauxActivites(tauxActivites)
            .jourActuel(jourActuel)
            .joursTotal(up.getProgramme().getDureeJours())
            .build();
    }
}
```

### 2. **Dans le Repository des Programmes**
```java
@Repository
public class ProgrammeRepository {
    
    @Query("SELECT p FROM Programme p " +
           "LEFT JOIN FETCH p.plats " +
           "LEFT JOIN FETCH p.activites " +
           "WHERE p.id = :id")
    Programme findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT up FROM UserProgramme up " +
           "LEFT JOIN FETCH up.programme.plats " +
           "LEFT JOIN FETCH up.programme.activites " +
           "WHERE up.user.id = :userId")
    List<UserProgramme> findHistoriqueWithDetails(@Param("userId") Long userId);
}
```

## ✅ Actions Frontend Déjà Corrigées

1. **MesProgrammesAdapter** utilise maintenant les vraies statistiques
2. **MonProgrammeDetailViewModel** recharge les statistiques après enregistrement
3. **MesProgrammesActivity** passe les statistiques à l'adapter
4. **Logs détaillés** pour diagnostiquer les problèmes

## 🎯 Résultat Attendu

Après ces corrections backend :

1. **Dans "Mes Programmes" :** Progression réelle basée sur les activités/repas
2. **Après enregistrement :** Progression mise à jour automatiquement
3. **Logs frontend :** Affichent les vraies valeurs du backend
4. **Interface cohérente :** Même progression dans tous les écrans

## 📞 Test Final

1. Enregistrer une progression dans "Mon Programme Détail"
2. Vérifier les logs : `✅ Statistiques chargées: Progression globale: XX%`
3. Retourner à "Mes Programmes"
4. Vérifier que la barre de progression affiche la vraie valeur
5. Aller dans "Statistiques" pour voir les détails complets

Si la progression reste à 0% ou ne se met pas à jour, le problème est côté backend.