# Guide Complet - Marquer sa Progression dans un Programme

## Vue d'Ensemble

Ce guide explique comment un utilisateur peut marquer son avancement dans un programme assigné en enregistrant ses repas, activités et poids. La progression est ensuite calculée automatiquement par le backend.

## 🎯 Fonctionnalité Créée: MonProgrammeDetailActivity

### Accès à l'Interface

1. **Depuis MesProgrammesActivity:**
   - L'utilisateur voit la liste de ses programmes
   - Il clique sur un programme
   - → Ouvre `MonProgrammeDetailActivity`

### Fonctionnalités de l'Interface

#### 1. **Informations du Programme**
- Nom et description du programme
- Durée totale (ex: 30 jours)
- Objectif (Perte de poids, Prise de masse, etc.)
- Progression globale (calculée automatiquement)
- Statistiques détaillées:
  - Taux de complétion
  - Taux de repas
  - Taux d'activités
  - Série actuelle (streak)
- Évolution du poids (début → actuel → objectif)

#### 2. **Sélection de la Date**
- Date actuelle affichée par défaut
- Bouton "Changer" pour sélectionner une autre date
- Permet de marquer la progression pour n'importe quel jour

#### 3. **Statut du Jour**
- ✅ Journée complète
- ⚠️ Journée partielle
- ❌ Aucune activité

#### 4. **Marquer les Repas Consommés**

**Liste des plats du programme avec checkbox:**
- Chaque plat affiche:
  - Nom du plat
  - Catégorie (🌅 Petit-déjeuner, ☀️ Déjeuner, 🌙 Dîner, 🍎 Collation)
  - Temps de préparation
  - Calories

**Processus:**
1. L'utilisateur coche un plat
2. Une dialog s'ouvre pour sélectionner le moment:
   - Petit-déjeuner
   - Déjeuner
   - Collation
   - Dîner
3. Le plat est enregistré via l'API
4. La progression est recalculée automatiquement
5. Le plat reste coché (déjà consommé)

**API appelée:**
```kotlin
POST /api/progression/plats
{
  "userProgrammeId": 1,
  "date": "2025-12-01",
  "platId": 3,
  "moment": "DEJEUNER",
  "heureConsommation": null
}
```

#### 5. **Marquer les Activités Réalisées**

**Liste des activités du programme avec checkbox:**
- Chaque activité affiche:
  - Nom de l'activité
  - Durée suggérée
  - Niveau (🟢 Débutant, 🟡 Intermédiaire, 🔴 Avancé)
  - Calories brûlées

**Processus:**
1. L'utilisateur coche une activité
2. Une dialog s'ouvre pour saisir la durée réelle:
   - Durée pré-remplie avec la durée suggérée
   - L'utilisateur peut modifier
3. L'activité est enregistrée via l'API
4. La progression est recalculée automatiquement
5. L'activité reste cochée (déjà réalisée)

**API appelée:**
```kotlin
POST /api/progression/activites
{
  "userProgrammeId": 1,
  "date": "2025-12-01",
  "activiteId": 2,
  "dureeMinutes": 30,
  "intensite": null,
  "heureRealisation": null
}
```

#### 6. **Enregistrer le Poids**

**Bouton "⚖️ Poids":**
1. L'utilisateur clique sur le bouton
2. Une dialog s'ouvre pour saisir le poids
3. Le poids est enregistré via l'API
4. La progression est recalculée automatiquement

**API appelée:**
```kotlin
POST /api/progression/poids
{
  "userProgrammeId": 1,
  "date": "2025-12-01",
  "poids": 82.5
}
```

## 📊 Calcul Automatique de la Progression

### Formule Backend

```
Progression Globale = (Taux Complétion × 40%) +
                      (Taux Repas × 30%) +
                      (Taux Activités × 20%) +
                      (Évolution Physique × 10%)
```

### Mise à Jour Dynamique

Après chaque action (ajout de plat, activité ou poids):
1. Le backend recalcule automatiquement la progression
2. Le frontend recharge les statistiques
3. L'interface se met à jour en temps réel

**Appels API pour mise à jour:**
```kotlin
// Après chaque ajout
viewModel.loadStatistiques()  // Recharge les stats calculées
viewModel.loadProgressionJour(date)  // Recharge la progression du jour
```

## 🔄 Flux Complet d'Utilisation

### Scénario: Utilisateur marque sa journée

1. **Ouverture:**
   - Utilisateur ouvre "Mes Programmes"
   - Clique sur son programme actif
   - → MonProgrammeDetailActivity s'ouvre

