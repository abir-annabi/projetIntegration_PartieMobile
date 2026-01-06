package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.R
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityLoginBinding
import com.example.projetintegration.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        // Vérifier si l'utilisateur est déjà connecté
        if (preferencesManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.emailError.observe(this) { error ->
            binding.tvEmailError.text = error
            binding.tvEmailError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.passwordError.observe(this) { error ->
            binding.tvPasswordError.text = error
            binding.tvPasswordError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.btnLogin.text = if (isLoading) "" else getString(R.string.login_button)
        }
        
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                android.util.Log.d("LoginActivity", "Login réussi - Token: ${response.token.take(20)}...")
                android.util.Log.d("LoginActivity", "UserId: ${response.userId}, Email: ${response.adresseEmail}")
                
                preferencesManager.saveAuthData(
                    response.token,
                    response.userId,
                    response.adresseEmail,
                    response.nom,
                    response.prenom
                )
                
                // Vérifier que le token est bien sauvegardé
                val savedToken = preferencesManager.getToken()
                android.util.Log.d("LoginActivity", "Token sauvegardé: ${savedToken?.take(20)}...")
                
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            }
            result.onFailure { exception ->
                android.util.Log.e("LoginActivity", "Erreur login: ${exception.message}", exception)
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }
        
        binding.tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    
    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
