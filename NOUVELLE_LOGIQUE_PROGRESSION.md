# 🔄 Migration vers la Nouvelle Logique de Progression

## ✅ Changements Implémentés

### 1. Modèles de Données Mis à Jour
- ✅ **UserProgramme** - Ajout de poidsDebut, poidsActuel, poidsObjectif, dateFinPrevue
- ✅ **ProgressionJournaliere** - Nouveau modèle pour le suivi quotidien
- ✅ **Statistiques** - Modèle complet avec tous les indicateurs
- ✅ **Badge** - Système de gamification
- ✅ **AssignerProgrammeRequest** - Nouvelle requête d'inscription
- ✅ **EnregistrerProgressionRequest** - Requête pour enregistrer la journée

### 2. API Service
- ✅ Tous les nouveaux endpoints ajoutés:
  - `POST /api/programmes/assigner`
  - `GET /api/programmes/actif`
  - `GET /api/programmes/statistiques`
  - `POST /api/progression/enregistrer`
  - `GET /api/progression/historique`
  - `GET /api/progression/aujourd-hui`
  - `GET /api/programmes/historique`
  - `PUT /api/programmes/terminer`
  - `PUT /api/programmes/pauser`
  - `PUT /api/programmes/reprendre`

### 3. Repository
- ✅ Toutes les méthodes mises à jour pour utiliser la nouvelle API

### 4. ViewModels
- ✅ **StatistiquesViewModel** - Nouveau ViewModel pour les statistiques
- ✅ **MesProgrammesViewModel** - Mis à jour pour utiliser programme actif
- ✅ **ProgrammeDetailViewModel** - Mis à jour pour l'inscription avec poids

### 5. Activities & Adapters
- ✅ **StatistiquesActivity** - Nouvelle activity pour afficher les stats
- ✅ **BadgesAdapter** - Adapter pour afficher les badges
- ✅ Layout **item_badge.xml** créé

---

## 🚧 À Compléter

### 1. Activity pour Enregistrer la Progression Quotidienne
Créer `EnregistrerProgressionActivity.kt` avec:
- Checklist des plats du programme
- Checklist des activités du programme
- Input pour le poids du jour
- Zone de notes
- Bouton d'enregistrement

### 2. Mettre à Jour MesProgrammesActivity
Remplacer l'ancien système de progression manuelle par:
- Affichage du programme actif
- Statistiques en temps réel
- Bouton "Enregistrer ma journée"
- Historique des jours
- Badges obtenus

