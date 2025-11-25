package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.databinding.ActivityProgrammesBinding
import com.example.projetintegration.ui.adapters.ProgrammesAdapter
import kotlinx.coroutines.launch

class ProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammesBinding
    private lateinit var programmesAdapter: ProgrammesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        loadProgrammes()
    }
    
    private fun setupRecyclerView() {
        programmesAdapter = ProgrammesAdapter { programme ->
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
    }
    
    private fun loadProgrammes() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val programmes = RetrofitClient.programmeApiService.getAllProgrammes()
                programmesAdapter.submitList(programmes)
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ProgrammesActivity,
                    "Erreur: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
