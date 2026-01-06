# Calcul de Progression - Implémentation Frontend

## ✅ Implémentation Complète

Le frontend affiche maintenant **tous les détails du calcul de progression** effectué par le backend.

### 📊 Score Global de Progression

Le score global (0-100%) est calculé par le backend selon la formule:

```
Score Global = (Taux Complétion × 0.4) + (Taux Repas × 0.3) + (Taux Activités × 0.2) + (Évolution Physique × 0.1)
```

### 🧮 Les 4 Composantes du Score

#### 1. Taux de Complétion (40% du score)
**Calcul Backend:**
```java
long joursCompletes = progressionRepository.countByUserProgrammeIdAndStatutJour(userProgrammeId, "COMPLETE");
int tauxCompletion = (int) ((joursCompletes * 100.0) / programme.getDureeJours());
```

**Affichage Frontend:**
- Card verte avec icône ✅
- Pourcentage affiché en grand
- Explication: "Jours complétés / Jours totaux du programme"
- Poids: 40% du score global

**Exemple:**
- 12 jours complétés sur 30 jours = 40%
- Contribution au score: 40% × 0.4 = 16 points

#### 2. Taux de Repas (30% du score)
**Calcul Backend:**
```java
int platsAttendus = programme.getPlats().size() * jourActuel;
int platsConsommes = getTotalPlatsConsommes(userProgrammeId);
int tauxRepas = (int) ((platsConsommes * 100.0) / platsAttendus);
```

**Affichage Frontend:**
- Card orange avec icône 🍽️
- Pourcentage affiché en grand
- Explication: "Plats consommés / Plats attendus"
- Total de plats consommés affiché
- Poids: 30% du score global

**Exemple:**
- Programme avec 4 plats/jour
- Jour 15: 60 plats attendus
- 42 plats consommés = 70%
- Contribution au score: 70% × 0.3 = 21 points

#### 3. Taux d'Activités (20% du score)
**Calcul Backend:**
```java
int activitesAttendues = programme.getActivites().size() * jourActuel;
int activitesRealisees = getTotalActivitesRealisees(userProgrammeId);
int tauxActivites = (int) ((activitesRealisees * 100.0) / activitesAttendues);
```

**Affichage Frontend:**
- Card bleue avec icône 💪
- Pourcentage affiché en grand
- Explication: "Activités réalisées / Activités attendues"
- Total d'activités réalisées affiché
- Poids: 20% du score global

**Exemple:**
- Programme avec 2 activités/jour
- Jour 15: 30 activités attendues
- 18 activités réalisées = 60%
- Contribution au score: 60% × 0.2 = 12 points

#### 4. Évolution Physique (10% du score)
**Calcul Backend:**
```java
double objectifTotal = Math.abs(poidsObjectif - poidsDebut);
double progressionRealisee = Math.abs(poidsActuel - poidsDebut);
int evolutionPhysique = (int) ((progressionRealisee / objectifTotal) * 100);
```

**Affichage Frontend:**
- Card violette avec icône ⚖️
- Pourcentage affiché en grand
- Explication: "Progression poids / Objectif poids"
- Affichage des 3 poids: Début, Actuel, Objectif
- Évolution en kg affichée (avec couleur)
- Poids: 10% du score global

**Exemple:**
- Poids début: 85 kg
- Poids objectif: 75 kg (objectif: -10 kg)
- Poids actuel: 81 kg (progression: -4 kg)
- Évolution: 4/10 = 40%
- Contribution au score: 40% × 0.1 = 4 points

### 📱 Interface Utilisateur

#### Écran Statistiques (StatistiquesActivity)

**Section 1: Score Global**
- Barre de progression visuelle
- Pourcentage en grand (ex: 67%)
- Jour actuel / Jours totaux
- Jours restants

**Section 2: Composition du Score**
Chaque composante dans une card colorée:
- ✅ Taux de Complétion (vert) - 40%
- 🍽️ Taux de Repas (orange) - 30%
- 💪 Taux d'Activités (bleu) - 20%
- ⚖️ Évolution Physique (violet) - 10%

Chaque card affiche:
- Icône et titre
- Pourcentage actuel
- Poids dans le calcul global
- Explication du calcul
- Données détaillées

