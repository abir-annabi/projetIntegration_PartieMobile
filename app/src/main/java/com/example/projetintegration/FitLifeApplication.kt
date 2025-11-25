package com.example.projetintegration

import android.app.Application
import com.example.projetintegration.data.api.RetrofitClient

class FitLifeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialiser RetrofitClient avec le contexte de l'application
        RetrofitClient.initialize(this)
    }
}
