package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.R
import com.example.projetintegration.databinding.ActivityHomeDashboardBinding

class HomeDashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeDashboardBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupAnimations()
        setupClickListeners()
    }
    
    private fun setupAnimations() {
        // Animation pour le titre BIO
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        binding.root.findViewById<android.widget.TextView>(R.id.tvBio)?.startAnimation(slideInAnimation)
        
        // Animation pour les fruits (délai progressif)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        
        // Animation pour le smoothie
        val scaleInAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        
        // Animation pour les éléments flottants
        animateFloatingElements()
    }
    
    private fun animateFloatingElements() {
        // Animation de rotation continue pour les feuilles
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_continuous)
        
        // Animation de pulsation pour les fruits
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
    }
    
    private fun setupClickListeners() {
        // Menu principal
        binding.ivMenu.setOnClickListener {
            openMainMenu()
        }
        
        // FAB Menu
        binding.fabMenu.setOnClickListener {
            openNavigationMenu()
        }
        
        // Refresh button
        binding.ivRefresh.setOnClickListener {
            refreshContent()
        }
        
        // Long click sur les fruits pour des actions spéciales
        setupFruitInteractions()
    }
    
    private fun openMainMenu() {
        // Animation du bouton
        binding.ivMenu.animate()
            .rotation(180f)
            .setDuration(300)
            .withEndAction {
                binding.ivMenu.rotation = 0f
            }
            .start()
        
        // Ouvrir le menu de navigation
        val intent = Intent(this, NavigationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    private fun openNavigationMenu() {
        // Animation du FAB
        binding.fabMenu.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                binding.fabMenu.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
        
        // Ouvrir le menu de navigation
        val intent = Intent(this, NavigationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
    
    private fun refreshContent() {
        // Animation de rotation pour le bouton refresh
        binding.ivRefresh.animate()
            .rotation(360f)
            .setDuration(500)
            .withEndAction {
                binding.ivRefresh.rotation = 0f
            }
            .start()
        
        // Ici on pourrait recharger le contenu, actualiser les données, etc.
        // Pour l'instant, on fait juste une animation
        animateContentRefresh()
    }
    
    private fun animateContentRefresh() {
        // Animation de rafraîchissement du contenu
        val refreshAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.root.startAnimation(refreshAnimation)
    }
    
    private fun setupFruitInteractions() {
        // Interactions avec les fruits pour des fonctionnalités futures
        // Par exemple : clic sur un fruit pour voir ses informations nutritionnelles
        // ou pour l'ajouter à un smoothie personnalisé
    }
    
    override fun onBackPressed() {
        // Animation de sortie personnalisée
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}