**Section 3: Streak**
- 🔥 Streak actuel (jours consécutifs)
- Record personnel

**Section 4: Autres Stats**
- Calories moyennes par jour
- Jours actifs

**Section 5: Badges**
- Liste des badges obtenus
- Affichage en grille

### 🔄 Flux de Données

```
1. User ouvre StatistiquesActivity
   ↓
2. ViewModel.loadStatistiques()
   ↓
3. API GET /api/programmes/statistiques
   ↓
4. Backend calcule:
   - Taux de complétion (40%)
   - Taux de repas (30%)
   - Taux d'activités (20%)
   - Évolution physique (10%)
   - Score global = somme pondérée
   ↓
5. Backend retourne Statistiques
   ↓
6. Frontend affiche dans l'UI:
   - Score global avec barre de progression
   - Détails de chaque composante
   - Poids, streaks, badges
```

### 📊 Exemple Complet

**Données:**
- Programme: 30 jours
- Jour actuel: 15
- Jours complétés: 12 (80%)
- Plats: 42/60 (70%)
- Activités: 18/30 (60%)
- Poids: 81kg (objectif -10kg, progression -4kg = 40%)

**Calcul:**
```
Score Global = (80 × 0.4) + (70 × 0.3) + (60 × 0.2) + (40 × 0.1)
             = 32 + 21 + 12 + 4
             = 69%
```

**Affichage:**
- Barre de progression: 69%
- ✅ Complétion: 80% (32 points)
- 🍽️ Repas: 70% (21 points)
- 💪 Activités: 60% (12 points)
- ⚖️ Poids: 40% (4 points)

### 🎯 Avantages de cette Implémentation

1. **Transparence Totale**
   - L'utilisateur voit exactement comment son score est calculé
   - Chaque composante est clairement identifiée

2. **Motivation**
   - L'utilisateur sait sur quoi se concentrer pour améliorer son score
   - Les poids (40%, 30%, 20%, 10%) montrent les priorités

3. **Feedback Visuel**
   - Couleurs différentes pour chaque composante
   - Barres de progression
   - Icônes expressives

4. **Conformité Backend**
   - Le frontend affiche exactement ce que le backend calcule
   - Aucun calcul côté frontend (source unique de vérité)

### 🔧 Fichiers Créés/Modifiés

**Nouveaux fichiers:**
- `app/src/main/java/com/example/projetintegration/ui/activities/StatistiquesActivity.kt`
- `app/src/main/res/layout/activity_statistiques.xml`

**Fichiers existants:**
- `app/src/main/java/com/example/projetintegration/ui/viewmodel/StatistiquesViewModel.kt` (déjà existant)
- `app/src/main/java/com/example/projetintegration/data/models/Programme.kt` (modèle Statistiques déjà défini)

### ⚠️ Note Importante

**Le frontend est 100% prêt** mais ne peut pas fonctionner tant que le backend retourne 403 pour `/api/programmes/statistiques`.

Une fois le backend corrigé, l'écran de statistiques affichera immédiatement:
- ✅ Le score global calculé dynamiquement
- ✅ Les 4 composantes avec leurs poids respectifs
- ✅ Tous les détails de progression
- ✅ Les badges obtenus

### 🚀 Pour Tester

Une fois le backend corrigé:

```kotlin
// Depuis n'importe quelle Activity:
val intent = Intent(this, StatistiquesActivity::class.java)
startActivity(intent)
```

Ou ajoutez un bouton dans le Dashboard:
```kotlin
binding.cardStatistiques.setOnClickListener {
    startActivity(Intent(this, StatistiquesActivity::class.java))
}
```

## 📝 Résumé

Le frontend implémente maintenant **l'affichage complet et détaillé** du système de calcul de progression:
- ✅ Score global avec formule pondérée
- ✅ 4 composantes clairement identifiées
- ✅ Poids de chaque composante (40%, 30%, 20%, 10%)
- ✅ Explications pour chaque calcul
- ✅ Interface visuelle intuitive et colorée

Tout est prêt côté frontend. Le seul blocage est le backend qui retourne 403.
