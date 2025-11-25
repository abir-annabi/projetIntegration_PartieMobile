package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityMesProgrammesBinding
import com.example.projetintegration.ui.adapters.MesProgrammesAdapter
import kotlinx.coroutines.launch

class MesProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMesProgrammesBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mesProgrammesAdapter: MesProgrammesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesProgrammesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        
        setupRecyclerView()
        setupClickListeners()
        loadMesProgrammes()
    }
    
    private fun setupRecyclerView() {
        mesProgrammesAdapter = MesProgrammesAdapter { userProgramme, newProgression ->
            updateProgression(userProgramme.id, newProgression)
        }
        
        binding.rvMesProgrammes.apply {
            layoutManager = LinearLayoutManager(this@MesProgrammesActivity)
            adapter = mesProgrammesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadMesProgrammes() {
        val userId = preferencesManager.getUserId()
        if (userId == null) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val mesProgrammes = RetrofitClient.programmeApiService.getProgrammesUser(userId)
                
                if (mesProgrammes.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvMesProgrammes.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvMesProgrammes.visibility = View.VISIBLE
                    mesProgrammesAdapter.submitList(mesProgrammes)
                }
                
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@MesProgrammesActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun updateProgression(userProgrammeId: Int, newProgression: Int) {
        lifecycleScope.launch {
            try {
                RetrofitClient.programmeApiService.updateProgression(userProgrammeId, newProgression)
                loadMesProgrammes() // Rafraîchir la liste
                Toast.makeText(
                    this@MesProgrammesActivity,
                    "Progression mise à jour!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MesProgrammesActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
