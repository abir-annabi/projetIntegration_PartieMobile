package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNavigationBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    
    private fun setupClickListeners() {
        // Navigation vers les différentes sections
        binding.cardProgrammes.setOnClickListener {
            animateCardAndNavigate(ProgrammesActivity::class.java)
        }
        
        binding.cardMesProgrammes.setOnClickListener {
            animateCardAndNavigate(MesProgrammesActivity::class.java)
        }
        
        binding.cardPlats.setOnClickListener {
            animateCardAndNavigate(PlatsActivity::class.java)
        }
        
        binding.cardStatistiques.setOnClickListener {
            animateCardAndNavigate(StatistiquesActivity::class.java)
        }
        
        binding.cardMessages.setOnClickListener {
            animateCardAndNavigate(MessageActivity::class.java)
        }
        
        binding.cardProfile.setOnClickListener {
            // TODO: Créer ProfileActivity si nécessaire
            animateCardAndNavigate(DashboardActivity::class.java)
        }
        
        // Actions rapides
        binding.btnQuickStart.setOnClickListener {
            animateButtonAndNavigate(ProgrammesActivity::class.java)
        }
        
        binding.btnQuickStats.setOnClickListener {
            animateButtonAndNavigate(StatistiquesActivity::class.java)
        }
    }
    
    private fun animateCardAndNavigate(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
    
    private fun animateButtonAndNavigate(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}