package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ActivityPlatDetailBinding
import kotlinx.coroutines.launch

class PlatDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlatDetailBinding
    private var platId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        platId = intent.getIntExtra("PLAT_ID", 0)
        
        setupClickListeners()
        loadPlatDetail()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadPlatDetail() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val plat = RetrofitClient.platApiService.getPlatById(platId)
                displayPlatDetail(plat)
                binding.progressBar.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@PlatDetailActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
    
    private fun displayPlatDetail(plat: Plat) {
        binding.tvNom.text = plat.nom
        binding.tvCalories.text = "${plat.calories} cal"
        binding.tvTemps.text = "${plat.tempsPreparation} min"
        binding.tvCategorie.text = formatCategorie(plat.categorie)
        
        // Afficher les ingrédients
        val ingredientsText = plat.ingredients.joinToString("\n") { "• $it" }
        binding.tvIngredients.text = ingredientsText
        
        // Afficher la recette
        binding.tvRecette.text = plat.description
    }
    
    private fun formatCategorie(categorie: String): String {
        return when (categorie) {
            "petit-dejeuner" -> "Petit-déjeuner"
            "dejeuner" -> "Déjeuner"
            "diner" -> "Dîner"
            "collation" -> "Collation"
            else -> categorie
        }
    }
}
