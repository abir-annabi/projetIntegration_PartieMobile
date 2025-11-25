package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityProgrammeDetailBinding
import kotlinx.coroutines.launch

class ProgrammeDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammeDetailBinding
    private lateinit var preferencesManager: PreferencesManager
    private var programmeId: Int = 0
    private var programme: Programme? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        programmeId = intent.getIntExtra("PROGRAMME_ID", 0)
        
        setupClickListeners()
        loadProgrammeDetail()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnInscrire.setOnClickListener {
            inscrireAuProgramme()
        }
    }
    
    private fun loadProgrammeDetail() {
        binding.progressBar.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                programme = RetrofitClient.programmeApiService.getProgrammeById(programmeId)
                programme?.let { displayProgrammeDetail(it) }
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ProgrammeDetailActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
    
    private fun displayProgrammeDetail(programme: Programme) {
        binding.tvNom.text = programme.nom
        binding.tvDescription.text = programme.description
        binding.tvDuree.text = "${programme.dureeJours} jours"
        binding.tvObjectif.text = formatObjectif(programme.objectif)
        
        // Plats
        binding.tvPlatsCount.text = "Plats recommandés (${programme.plats.size})"
        val platsText = programme.plats.joinToString("\n\n") { plat ->
            "🍽️ ${plat.nom}\n   ${plat.calories} cal • ${plat.tempsPreparation} min"
        }
        binding.tvPlatsList.text = platsText
        
        // Activités
        binding.tvActivitesCount.text = "Activités sportives (${programme.activites.size})"
        val activitesText = programme.activites.joinToString("\n\n") { activite ->
            "💪 ${activite.nom}\n   ${activite.duree} min • ${activite.caloriesBrulees} cal • ${activite.niveau}"
        }
        binding.tvActivitesList.text = activitesText
        
        // Conseils
        val conseilsText = programme.conseils.joinToString("\n") { "• $it" }
        binding.tvConseilsList.text = conseilsText
    }
    
    private fun inscrireAuProgramme() {
        val userId = preferencesManager.getUserId()
        if (userId == null) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnInscrire.isEnabled = false
        binding.btnInscrire.text = "Inscription..."
        
        lifecycleScope.launch {
            try {
                RetrofitClient.programmeApiService.inscrireUserAuProgramme(programmeId, userId)
                Toast.makeText(
                    this@ProgrammeDetailActivity,
                    "Inscription réussie!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                binding.btnInscrire.isEnabled = true
                binding.btnInscrire.text = "S'inscrire"
                Toast.makeText(
                    this@ProgrammeDetailActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun formatObjectif(objectif: String): String {
        return when (objectif) {
            "perte-poids" -> "Perte de poids"
            "prise-masse" -> "Prise de masse"
            "maintien" -> "Maintien"
            "endurance" -> "Endurance"
            else -> objectif
        }
    }
}
