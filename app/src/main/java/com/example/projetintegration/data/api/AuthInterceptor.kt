package com.example.projetintegration.data.api

import android.content.Context
import com.example.projetintegration.data.preferences.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferencesManager = PreferencesManager(context)
        val token = preferencesManager.getToken()
        
        val requestBuilder = chain.request().newBuilder()
        
        // Ajouter le token si disponible
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
