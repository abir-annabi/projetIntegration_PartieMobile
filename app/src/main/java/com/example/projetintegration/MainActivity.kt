package com.example.projetintegration

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetintegration.ui.activities.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Rediriger directement vers la page de login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}