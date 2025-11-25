package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ActivityPlatsBinding
import com.example.projetintegration.ui.adapters.PlatsAdapter
import kotlinx.coroutines.launch

class PlatsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlatsBinding
    private lateinit var platsAdapter: PlatsAdapter
    private var allPlats: List<Plat> = emptyList()
    private var currentCategorie: String = "all"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        loadPlats()
    }
    
    private fun setupRecyclerView() {
        platsAdapter = PlatsAdapter { plat ->
            val intent = Intent(this, PlatDetailActivity::class.java)
            intent.putExtra("PLAT_ID", plat.id)
            startActivity(intent)
        }
        
        binding.rvPlats.apply {
            layoutManager = GridLayoutManager(this@PlatsActivity, 2)
            adapter = platsAdapter
        }
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
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadPlats() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                allPlats = RetrofitClient.platApiService.getAllPlats()
                platsAdapter.submitList(allPlats)
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@PlatsActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun filterByCategorie(categorie: String) {
        currentCategorie = categorie
        updateButtonStates()
        
        val filteredPlats = if (categorie == "all") {
            allPlats
        } else {
            allPlats.filter { it.categorie == categorie }
        }
        
        platsAdapter.submitList(filteredPlats)
    }
    
    private fun updateButtonStates() {
        // Reset all buttons
        listOf(binding.btnAll, binding.btnPetitDejeuner, binding.btnDejeuner, 
               binding.btnDiner, binding.btnCollation).forEach {
            it.alpha = 0.5f
        }
        
        // Highlight selected button
        when (currentCategorie) {
            "all" -> binding.btnAll.alpha = 1f
            "petit-dejeuner" -> binding.btnPetitDejeuner.alpha = 1f
            "dejeuner" -> binding.btnDejeuner.alpha = 1f
            "diner" -> binding.btnDiner.alpha = 1f
            "collation" -> binding.btnCollation.alpha = 1f
        }
    }
}
