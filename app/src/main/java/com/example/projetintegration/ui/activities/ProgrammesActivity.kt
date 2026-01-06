package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.databinding.ActivityProgrammesBinding
import com.example.projetintegration.ui.adapters.ProgrammesAdapter
import com.example.projetintegration.ui.viewmodel.ProgrammeViewModel
import com.example.projetintegration.ui.viewmodel.FavoriViewModel

class ProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammesBinding
    private lateinit var viewModel: ProgrammeViewModel
    private lateinit var favoriViewModel: FavoriViewModel
    private lateinit var programmesAdapter: ProgrammesAdapter
    
    // État du filtre favoris
    private var isFilteringFavorites = false
    private var allProgrammes: List<Programme> = emptyList()
    private var favoritesProgrammeIds: Set<Long> = emptySet()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProgrammeViewModel::class.java]
        favoriViewModel = ViewModelProvider(this)[FavoriViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadProgrammes()
    }
    
    private fun setupObservers() {
        viewModel.programmes.observe(this) { programmes ->
            // Stocker tous les programmes
            allProgrammes = programmes
            
            // Appliquer le filtre si nécessaire
            val programmesToShow = if (isFilteringFavorites) {
                filterFavoritesProgrammes(programmes)
            } else {
                programmes
            }
            
            programmesAdapter.submitList(programmesToShow)
            
            // Gestion de l'état vide
            updateEmptyState(programmesToShow.isEmpty())
        }
        
        // Observer les programmes favoris pour le filtre
        favoriViewModel.favorisProgrammes.observe(this) { favoris ->
            favoritesProgrammeIds = favoris.map { it.programmeId }.toSet()
            android.util.Log.d("ProgrammesActivity", "💖 Programmes favoris mis à jour: ${favoritesProgrammeIds.size}")
            
            // Réappliquer le filtre si actif
            if (isFilteringFavorites && allProgrammes.isNotEmpty()) {
                val filteredProgrammes = filterFavoritesProgrammes(allProgrammes)
                programmesAdapter.submitList(filteredProgrammes)
                updateEmptyState(filteredProgrammes.isEmpty())
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            
            // Masquer l'état vide pendant le chargement
            if (isLoading) {
                binding.emptyState.visibility = View.GONE
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
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
        programmesAdapter = ProgrammesAdapter(
            favoriViewModel = favoriViewModel,
            lifecycleOwner = this
        ) { programme ->
            val intent = Intent(this, ProgrammeDetailActivity::class.java)
            intent.putExtra("PROGRAMME_ID", programme.id)
            startActivity(intent)
        }
        
        binding.rvProgrammes.apply {
            layoutManager = LinearLayoutManager(this@ProgrammesActivity)
            adapter = programmesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnMesProgrammes.setOnClickListener {
            startActivity(Intent(this, MesProgrammesActivity::class.java))
        }
        
        // Bouton filtre favoris
        binding.btnFilterFavorites.setOnClickListener {
            toggleFavoritesFilter()
        }
    }
    
    private fun loadProgrammes() {
        viewModel.loadAllProgrammes()
        // Charger les favoris pour le filtre
        favoriViewModel.loadFavorisProgrammes()
    }
    
    // ===== GESTION DU FILTRE FAVORIS =====
    
    private fun toggleFavoritesFilter() {
        isFilteringFavorites = !isFilteringFavorites
        
        android.util.Log.d("ProgrammesActivity", "💖 Toggle filtre favoris: $isFilteringFavorites")
        
        // Mettre à jour l'icône du bouton
        updateFilterButton()
        
        // Charger les favoris si nécessaire
        if (isFilteringFavorites && favoritesProgrammeIds.isEmpty()) {
            favoriViewModel.loadFavorisProgrammes()
        }
        
        // Appliquer le filtre
        val programmesToShow = if (isFilteringFavorites) {
            filterFavoritesProgrammes(allProgrammes)
        } else {
            allProgrammes
        }
        
        programmesAdapter.submitList(programmesToShow)
        updateEmptyState(programmesToShow.isEmpty())
        
        // Message utilisateur
        val message = if (isFilteringFavorites) {
            "Affichage des programmes favoris uniquement"
        } else {
            "Affichage de tous les programmes"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun updateFilterButton() {
        val iconRes = if (isFilteringFavorites) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_outline
        }
        binding.btnFilterFavorites.setImageResource(iconRes)
    }
    
    private fun filterFavoritesProgrammes(programmes: List<Programme>): List<Programme> {
        return programmes.filter { programme ->
            favoritesProgrammeIds.contains(programme.id.toLong())
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty && !viewModel.isLoading.value!!) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvProgrammes.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvProgrammes.visibility = View.VISIBLE
        }
    }
}
