package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.R
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        
        // Vérifier si l'utilisateur est connecté
        if (!preferencesManager.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        setupUserInfo()
        setupClickListeners()
    }
    
    private fun setupUserInfo() {
        val prenom = preferencesManager.getUserPrenom() ?: ""
        val nom = preferencesManager.getUserNom() ?: ""
        val email = preferencesManager.getUserEmail() ?: ""
        
        binding.tvWelcome.text = getString(R.string.welcome_message, prenom)
        binding.tvUserName.text = "$prenom $nom"
        binding.tvUserEmail.text = email
    }
    
    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            preferencesManager.clearAuthData()
            navigateToLogin()
        }
        
        // Navigation vers les plats
        binding.cardPlats.setOnClickListener {
            startActivity(Intent(this, PlatsActivity::class.java))
        }
        
        // Navigation vers les programmes
        binding.cardProgrammes.setOnClickListener {
            startActivity(Intent(this, ProgrammesActivity::class.java))
        }
        
        // Navigation vers mes programmes
        binding.cardMesProgrammes.setOnClickListener {
            startActivity(Intent(this, MesProgrammesActivity::class.java))
        }
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
