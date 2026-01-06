package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.R
import com.example.projetintegration.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupAnimations()
        setupClickListeners()
    }
    
    private fun setupAnimations() {
        // Animation pour le nom de l'app
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.tvAppName.startAnimation(fadeInAnimation)
        
        // Animation pour le sous-titre
        val slideInBottomAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        binding.tvSubtitle.startAnimation(slideInBottomAnimation)
        
        // Animation pour l'image principale
        val scaleInAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        binding.ivMainSalad.startAnimation(scaleInAnimation)
        
        // Animation pour le bouton
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.btnGetStarted.startAnimation(pulseAnimation)
        
        // Animation pour les éléments flottants
        animateFloatingElements()
    }
    
    private fun animateFloatingElements() {
        // Animation de rotation continue pour les feuilles
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_continuous)
        
        // Animation de pulsation pour les fruits
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        
        // Animation des gouttes d'eau
        animateWaterDroplets()
    }
    
    private fun animateWaterDroplets() {
        // Animation subtile pour les gouttes d'eau
        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
    }
    
    private fun setupClickListeners() {
        binding.btnGetStarted.setOnClickListener {
            // Animation du bouton
            animateButton()
            
            // Naviguer vers la page de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            
            // Animation de transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
    
    private fun animateButton() {
        binding.btnGetStarted.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                binding.btnGetStarted.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
}