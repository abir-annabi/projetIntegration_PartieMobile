package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ActivityPlatsBinding
import com.example.projetintegration.ui.adapters.PlatsModernAdapter
import com.example.projetintegration.ui.viewmodel.PlatViewModel
import com.example.projetintegration.ui.viewmodel.FavoriViewModel

class PlatsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlatsBinding
    private lateinit var viewModel: PlatViewModel
    private lateinit var favoriViewModel: FavoriViewModel
    private lateinit var platsAdapter: PlatsModernAdapter
    private var allPlats: List<Plat> = emptyList()
    private var currentCategorie: String = "all"
    private var searchQuery: String = ""
    private var showOnlyFavorites = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[PlatViewModel::class.java]
        favoriViewModel = ViewModelProvider(this)[FavoriViewModel::class.java]
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearchBar()
        loadPlats()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupObservers() {
        println("PlatsActivity: Setting up observers on thread: ${Thread.currentThread().name}")
        
        viewModel.plats.observe(this) { plats ->
            println("PlatsActivity: Observer called on thread: ${Thread.currentThread().name}")
            println("PlatsActivity: Received ${plats.size} plats from ViewModel")
            allPlats = plats
            applyFilters()
            updateEmptyState()
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            println("PlatsActivity: Loading state = $isLoading on thread: ${Thread.currentThread().name}")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                println("PlatsActivity: Error = $it on thread: ${Thread.currentThread().name}")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        // Observer les messages de succès des favoris
        favoriViewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                favoriViewModel.clearSuccessMessage()
            }
        }
        
        // Observer les erreurs des favoris
        favoriViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Erreur favoris: $it", Toast.LENGTH_LONG).show()
                favoriViewModel.clearError()
            }
        }
    }
    
    private fun setupRecyclerView() {
        println("PlatsActivity: Setting up RecyclerView")
        
        platsAdapter = PlatsModernAdapter(
            onPlatClick = { plat ->
                val intent = Intent(this, PlatDetailActivity::class.java)
                intent.putExtra("PLAT_ID", plat.id)
                startActivity(intent)
            },
            favoriViewModel = favoriViewModel,
            lifecycleOwner = this
        )
        
        binding.rvPlats.apply {
            println("PlatsActivity: Configuring RecyclerView")
            layoutManager = LinearLayoutManager(this@PlatsActivity)
            adapter = platsAdapter
            // Améliorer les performances
            setHasFixedSize(false) // Changed to false for wrap_content
            setItemViewCacheSize(20)
            
            // Force layout
            post {
                println("PlatsActivity: RecyclerView post - width: $width, height: $height")
                println("PlatsActivity: RecyclerView visibility: $visibility")
                println("PlatsActivity: RecyclerView adapter: ${adapter != null}")
                println("PlatsActivity: RecyclerView layoutManager: ${layoutManager != null}")
            }
        }
        
        println("PlatsActivity: RecyclerView setup completed")
    }
    
    private fun setupClickListeners() {
        binding.btnAll.setOnClickListener {
            filterByCategorie("all")
        }
        
        binding.btnPetitDejeuner.setOnClickListener {
            filterByCategorie("petit-dejeuner")
        }
        
        binding.btnDejeuner.setOnClickListener {
            filterByCategorie("dejeuner")
        }
        
        binding.btnDiner.setOnClickListener {
            filterByCategorie("diner")
        }
        
        binding.btnCollation.setOnClickListener {
            filterByCategorie("collation")
        }
        
        binding.btnFavorites.setOnClickListener {
            toggleFavoritesFilter()
        }
    }
    
    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.lowercase() ?: ""
                println("PlatsActivity: Search query changed to: '$searchQuery'")
                applyFilters()
                updateEmptyState()
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun loadPlats() {
        // Test JSON parsing first
        testJsonParsing()
        
        viewModel.loadAllPlats()
    }
    
    private fun testJsonParsing() {
        try {
            val jsonResponse = """[{"id":1,"nom":"Salade Quinoa & Avocat","description":"Salade quinoa, avocat, pois chiches et citron.","ingredients":["Quinoa","Avocat","Pois chiches","Citron","Concombre"],"calories":220,"categorie":"dejeuner","imageUrl":"https://example.com/images/quinoa_salad.jpg","tempsPreparation":15}]"""
            
            val gson = com.google.gson.Gson()
            val listType = object : com.google.gson.reflect.TypeToken<List<Plat>>() {}.type
            val plats: List<Plat> = gson.fromJson(jsonResponse, listType)
            
            println("PlatsActivity: JSON parsing test - Successfully parsed ${plats.size} plats")
            plats.forEach { plat ->
                println("PlatsActivity: Test plat - ${plat.nom} (${plat.categorie})")
            }
        } catch (e: Exception) {
            println("PlatsActivity: JSON parsing test failed - ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun filterByCategorie(categorie: String) {
        currentCategorie = categorie
        updateButtonStates()
        applyFilters()
        updateEmptyState()
        
        // Animation de feedback
        animateFilterSelection()
    }
    
    private fun applyFilters() {
        var filteredPlats = allPlats
        
        println("PlatsActivity: Applying filters - Total plats: ${allPlats.size}")
        println("PlatsActivity: Current category: '$currentCategorie', Search query: '$searchQuery', Show favorites: $showOnlyFavorites")
        
        // Filtre par catégorie d'abord (toujours actif)
        if (currentCategorie != "all") {
            filteredPlats = filteredPlats.filter { it.categorie == currentCategorie }
            println("PlatsActivity: After category filter: ${filteredPlats.size} plats")
        }
        
        // Filtre par favoris ensuite (sur les plats déjà filtrés par catégorie)
        if (showOnlyFavorites) {
            filteredPlats = applyFavoritesFilter(filteredPlats)
            println("PlatsActivity: After favorites filter: ${filteredPlats.size} plats")
        }
        
        // Filtre par recherche en dernier
        if (searchQuery.isNotEmpty()) {
            filteredPlats = filteredPlats.filter { plat ->
                val matchesName = plat.nom.lowercase().contains(searchQuery)
                val matchesDescription = plat.description?.lowercase()?.contains(searchQuery) == true
                val matchesIngredients = plat.ingredients.any { it.lowercase().contains(searchQuery) }
                
                val matches = matchesName || matchesDescription || matchesIngredients
                if (matches) {
                    println("PlatsActivity: Plat '${plat.nom}' matches search query")
                }
                matches
            }
            println("PlatsActivity: After search filter: ${filteredPlats.size} plats")
        }
        
        println("PlatsActivity: Final filtered plats: ${filteredPlats.size}")
        platsAdapter.submitList(filteredPlats)
        
        // Force RecyclerView to layout
        binding.rvPlats.post {
            binding.rvPlats.requestLayout()
            println("PlatsActivity: Forced RecyclerView layout after filter")
        }
    }
    
    private fun updateButtonStates() {
        // Reset all buttons to outline style
        listOf(binding.btnAll, binding.btnPetitDejeuner, binding.btnDejeuner, 
               binding.btnDiner, binding.btnCollation, binding.btnFavorites).forEach { button ->
            button.backgroundTintList = null
            button.setTextColor(getColor(R.color.primary_gradient_start))
            // Reset to outline style
            button.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
        
        // Highlight selected button
        val selectedButton = when {
            showOnlyFavorites -> binding.btnFavorites
            currentCategorie == "all" -> binding.btnAll
            currentCategorie == "petit-dejeuner" -> binding.btnPetitDejeuner
            currentCategorie == "dejeuner" -> binding.btnDejeuner
            currentCategorie == "diner" -> binding.btnDiner
            currentCategorie == "collation" -> binding.btnCollation
            else -> binding.btnAll
        }
        
        selectedButton.backgroundTintList = getColorStateList(R.color.primary_gradient_start)
        selectedButton.setTextColor(getColor(android.R.color.white))
    }
    
    private fun updateEmptyState() {
        val isEmpty = platsAdapter.itemCount == 0 && viewModel.isLoading.value != true
        println("PlatsActivity: UpdateEmptyState - isEmpty: $isEmpty, itemCount: ${platsAdapter.itemCount}, isLoading: ${viewModel.isLoading.value}")
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvPlats.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun animateFilterSelection() {
        // Animation subtile pour le feedback de sélection
        binding.rvPlats.animate()
            .alpha(0.7f)
            .setDuration(150)
            .withEndAction {
                binding.rvPlats.animate()
                    .alpha(1.0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
    
    private fun toggleFavoritesFilter() {
        showOnlyFavorites = !showOnlyFavorites
        
        if (showOnlyFavorites) {
            // Load favorites from backend
            favoriViewModel.loadFavorisPlats()
        }
        
        updateButtonStates()
        applyFilters()
        updateEmptyState()
        animateFilterSelection()
        
        // Message utilisateur
        val message = if (showOnlyFavorites) {
            "Affichage des plats favoris uniquement"
        } else {
            "Affichage de tous les plats"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun applyFavoritesFilter(plats: List<Plat>): List<Plat> {
        if (!showOnlyFavorites) return plats
        
        // Get current favorites from ViewModel
        val favorisPlats = favoriViewModel.favorisPlats.value ?: emptyList()
        val favoriteIds = favorisPlats.map { it.platId }.toSet()
        
        return plats.filter { plat -> favoriteIds.contains(plat.id.toLong()) }
    }
}
