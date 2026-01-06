// 🧪 TEST SIMPLE - Ajoutez ce code temporairement dans MesProgrammesActivity.onCreate()

// TEST: Vérifier si les statistiques sont chargées
viewModel.statistiques.observe(this) { stats ->
    if (stats != null) {
        android.util.Log.d("TEST_PROGRESSION", "=== STATISTIQUES REÇUES ===")
        android.util.Log.d("TEST_PROGRESSION", "Progression globale: ${stats.progressionGlobale}%")
        android.util.Log.d("TEST_PROGRESSION", "Taux repas: ${stats.tauxRepas}%")
        android.util.Log.d("TEST_PROGRESSION", "Taux activités: ${stats.tauxActivites}%")
        android.util.Log.d("TEST_PROGRESSION", "Jour actuel: ${stats.jourActuel}/${stats.joursTotal}")
        android.util.Log.d("TEST_PROGRESSION", "===============================")
        
        // TEST: Afficher un toast pour confirmer
        Toast.makeText(this, "Progression: ${stats.progressionGlobale}%", Toast.LENGTH_LONG).show()
        
        // Passer les statistiques à l'adapter
        if (::mesProgrammesAdapter.isInitialized) {
            mesProgrammesAdapter.updateStatistiques(stats)
        }
    } else {
        android.util.Log.e("TEST_PROGRESSION", "❌ STATISTIQUES NULL - Problème backend!")
        Toast.makeText(this, "❌ Statistiques non disponibles", Toast.LENGTH_LONG).show()
    }
}

// TEST: Forcer le chargement des statistiques après 2 secondes
Handler(Looper.getMainLooper()).postDelayed({
    android.util.Log.d("TEST_PROGRESSION", "🔄 FORCE RELOAD STATISTIQUES")
    viewModel.loadStatistiques()
}, 2000)