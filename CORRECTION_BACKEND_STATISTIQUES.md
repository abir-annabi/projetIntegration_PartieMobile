# 🚨 CORRECTION URGENTE BACKEND - Erreur Statistiques

## 🎯 Problème Identifié

**Erreur backend :**
```
"progressionsValides cannot be resolved"
```

**Endpoint défaillant :** `/api/programmes/statistiques`

## 🔧 Correction Backend Requise

### 1. Trouvez le fichier StatistiquesService.java (ou similaire)

Cherchez cette ligne qui cause l'erreur :
```java
// ❌ ERREUR - Variable non déclarée
progressionsValides.something()
```

### 2. Corrigez la variable manquante

**Option A - Si c'est une liste de progressions :**
```java
// ✅ CORRECTION
List<ProgressionJournaliere> progressionsValides = progressionRepository
    .findByUserProgrammeAndStatutJour(userProgramme, "COMPLETE");
```

**Option B - Si c'est un filtre :**
```java
// ✅ CORRECTION
List<ProgressionJournaliere> progressionsValides = progressions.stream()
    .filter(p -> p.getStatutJour() != null && !p.getStatutJour().equals("NON_FAIT"))
    .collect(Collectors.toList());
```

### 3. Exemple de Service Statistiques Complet

```java
@Service
public class StatistiquesService {
    
    @Autowired
    private ProgressionJournaliereRepository progressionRepository;
    
    @Autowired
    private UserProgrammeRepository userProgrammeRepository;
    
    public Statistiques calculerStatistiques(Long userId) {
        // Récupérer le programme actif
        UserProgramme userProgramme = userProgrammeRepository
            .findByUserIdAndStatut(userId, "EN_COURS")
            .orElseThrow(() -> new RuntimeException("Aucun programme actif"));
        
        // ✅ CORRECTION - Déclarer progressionsValides
        List<ProgressionJournaliere> progressionsValides = progressionRepository
            .findByUserProgrammeId(userProgramme.getId());
        
        // Calculer les statistiques
        int totalJours = userProgramme.getProgramme().getDureeJours();
        int jourActuel = (int) ChronoUnit.DAYS.between(
            userProgramme.getDateDebut(), LocalDate.now()) + 1;
        
        // Calculer les taux
        int tauxCompletion = Math.min(100, (jourActuel * 100) / totalJours);
        
        int tauxRepas = calculerTauxRepas(progressionsValides, userProgramme);
        int tauxActivites = calculerTauxActivites(progressionsValides, userProgramme);
        int evolutionPhysique = calculerEvolutionPhysique(userProgramme);
        
        // ✅ FORMULE DE PROGRESSION
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
            .evolutionPhysique(evolutionPhysique)
            .jourActuel(jourActuel)
            .joursTotal(totalJours)
            .joursRestants(Math.max(0, totalJours - jourActuel))
            .build();
    }
    
    private int calculerTauxRepas(List<ProgressionJournaliere> progressions, UserProgramme up) {
        if (progressions.isEmpty()) return 0;
        
        int totalRepasAttendu = progressions.size() * up.getProgramme().getPlats().size();
        int totalRepasConsomme = progressions.stream()
            .mapToInt(p -> p.getPlatsConsommes() != null ? p.getPlatsConsommes().size() : 0)
            .sum();
        
        return totalRepasAttendu > 0 ? (totalRepasConsomme * 100) / totalRepasAttendu : 0;
    }
    
    private int calculerTauxActivites(List<ProgressionJournaliere> progressions, UserProgramme up) {
        if (progressions.isEmpty()) return 0;
        
        int totalActivitesAttendu = progressions.size() * up.getProgramme().getActivites().size();
        int totalActivitesRealisees = progressions.stream()
            .mapToInt(p -> p.getActivitesRealisees() != null ? p.getActivitesRealisees().size() : 0)
            .sum();
        
        return totalActivitesAttendu > 0 ? (totalActivitesRealisees * 100) / totalActivitesAttendu : 0;
    }
    
    private int calculerEvolutionPhysique(UserProgramme up) {
        if (up.getPoidsDebut() == null || up.getPoidsObjectif() == null || up.getPoidsActuel() == null) {
            return 50; // Valeur par défaut
        }
        
        double objectifTotal = Math.abs(up.getPoidsObjectif() - up.getPoidsDebut());
        double progressionActuelle = Math.abs(up.getPoidsActuel() - up.getPoidsDebut());
        
        return objectifTotal > 0 ? (int) Math.min(100, (progressionActuelle * 100) / objectifTotal) : 50;
    }
}
```

## 🧪 Test Après Correction

1. **Redémarrez votre serveur backend**
2. **Testez l'endpoint :**
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        http://localhost:8080/api/programmes/statistiques
   ```
3. **Résultat attendu :**
   ```json
   {
     "progressionGlobale": 75,
     "tauxCompletion": 80,
     "tauxRepas": 70,
     "tauxActivites": 85,
     "jourActuel": 34,
     "joursTotal": 45
   }
   ```

## ✅ Résultat Frontend

Une fois le backend corrigé, vous verrez dans les logs :
```
✅ Statistiques chargées: Progression globale: 75%
✅ Progression backend: 75% (au lieu de progression locale)
```

Et la progression sera correctement affichée dans l'interface !