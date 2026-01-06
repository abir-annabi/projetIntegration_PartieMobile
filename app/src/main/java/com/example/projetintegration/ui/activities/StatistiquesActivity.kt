package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetintegration.databinding.ActivityStatistiquesBinding
import com.example.projetintegration.ui.adapters.BadgesAdapter
import com.example.projetintegration.ui.viewmodel.StatistiquesViewModel

class StatistiquesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatistiquesBinding
    private lateinit var viewModel: StatistiquesViewModel
    private lateinit var badgesAdapter: BadgesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatistiquesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[StatistiquesViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadData()
    }
    
    private fun setupRecyclerView() {
        badgesAdapter = BadgesAdapter()
        binding.rvBadges.apply {
            layoutManager = GridLayoutManager(this@StatistiquesActivity, 2)
            adapter = badgesAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.statistiques.observe(this) { stats ->
            // Progression globale (Score calculé par le backend)
            binding.progressBar.progress = stats.progressionGlobale
            binding.tvProgressionGlobale.text = "${stats.progressionGlobale}%"
            
            // Jours
            binding.tvJourActuel.text = "Jour ${stats.jourActuel}/${stats.joursTotal}"
            binding.tvJoursRestants.text = "${stats.joursRestants} jours restants"
            
            // Taux de Complétion (40% du score global)
            binding.tvTauxCompletion.text = "${stats.tauxCompletion}%"
            
            // Taux de Repas (30% du score global)
            binding.tvTauxRepas.text = "${stats.tauxRepas}%"
            binding.tvTotalPlats.text = "${stats.totalPlatsConsommes} plats consommés"
            
            // Taux d'Activités (20% du score global)
            binding.tvTauxActivites.text = "${stats.tauxActivites}%"
            binding.tvTotalActivites.text = "${stats.totalActivitesRealisees} activités réalisées"
            
            // Évolution Physique (10% du score global)
            binding.tvEvolutionPhysiqueDetail.text = "${stats.evolutionPhysique}%"
            stats.poidsDebut?.let { debut ->
                binding.tvPoidsDebut.text = String.format("%.1f kg", debut)
            }
            stats.poidsActuel?.let { actuel ->
                binding.tvPoidsActuel.text = String.format("%.1f kg", actuel)
            }
            stats.poidsObjectif?.let { objectif ->
                binding.tvPoidsObjectif.text = String.format("%.1f kg", objectif)
            }
            stats.evolutionPoids?.let { evolution ->
                val signe = if (evolution > 0) "+" else ""
                binding.tvEvolutionPoids.text = String.format("%s%.1f kg", signe, evolution)
                
                // Couleur selon l'évolution
                val color = if (evolution < 0) {
                    android.graphics.Color.parseColor("#4CAF50") // Vert pour perte
                } else {
                    android.graphics.Color.parseColor("#FF9800") // Orange pour gain
                }
                binding.tvEvolutionPoids.setTextColor(color)
            }
            
            // Streak
            binding.tvStreakActuel.text = "${stats.streakActuel}"
            binding.tvMeilleurStreak.text = "Record: ${stats.meilleurStreak} jours"
            
            // Calories moyennes
            binding.tvCaloriesMoyennes.text = "${stats.caloriesMoyennes} kcal"
            
            // Jours actifs
            binding.tvJoursActifs.text = "${stats.joursActifs} jours"
            
            // Badges
            badgesAdapter.submitList(stats.badges)
            
            // Afficher les détails de calcul
            afficherDetailsCalcul(stats)
        }
        
        viewModel.programmeActif.observe(this) { programme ->
            binding.tvProgrammeName.text = programme.programme.nom
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        

    }
    
    private fun loadData() {
        viewModel.loadProgrammeActif()
        viewModel.loadStatistiques()
    }
    
    private fun afficherDetailsCalcul(stats: com.example.projetintegration.data.models.Statistiques) {
        // Les détails sont déjà affichés dans le layout
        // Cette méthode peut être utilisée pour des logs ou analytics
    }
}
