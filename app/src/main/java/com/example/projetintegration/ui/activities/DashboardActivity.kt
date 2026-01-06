package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projetintegration.R
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityDashboardBinding
import com.example.projetintegration.ui.viewmodel.OllamaViewModel

class DashboardActivity : AppCompatActivity() {
    //home
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var preferencesManager: PreferencesManager
    private val ollamaViewModel: OllamaViewModel by viewModels()
    
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
        
        // Vérifier le statut Ollama au démarrage - REMOVED
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
        
        // Navigation vers le profil
        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        
        // Navigation vers le chatbot
        binding.cardChatbot.setOnClickListener {
            startActivity(Intent(this, ChatBotConversationsActivity::class.java))
        }
        
        // Navigation vers la communauté
        binding.cardCommunity.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }
    }
    
    // Ollama observers and methods - REMOVED
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
