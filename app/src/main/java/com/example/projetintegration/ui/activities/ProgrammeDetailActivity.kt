package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityProgrammeDetailBinding
import com.example.projetintegration.ui.viewmodel.ProgrammeDetailViewModel

class ProgrammeDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammeDetailBinding
    private lateinit var viewModel: ProgrammeDetailViewModel
    private lateinit var preferencesManager: PreferencesManager
    private var programmeId: Int = 0
    private var programme: Programme? = null
    
    // Images des programmes dans l'ordre
    private val programmeImages = arrayOf(
        R.drawable.programme1,
        R.drawable.programme2,
        R.drawable.programme3,
        R.drawable.programme4,
        R.drawable.programme5,
        R.drawable.programme6,
        R.drawable.programme7
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProgrammeDetailViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        programmeId = intent.getIntExtra("PROGRAMME_ID", 0)
        
        setupToolbar()
        setupObservers()
        setupClickListeners()
        loadProgrammeDetail()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupObservers() {
        viewModel.programme.observe(this) { prog ->
            programme = prog
            displayProgrammeDetail(prog)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                finish()
            }
        }
        
        viewModel.inscriptionResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "🎉 Inscription réussie! Bon entraînement!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { exception ->
                binding.fabInscrire.isEnabled = true
                binding.fabInscrire.text = "🚀 Commencer maintenant"
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabInscrire.setOnClickListener {
            inscrireAuProgramme()
        }
        
        // Gestion des clics sur les sections
        binding.cardPlats.setOnClickListener {
            // Animation de feedback
            animateCardClick(binding.cardPlats)
        }
        
        binding.cardActivites.setOnClickListener {
            animateCardClick(binding.cardActivites)
        }
    }
    
    private fun animateCardClick(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
    
    private fun loadProgrammeDetail() {
        viewModel.loadProgramme(programmeId)
    }
    
    private fun displayProgrammeDetail(programme: Programme) {
        // Image du programme (basée sur l'ID pour la cohérence)
        val imageIndex = (programme.id - 1) % programmeImages.size
        binding.ivProgrammeHero.setImageResource(programmeImages[imageIndex])
        
        // Informations principales
        binding.tvNom.text = programme.nom
        binding.tvDescription.text = programme.description
        binding.tvFloatingDays.text = programme.dureeJours.toString()
        binding.tvObjectif.text = formatObjectif(programme.objectif)
        
        // 🔧 PROTECTION CONTRE NULL - Plats
        val plats = programme.plats ?: emptyList()
        
        val platsText = if (plats.isEmpty()) {
            "Aucun plat disponible pour ce programme.\nConsultez notre section plats pour des idées de repas sains."
        } else {
            plats.joinToString("\n\n") { plat ->
                "🍽️ ${plat.nom}\n" +
                "   ⚡ ${plat.calories} kcal • ⏱️ ${plat.tempsPreparation} min\n" +
                "   ${plat.description}"
            }
        }
        binding.tvPlatsList.text = platsText
        
        // 🔧 PROTECTION CONTRE NULL - Activités
        val activites = programme.activites ?: emptyList()
        
        val activitesText = if (activites.isEmpty()) {
            "Aucune activité disponible pour ce programme."
        } else {
            activites.joinToString("\n\n") { activite ->
                "💪 ${activite.nom}\n" +
                "   ⏱️ ${activite.duree} min • 🔥 ${activite.caloriesBrulees} kcal\n" +
                "   📊 Niveau: ${formatNiveau(activite.niveau)}\n" +
                "   ${activite.description}"
            }
        }
        binding.tvActivitesList.text = activitesText
        
        // 🔧 PROTECTION CONTRE NULL - Conseils
        val conseils = programme.conseils ?: emptyList()
        val conseilsText = if (conseils.isEmpty()) {
            "💡 Restez hydraté pendant vos entraînements\n" +
            "💡 Écoutez votre corps et respectez les temps de repos\n" +
            "💡 Maintenez une alimentation équilibrée\n" +
            "💡 Dormez suffisamment pour une récupération optimale"
        } else {
            conseils.joinToString("\n") { "💡 $it" }
        }
        binding.tvConseilsList.text = conseilsText
        
        // Mise à jour des titres des sections avec compteurs intégrés
        val platsTitle = "Plats recommandés (${plats.size})"
        val activitesTitle = "Activités sportives (${activites.size})"
    }
    
    private fun inscrireAuProgramme() {
        val userId = preferencesManager.getUserId()
        if (userId == null) {
            Toast.makeText(this, "❌ Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.fabInscrire.isEnabled = false
        binding.fabInscrire.text = "⏳ Inscription..."
        
        // Animation du bouton
        binding.fabInscrire.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(150)
            .withEndAction {
                binding.fabInscrire.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
        
        viewModel.inscrireAuProgramme(programmeId, null, null)
    }
    
    private fun formatObjectif(objectif: String): String {
        return when (objectif.lowercase()) {
            "perte-poids", "perte_poids" -> "🎯 Perte de poids"
            "prise-masse", "prise_masse" -> "💪 Prise de masse"
            "maintien" -> "⚖️ Maintien"
            "endurance" -> "🏃 Endurance"
            "force" -> "💪 Force"
            "cardio" -> "❤️ Cardio"
            "hiit" -> "⚡ HIIT"
            else -> "🎯 $objectif"
        }
    }
    
    private fun formatNiveau(niveau: String): String {
        return when (niveau.lowercase()) {
            "debutant" -> "🟢 Débutant"
            "intermediaire", "intermédiaire" -> "🟡 Intermédiaire"
            "avance", "avancé" -> "🔴 Avancé"
            else -> niveau
        }
    }
}
