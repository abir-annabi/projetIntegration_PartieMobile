package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.R
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityProfileBinding
import com.example.projetintegration.ui.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        
        // Vérifier si l'utilisateur est connecté
        if (!preferencesManager.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        setupObservers()
        setupClickListeners()
        loadProfile()
    }
    
    override fun onResume() {
        super.onResume()
        loadProfile() // Recharger le profil quand on revient de l'édition
    }
    
    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            binding.tvUserName.text = "${user.prenom} ${user.nom}"
            binding.tvUserEmail.text = user.adresseEmail
            binding.tvNomValue.text = user.nom
            binding.tvPrenomValue.text = user.prenom
            binding.tvEmailValue.text = user.adresseEmail
            
            // Afficher les caractéristiques physiques
            binding.tvTailleValue.text = user.taille?.let { "$it m" } ?: "Non renseigné"
            binding.tvPoidsValue.text = user.poids?.let { "$it kg" } ?: "Non renseigné"
            binding.tvSexeValue.text = formatSexe(user.sexe)
            
            // Afficher l'IMC
            if (user.imc != null) {
                binding.tvImcValue.text = String.format("%.2f", user.imc)
                binding.tvImcCategory.text = getIMCCategory(user.imc)
                binding.tvImcCategory.visibility = View.VISIBLE
            } else {
                binding.tvImcValue.text = "Non calculable"
                binding.tvImcCategory.visibility = View.GONE
            }
            
            // Afficher l'âge
            binding.tvAgeValue.text = user.age?.let { "$it ans" } ?: "Non calculé"
            
            // Afficher les objectifs
            binding.tvObjectifValue.text = formatObjectif(user.objectif)
            binding.tvNiveauActiviteValue.text = formatNiveauActivite(user.niveauActivite)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        
        binding.btnLogout.setOnClickListener {
            preferencesManager.clearAuthData()
            navigateToLogin()
        }
    }
    
    private fun loadProfile() {
        // Vérifier que le token existe
        val token = preferencesManager.getToken()
        android.util.Log.d("ProfileActivity", "Token disponible: ${token != null}")
        if (token != null) {
            android.util.Log.d("ProfileActivity", "Token: ${token.take(20)}...")
        }
        
        // Le backend identifie l'utilisateur via le token JWT
        viewModel.loadProfile()
    }
    
    private fun formatSexe(sexe: String?): String {
        return when (sexe) {
            "HOMME" -> "Homme"
            "FEMME" -> "Femme"
            else -> "Non renseigné"
        }
    }
    
    private fun formatObjectif(objectif: String?): String {
        return when (objectif) {
            "PERTE_POIDS" -> "Perte de poids"
            "PRISE_MASSE" -> "Prise de masse"
            "MAINTIEN" -> "Maintien"
            else -> "Non défini"
        }
    }
    
    private fun formatNiveauActivite(niveau: String?): String {
        return when (niveau) {
            "SEDENTAIRE" -> "Sédentaire"
            "LEGER" -> "Léger"
            "MODERE" -> "Modéré"
            "INTENSE" -> "Intense"
            "TRES_INTENSE" -> "Très intense"
            else -> "Non défini"
        }
    }
    
    private fun getIMCCategory(imc: Double): String {
        return when {
            imc < 18.5 -> "Insuffisance pondérale"
            imc < 25 -> "Poids normal"
            imc < 30 -> "Surpoids"
            imc < 35 -> "Obésité modérée"
            imc < 40 -> "Obésité sévère"
            else -> "Obésité morbide"
        }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
