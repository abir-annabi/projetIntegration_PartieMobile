package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityDiagnosticBinding
import com.example.projetintegration.utils.NetworkErrorHandler
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DiagnosticActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDiagnosticBinding
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosticBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        
        setupClickListeners()
        runDiagnostic()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnRefresh.setOnClickListener {
            runDiagnostic()
        }
        
        binding.btnTestLogin.setOnClickListener {
            testLogin()
        }
        
        binding.btnTestProgrammes.setOnClickListener {
            testProgrammes()
        }
    }
    
    private fun runDiagnostic() {
        binding.tvResults.text = "🔍 Diagnostic en cours...\n\n"
        
        // Test 1: Connectivité réseau
        val isNetworkAvailable = NetworkErrorHandler.isNetworkAvailable(this)
        appendResult("📶 Connexion réseau", if (isNetworkAvailable) "✅ OK" else "❌ Pas de connexion")
        
        // Test 2: Token JWT
        val token = preferencesManager.getToken()
        appendResult("🔑 Token JWT", if (token != null) "✅ Présent (${token.take(20)}...)" else "❌ Absent")
        
        // Test 3: Informations utilisateur
        val userId = preferencesManager.getUserId()
        val email = preferencesManager.getUserEmail()
        appendResult("👤 Utilisateur connecté", if (userId != null) "✅ ID: $userId, Email: $email" else "❌ Non connecté")
        
        // Test 4: Ping serveur
        testServerConnection()
    }
    
    private fun testServerConnection() {
        lifecycleScope.launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:8099/")
                    .build()
                
                val response = client.newCall(request).execute()
                appendResult("🌐 Serveur backend", "✅ Accessible (${response.code})")
                response.close()
            } catch (e: IOException) {
                appendResult("🌐 Serveur backend", "❌ Inaccessible: ${e.message}")
            }
        }
    }
    
    private fun testLogin() {
        binding.tvResults.append("\n🔐 Test de l'endpoint de connexion...\n")
        
        lifecycleScope.launch {
            try {
                // Test avec l'endpoint actuel
                val authService = RetrofitClient.authApiService
                val testRequest = com.example.projetintegration.data.models.AuthenticationRequest(
                    adresseEmail = "test@test.com",
                    motDePasse = "test123"
                )
                
                val response = authService.authentification(testRequest)
                
                when (response.code()) {
                    200, 201 -> {
                        appendResult("✅ SUCCÈS", "Endpoint fonctionne parfaitement!")
                    }
                    400 -> {
                        appendResult("🟡 ENDPOINT OK", "Endpoint existe mais données invalides (normal)")
                    }
                    401 -> {
                        appendResult("🟡 ENDPOINT OK", "Endpoint existe mais identifiants incorrects (normal)")
                    }
                    404 -> {
                        appendResult("❌ 404", "Endpoint n'existe pas - Vérifiez l'URL")
                    }
                    500 -> {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody?.contains("No static resource") == true) {
                            appendResult("❌ MAUVAIS ENDPOINT", "L'endpoint /api/auth/authenticate n'existe pas")
                            appendResult("💡 SUGGESTION", "Essayez /api/auth/authentification ou /api/auth/signin")
                        } else {
                            appendResult("🟡 ERREUR SERVEUR", "Endpoint existe mais erreur interne")
                        }
                    }
                    else -> {
                        appendResult("❓ CODE ${response.code()}", "Réponse inattendue")
                    }
                }
                
            } catch (e: Exception) {
                appendResult("❌ ERREUR RÉSEAU", NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }
    
    private fun testProgrammes() {
        binding.tvResults.append("\n📋 Test des programmes...\n")
        
        lifecycleScope.launch {
            try {
                val programmeService = RetrofitClient.programmeApiService
                val programmes = programmeService.getAllProgrammes()
                appendResult("Programmes", "✅ ${programmes.size} programmes trouvés")
            } catch (e: Exception) {
                appendResult("Programmes", "❌ Erreur: ${NetworkErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun appendResult(test: String, result: String) {
        runOnUiThread {
            binding.tvResults.append("$test: $result\n")
        }
    }
}