2. **Consultation:**
   - Voit sa progression globale: 62%
   - Voit les détails: Complétion 40%, Repas 90%, Activités 80%
   - Voit son poids actuel vs objectif

3. **Marquer les repas:**
   - Coche "Omelette aux légumes"
   - Sélectionne "Petit-déjeuner"
   - ✅ Enregistré
   - Coche "Salade César"
   - Sélectionne "Déjeuner"
   - ✅ Enregistré

4. **Marquer les activités:**
   - Coche "Course à pied"
   - Confirme 30 minutes
   - ✅ Enregistré

5. **Enregistrer le poids:**
   - Clique sur "⚖️ Poids"
   - Saisit 82.3 kg
   - ✅ Enregistré

6. **Résultat:**
   - Progression mise à jour automatiquement: 65%
   - Statut du jour: ✅ Journée complète
   - Calories affichées: 1850 kcal

## 📱 Fichiers Créés

### Activities
- `MonProgrammeDetailActivity.kt` - Interface principale

### ViewModels
- `MonProgrammeDetailViewModel.kt` - Logique métier

### Adapters
- `PlatsSelectionAdapter.kt` - Liste des plats avec checkbox
- `ActivitesSelectionAdapter.kt` - Liste des activités avec checkbox

### Layouts
- `activity_mon_programme_detail.xml` - Layout principal
- `item_plat_selection.xml` - Item de plat avec checkbox
- `item_activite_selection.xml` - Item d'activité avec checkbox

### Models (ajoutés dans Programme.kt)
- `AjouterPlatRequest` - Requête pour ajouter un plat
- `AjouterActiviteRequest` - Requête pour ajouter une activité
- `EnregistrerPoidsRequest` - Requête pour enregistrer le poids

### API (ajoutés dans ProgrammeApiService.kt)
- `POST /api/progression/plats` - Ajouter un plat
- `POST /api/progression/activites` - Ajouter une activité
- `POST /api/progression/poids` - Enregistrer le poids
- `GET /api/progression/jour/{date}` - Obtenir la progression d'un jour

## 🎨 Expérience Utilisateur

### Points Forts

1. **Interface Intuitive:**
   - Checkbox simples pour marquer les actions
   - Feedback visuel immédiat
   - Plats/activités déjà réalisés restent cochés

2. **Flexibilité:**
   - Peut marquer n'importe quel jour (pas seulement aujourd'hui)
   - Peut modifier la durée des activités
   - Peut choisir le moment des repas

3. **Motivation:**
   - Voit sa progression augmenter en temps réel
   - Voit son streak (série de jours consécutifs)
   - Voit l'évolution de son poids

4. **Transparence:**
   - Comprend comment la progression est calculée
   - Voit les détails (complétion, repas, activités)
   - Peut accéder aux statistiques détaillées

### Améliorations Futures Possibles

1. **Retirer des éléments:**
   - Permettre de décocher un plat/activité déjà enregistré
   - Ajouter un endpoint DELETE

2. **Notifications:**
   - Rappel quotidien pour enregistrer la progression
   - Félicitations quand un objectif est atteint

3. **Graphiques:**
   - Courbe d'évolution du poids
   - Graphique de progression sur 30 jours
   - Répartition des calories

4. **Notes:**
   - Ajouter des notes personnelles pour chaque jour
   - Enregistrer comment on se sent

## 🔧 Configuration Technique

### Dépendances Utilisées
- ViewBinding (déjà configuré)
- RecyclerView pour les listes
- Material Design Components pour les dialogs
- Coroutines pour les appels API asynchrones
- LiveData pour la réactivité

### Navigation
```
MesProgrammesActivity
    ↓ (click sur programme)
MonProgrammeDetailActivity
    ↓ (click sur "Voir Statistiques")
StatistiquesActivity (à implémenter)
```

## ✅ Compilation

Le projet compile sans erreurs:
```bash
.\gradlew assembleDebug
BUILD SUCCESSFUL
```

## 📝 Résumé

L'utilisateur peut maintenant:
1. ✅ Voir les détails de son programme assigné
2. ✅ Marquer les repas consommés (avec moment de la journée)
3. ✅ Marquer les activités réalisées (avec durée personnalisée)
4. ✅ Enregistrer son poids quotidien
5. ✅ Voir sa progression calculée automatiquement
6. ✅ Changer de date pour marquer des jours passés
7. ✅ Voir le statut de sa journée (complète/partielle/vide)

La progression est **calculée automatiquement** par le backend et **mise à jour dynamiquement** dans l'interface!