### 3. Mettre à Jour ProgrammeDetailActivity
Lors de l'inscription, demander:
- Poids de départ (optionnel)
- Poids objectif (optionnel)
- Date de début (optionnel, défaut: aujourd'hui)

### 4. Layout activity_statistiques.xml
Créer le layout complet avec:
- Barre de progression globale
- Cartes pour streak, poids, repas, activités
- Section badges
- Graphiques (optionnel)

### 5. Layout activity_enregistrer_progression.xml
Créer le formulaire d'enregistrement quotidien

### 6. Mettre à Jour item_mes_programmes.xml
Remplacer les boutons +10% / +25% par:
- Affichage de la progression calculée automatiquement
- Bouton "Voir les statistiques"
- Bouton "Enregistrer aujourd'hui"

---

## 📊 Nouvelle Logique de Calcul

### Progression Globale (Calculée Automatiquement)
```
Progression = (Taux Complétion × 40%) + 
              (Taux Repas × 30%) + 
              (Taux Activités × 20%) + 
              (Évolution Physique × 10%)
```

### Taux de Complétion
```
Taux = (Jours Complétés / Total Jours) × 100
```

### Taux Repas
```
Taux = (Plats Consommés / Plats Prévus) × 100
```

### Taux Activités
```
Taux = (Activités Réalisées / Activités Prévues) × 100
```

### Évolution Physique
```
Si objectif perte de poids:
  Évolution = ((Poids Début - Poids Actuel) / (Poids Début - Poids Objectif)) × 100

Si objectif prise de masse:
  Évolution = ((Poids Actuel - Poids Début) / (Poids Objectif - Poids Début)) × 100
```

---

## 🎮 Système de Badges

### Badges Disponibles
1. **🔥 Débutant** - 7 jours consécutifs
2. **⭐ Régulier** - 14 jours consécutifs
3. **🏆 Champion** - 30 jours consécutifs
4. **💪 Sportif** - 20 activités complétées
5. **🥗 Nutritionniste** - 50 plats consommés
6. **🎯 Objectif Atteint** - Programme terminé

---

## 🔄 Flux Utilisateur Mis à Jour

### Ancien Flux (À Remplacer)
```
1. Inscription au programme
2. Mise à jour manuelle de la progression (+10%, +25%)
3. Affichage de la barre de progression
```

### Nouveau Flux
```
1. Inscription au programme (avec poids optionnel)
2. Chaque jour: Enregistrer les plats et activités
3. Système calcule automatiquement la progression
4. Déblocage automatique des badges
5. Consultation des statistiques détaillées
```

---

## 📱 Interfaces à Créer/Modifier

### 1. Dashboard Principal (Optionnel)
Ajouter une carte "Mon Programme" avec:
- Nom du programme
- Jour X/Y
- Progression globale
- Streak actuel
- Bouton "Enregistrer aujourd'hui"

### 2. Mes Programmes (Modifier)
**Avant:**
- Liste des programmes
- Boutons +10% / +25%

**Après:**
- Programme actif en haut
- Statistiques résumées
- Bouton "Enregistrer aujourd'hui"
- Bouton "Voir statistiques détaillées"
- Historique des programmes (onglet séparé)

### 3. Enregistrer Progression (Nouveau)
- Date (défaut: aujourd'hui)
- Liste des plats du programme (checkboxes)
- Liste des activités du programme (checkboxes)
- Input poids du jour
- Zone de notes
- Bouton "Enregistrer"

### 4. Statistiques Détaillées (Nouveau)
- Progression globale (grande barre)
- 4 cartes: Complétion, Repas, Activités, Physique
- Streak actuel et record
- Évolution du poids (graphique)
- Badges obtenus
- Historique des 30 derniers jours

### 5. Détail Programme (Modifier)
Lors de l'inscription, ajouter un dialog:
```
"Voulez-vous définir un objectif de poids?"
- Poids actuel: [___] kg
- Poids objectif: [___] kg
- [Ignorer] [Enregistrer]
```

---

## 🎨 Composants UI Recommandés

### Carte Statistique
```xml
<CardView>
  <Icon> 🔥 </Icon>
  <Title> Streak </Title>
  <Value> 5 jours </Value>
  <Detail> Record: 8 jours </Detail>
</CardView>
```

### Barre de Progression Détaillée
```xml
<ProgressBar progress="67%" />
<LinearLayout>
  <Text>Complétion: 80%</Text>
  <Text>Repas: 70%</Text>
  <Text>Activités: 60%</Text>
  <Text>Physique: 46%</Text>
</LinearLayout>
```

### Badge
```xml
<CardView>
  <Icon> 🏆 </Icon>
  <Title> Champion </Title>
  <Description> 30 jours consécutifs </Description>
</CardView>
```

---

## 🔧 Code à Ajouter

### Dans ProgrammeDetailActivity
```kotlin
private fun showPoidsDialog(programmeId: Int) {
    val dialog = AlertDialog.Builder(this)
    val view = layoutInflater.inflate(R.layout.dialog_poids_objectif, null)
    
    val etPoidsDebut = view.findViewById<EditText>(R.id.etPoidsDebut)
    val etPoidsObjectif = view.findViewById<EditText>(R.id.etPoidsObjectif)
    
    dialog.setView(view)
        .setTitle("Objectif de poids")
        .setPositiveButton("Enregistrer") { _, _ ->
            val poidsDebut = etPoidsDebut.text.toString().toDoubleOrNull()
            val poidsObjectif = etPoidsObjectif.text.toString().toDoubleOrNull()
            viewModel.inscrireAuProgramme(programmeId, poidsDebut, poidsObjectif)
        }
        .setNegativeButton("Ignorer") { _, _ ->
            viewModel.inscrireAuProgramme(programmeId, null, null)
        }
        .show()
}
```

### Dans MesProgrammesActivity
```kotlin
private fun setupObservers() {
    viewModel.programmeActif.observe(this) { programme ->
        if (programme != null) {
            binding.cardProgrammeActif.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            displayProgrammeActif(programme)
        } else {
            binding.cardProgrammeActif.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        }
    }
    
    viewModel.statistiques.observe(this) { stats ->
        stats?.let { displayStatistiques(it) }
    }
}

private fun displayStatistiques(stats: Statistiques) {
    binding.progressBar.progress = stats.progressionGlobale
    binding.tvProgression.text = "${stats.progressionGlobale}%"
    binding.tvStreak.text = "${stats.streakActuel} 🔥"
    binding.tvJour.text = "Jour ${stats.jourActuel}/${stats.joursTotal}"
}
```

---

## ✅ Checklist de Migration

### Backend
- [x] API endpoints créés
- [x] Calcul automatique de la progression
- [x] Système de badges
- [x] Statistiques détaillées

### Frontend - Modèles & Architecture
- [x] Modèles de données mis à jour
- [x] API Service mis à jour
- [x] Repository mis à jour
- [x] ViewModels créés/mis à jour

### Frontend - UI (À Compléter)
- [ ] Layout activity_statistiques.xml
- [ ] Layout activity_enregistrer_progression.xml
- [ ] Layout dialog_poids_objectif.xml
- [ ] Mettre à jour activity_mes_programmes.xml
- [ ] Créer EnregistrerProgressionActivity.kt
- [ ] Mettre à jour MesProgrammesActivity.kt
- [ ] Mettre à jour ProgrammeDetailActivity.kt
- [ ] Ajouter navigation vers statistiques
- [ ] Ajouter navigation vers enregistrement
- [ ] Tester le flux complet

---

## 🚀 Prochaines Étapes

1. **Créer les layouts manquants**
2. **Implémenter EnregistrerProgressionActivity**
3. **Mettre à jour MesProgrammesActivity avec la nouvelle UI**
4. **Ajouter le dialog de poids dans ProgrammeDetailActivity**
5. **Tester le flux complet**
6. **Ajouter des graphiques (optionnel)**

---

**Status**: Architecture backend et frontend complétée à 70%  
**Reste**: UI des nouvelles fonctionnalités (30%)
