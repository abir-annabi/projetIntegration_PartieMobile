package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projetintegration.R
import com.example.projetintegration.databinding.ActivityOllamaTestBinding
import com.example.projetintegration.ui.viewmodel.OllamaViewModel

class OllamaTestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOllamaTestBinding
    private val viewModel: OllamaViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOllamaTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
        
        // Vérifier le statut au démarrage
        viewModel.checkOllamaStatus()
    }
    
    private fun setupUI() {
        binding.apply {
            btnCheckStatus.setOnClickListener {
                viewModel.checkOllamaStatus()
            }
            
            btnTestGeneration.setOnClickListener {
                val prompt = etTestPrompt.text.toString().trim()
                if (prompt.isNotEmpty()) {
                    viewModel.testOllamaGeneration(prompt)
                } else {
                    viewModel.testOllamaGeneration()
                }
            }
            
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.ollamaStatus.observe(this) { status ->
            updateStatusUI(status)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckStatus.isEnabled = !isLoading
            binding.btnTestGeneration.isEnabled = !isLoading
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun updateStatusUI(status: com.example.projetintegration.data.model.OllamaStatusResponse?) {
        binding.apply {
            if (status != null) {
                // Statut disponible
                if (status.available) {
                    ivStatusIcon.setColorFilter(ContextCompat.getColor(this@OllamaTestActivity, R.color.green))
                    tvStatusTitle.text = "✅ Assistant IA Local Actif"
                    tvStatusMessage.text = status.message
                    
                    // Afficher les modèles si disponibles
                    if (!status.models.isNullOrEmpty()) {
                        tvModels.visibility = View.VISIBLE
                        tvModels.text = "Modèles: ${status.models.joinToString(", ")}"
                    } else {
                        tvModels.visibility = View.GONE
                    }
                } else {
                    ivStatusIcon.setColorFilter(ContextCompat.getColor(this@OllamaTestActivity, R.color.red))
                    tvStatusTitle.text = "❌ Assistant IA Indisponible"
                    tvStatusMessage.text = status.message
                    tvModels.visibility = View.GONE
                }
            } else {
                // Statut inconnu
                ivStatusIcon.setColorFilter(ContextCompat.getColor(this@OllamaTestActivity, R.color.gray))
                tvStatusTitle.text = "❓ Statut Inconnu"
                tvStatusMessage.text = "Impossible de vérifier le statut"
                tvModels.visibility = View.GONE
            }
        }
    }
}