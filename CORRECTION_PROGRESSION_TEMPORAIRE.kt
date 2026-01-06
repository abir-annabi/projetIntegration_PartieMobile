// 🔧 CORRECTION TEMPORAIRE - Si le backend ne fonctionne pas
// Ajoutez cette méthode dans MesProgrammesAdapter.kt

private fun calculerProgressionLocale(userProgramme: UserProgramme): Int {
    try {
        val dateDebut = java.time.LocalDate.parse(userProgramme.dateDebut)
        val dateActuelle = java.time.LocalDate.now()
        val joursEcoules = java.time.temporal.ChronoUnit.DAYS.between(dateDebut, dateActuelle).toInt()
        val dureeTotal = userProgramme.programme.dureeJours
        
        return when (userProgramme.statut.uppercase()) {
            "EN_COURS" -> {
                if (dureeTotal > 0) {
                    // Progression basée sur les jours écoulés (max 100%)
                    kotlin.math.min(100, kotlin.math.max(0, (joursEcoules * 100) / dureeTotal))
                } else 0
            }
            "TERMINE" -> 100
            "PAUSE" -> {
                // Pour les programmes en pause, calculer la progression jusqu'à la pause
                if (dureeTotal > 0) {
                    kotlin.math.min(100, kotlin.math.max(0, (joursEcoules * 100) / dureeTotal))
                } else 50
            }
            "ABANDONNE" -> 0
            else -> 0
        }
    } catch (e: Exception) {
        android.util.Log.e("MesProgrammesAdapter", "Erreur calcul progression locale: ${e.message}")
        return 0
    }
}

// 🔧 CORRECTION TEMPORAIRE - Modifier la méthode bind() dans MesProgrammesAdapter
fun bind(userProgramme: UserProgramme) {
    // ... code existant ...
    
    // ✅ CORRECTION TEMPORAIRE: Utiliser la progression locale si pas de statistiques
    val statistiques = statistiquesMap[userProgramme.id]
    val progressionAffichee = if (statistiques?.progressionGlobale != null && statistiques.progressionGlobale > 0) {
        // Utiliser la vraie progression du backend
        android.util.Log.d("MesProgrammesAdapter", "✅ Progression backend: ${statistiques.progressionGlobale}%")
        statistiques.progressionGlobale
    } else {
        // 🔧 FALLBACK: Calcul local si backend ne fonctionne pas
        val progressionLocale = calculerProgressionLocale(userProgramme)
        android.util.Log.w("MesProgrammesAdapter", "⚠️ Progression locale: $progressionLocale% (backend non disponible)")
        progressionLocale
    }
    
    binding.progressBar.progress = progressionAffichee
    
    // Afficher les détails
    if (statistiques != null) {
        binding.tvProgression.text = "${progressionAffichee}% • ${statistiques.tauxRepas}% repas • ${statistiques.tauxActivites}% activités"
    } else {
        // 🔧 FALLBACK: Affichage basique si pas de statistiques
        val nbPlats = userProgramme.programme.plats?.size ?: 0
        val nbActivites = userProgramme.programme.activites?.size ?: 0
        binding.tvProgression.text = "${progressionAffichee}% • 📋 $nbPlats plats • 💪 $nbActivites activités (estimation)"
    }
    
    // ... reste du code ...
}