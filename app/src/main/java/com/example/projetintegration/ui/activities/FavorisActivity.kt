package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.databinding.ActivityFavorisBinding
import com.example.projetintegration.ui.adapters.FavorisProgrammesAdapter
import com.example.projetintegration.ui.adapters.FavorisPlatsAdapter
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import com.google.android.material.tabs.TabLayout

class FavorisActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFavorisBinding
    private lateinit var favoriViewModel: FavoriViewModel
    private lateinit var favorisProgrammesAdapter: FavorisProgrammesAdapter
    private lateinit var favorisPlatsAdapter: FavorisPlatsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavorisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        favoriViewModel = ViewModelProvider(this)[FavoriViewModel::class.java]
        
        setupTabs()
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
        loadFavoris()
    }
    
    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Programmes"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Plats"))
        
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showProgrammesTab()
                    1 -> showPlatsTab()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // Afficher les programmes par défaut
        showProgrammesTab()
    }
    
    private fun setupRecyclerViews() {
        // Adapter pour les programmes favoris
        favorisProgrammesAdapter = FavorisProgrammesAdapter(
            favoriViewModel = favoriViewModel,
            lifecycleOwner = this,
            onProgrammeClick = { favoriProgramme ->
                val intent = Intent(this, ProgrammeDetailActivity::class.java)
                intent.putExtra("PROGRAMME_ID", favoriProgramme.programmeId.toInt())
                startActivity(intent)
            }
        )
        
        // Adapter pour les plats favoris
        favorisPlatsAdapter = FavorisPlatsAdapter(
            favoriViewModel = favoriViewModel,
            lifecycleOwner = this,
            onPlatClick = { favoriPlat ->
                val intent = Intent(this, PlatDetailActivity::class.java)
                intent.putExtra("PLAT_ID", favoriPlat.platId.toInt())
                startActivity(intent)
            }
        )
        
        binding.rvFavoris.layoutManager = LinearLayoutManager(this)
    }
    
    private fun setupObservers() {
        // Observer les programmes favoris
        favoriViewModel.favorisProgrammes.observe(this) { programmes ->
            favorisProgrammesAdapter.submitList(programmes)
            updateEmptyState(programmes.isEmpty(), "programmes")
        }
        
        // Observer les plats favoris
        favoriViewModel.favorisPlats.observe(this) { plats ->
            favorisPlatsAdapter.submitList(plats)
            updateEmptyState(plats.isEmpty(), "plats")
        }
        
        // Observer le loading
        favoriViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observer les erreurs
        favoriViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Erreur: $it", Toast.LENGTH_LONG).show()
                favoriViewModel.clearError()
            }
        }
        
        // Observer les messages de succès
        favoriViewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                favoriViewModel.clearSuccessMessage()
                // Recharger les favoris après modification
                loadFavoris()
            }
        }
        
        // Observer les statistiques
        favoriViewModel.favorisStats.observe(this) { stats ->
            stats?.let {
                binding.tvStatsTitle.text = "Mes Favoris (${stats.totalFavoris})"
                binding.tvStatsProgrammes.text = "${stats.totalProgrammesFavoris} programmes"
                binding.tvStatsPlats.text = "${stats.totalPlatsFavoris} plats"
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun showProgrammesTab() {
        binding.rvFavoris.adapter = favorisProgrammesAdapter
        binding.tvEmptyMessage.text = "Aucun programme favori.\n\nAjoutez des programmes à vos favoris depuis la liste des programmes."
    }
    
    private fun showPlatsTab() {
        binding.rvFavoris.adapter = favorisPlatsAdapter
        binding.tvEmptyMessage.text = "Aucun plat favori.\n\nAjoutez des plats à vos favoris depuis la liste des plats."
    }
    
    private fun updateEmptyState(isEmpty: Boolean, type: String) {
        if (isEmpty && !favoriViewModel.isLoading.value!!) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvFavoris.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvFavoris.visibility = View.VISIBLE
        }
    }
    
    private fun loadFavoris() {
        favoriViewModel.loadFavorisProgrammes()
        favoriViewModel.loadFavorisPlats()
        favoriViewModel.loadFavorisStats()
    }
}