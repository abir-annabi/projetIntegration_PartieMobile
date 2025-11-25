package com.example.projetintegration

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.ui.activities.DashboardActivity
import com.example.projetintegration.ui.activities.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val preferencesManager = PreferencesManager(this)
        
        // Rediriger vers le bon écran selon l'état de connexion
        val intent = if (preferencesManager.isLoggedIn()) {
            Intent(this, DashboardActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
}