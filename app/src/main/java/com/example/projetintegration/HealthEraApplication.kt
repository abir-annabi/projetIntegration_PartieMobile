package com.example.projetintegration

import android.app.Application
import com.example.projetintegration.data.api.RetrofitClient

class HealthEraApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialiser RetrofitClient avec le contexte de l'application
        RetrofitClient.initialize(this)
    }
}
