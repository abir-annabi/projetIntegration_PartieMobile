package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ActivityPlatDetailBinding
import com.example.projetintegration.ui.adapters.IngredientsModernAdapter
import com.example.projetintegration.ui.viewmodel.PlatDetailViewModel

class PlatDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlatDetailBinding
    private lateinit var viewModel: PlatDetailViewModel
    private lateinit var ingredientsAdapter: IngredientsModernAdapter
    private var platId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[PlatDetailViewModel::class.java]
        platId = intent.getIntExtra("PLAT_ID", 0)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadPlatDetail()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupRecyclerView() {
        ingredientsAdapter = IngredientsModernAdapter()
        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(this@PlatDetailActivity)
            adapter = ingredientsAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupObservers() {
        viewModel.plat.observe(this) { plat ->
            displayPlatDetail(plat)
            binding.contentLayout.visibility = View.VISIBLE
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
    }
    
    private fun setupClickListeners() {
        binding.btnAddToFavorites.setOnClickListener {
            // Animation et feedback
            animateButton(binding.btnAddToFavorites)
            Toast.makeText(this, "❤️ Ajouté aux favoris!", Toast.LENGTH_SHORT).show()
            // TODO: Implémenter la logique des favoris
        }
        
        binding.btnAddToMeal.setOnClickListener {
            // Animation et feedback
            animateButton(binding.btnAddToMeal)
            Toast.makeText(this, "✅ Ajouté au repas!", Toast.LENGTH_SHORT).show()
            // TODO: Implémenter la logique d'ajout au repas
        }
    }
    
    private fun loadPlatDetail() {
        binding.contentLayout.visibility = View.GONE
        viewModel.loadPlat(platId)
    }
    
    private fun displayPlatDetail(plat: Plat) {
        // Set collapsing toolbar title
        binding.collapsingToolbar.title = plat.nom
        
        // Basic info
        binding.tvNom.text = plat.nom
        binding.tvDescription.text = plat.description ?: "Délicieux plat fait maison"
        
        // Nutrition info
        binding.tvCalories.text = plat.calories.toString()
        binding.tvTemps.text = plat.tempsPreparation.toString()
        
        // Category badge
        binding.tvCategoryBadge.text = getCategorieWithEmoji(plat.categorie)
        
        // Rating (calculated based on calories like in the list)
        val rating = calculateRating(plat.calories)
        binding.tvRating.text = rating.toString()
        
        // Difficulty (based on preparation time)
        binding.tvDifficulty.text = getDifficulty(plat.tempsPreparation)
        
        // Ingredients
        binding.tvIngredientsCount.text = "${plat.ingredients.size} items"
        ingredientsAdapter.submitList(plat.ingredients)
        
        // Instructions (using description for now)
        binding.tvInstructions.text = generateInstructions(plat)
        
        // Image according to category
        val imageRes = getCategorieImage(plat.categorie)
        binding.ivPlatImage.setImageResource(imageRes)
    }
    
    private fun getCategorieWithEmoji(categorie: String): String {
        return when (categorie) {
            "petit-dejeuner" -> "🥐 Petit-déjeuner"
            "dejeuner" -> "🥗 Déjeuner"
            "diner" -> "🍲 Dîner"
            "collation" -> "🍎 Collation"
            else -> "🍽️ Plat"
        }
    }
    
    private fun getCategorieImage(categorie: String): Int {
        return when (categorie) {
            "petit-dejeuner" -> R.drawable.omelette
            "dejeuner" -> R.drawable.plat_meat
            "diner" -> R.drawable.tacos
            "collation" -> R.drawable.applesplash
            else -> R.drawable.vegetables
        }
    }
    
    private fun calculateRating(calories: Int): Double {
        return when {
            calories in 200..300 -> (48 + (Math.random() * 3).toInt()) / 10.0
            calories in 150..200 || calories in 300..400 -> (45 + (Math.random() * 3).toInt()) / 10.0
            else -> (40 + (Math.random() * 5).toInt()) / 10.0
        }
    }
    
    private fun getDifficulty(tempsPreparation: Int): String {
        return when {
            tempsPreparation <= 10 -> "Très facile"
            tempsPreparation <= 20 -> "Facile"
            tempsPreparation <= 30 -> "Moyen"
            else -> "Difficile"
        }
    }
    
    private fun generateInstructions(plat: Plat): String {
        // Generate basic instructions based on ingredients and category
        val baseInstructions = when (plat.categorie) {
            "petit-dejeuner" -> "1. Préparez tous les ingrédients\n2. Mélangez délicatement\n3. Servez frais"
            "dejeuner" -> "1. Lavez et préparez les légumes\n2. Mélangez tous les ingrédients\n3. Assaisonnez selon votre goût\n4. Servez immédiatement"
            "diner" -> "1. Préparez les ingrédients\n2. Faites cuire selon les instructions\n3. Laissez reposer quelques minutes\n4. Servez chaud"
            "collation" -> "1. Préparez les fruits\n2. Mélangez avec les autres ingrédients\n3. Servez frais"
            else -> "1. Préparez tous les ingrédients\n2. Suivez les étapes de préparation\n3. Servez selon vos préférences"
        }
        
        return if (plat.description != null && plat.description.length > 50) {
            plat.description
        } else {
            baseInstructions
        }
    }
    
    private fun animateButton(button: com.google.android.material.button.MaterialButton) {
        button.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
}
