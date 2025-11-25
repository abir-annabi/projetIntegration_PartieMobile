package com.example.projetintegration.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "FitLifePrefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_NOM = "userNom"
        private const val KEY_USER_PRENOM = "userPrenom"
    }
    
    fun saveAuthData(token: String, userId: Int, email: String, nom: String, prenom: String) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NOM, nom)
            putString(KEY_USER_PRENOM, prenom)
            apply()
        }
    }
    
    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)
    
    fun getUserId(): Int? {
        val id = sharedPreferences.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }
    
    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)
    
    fun getUserNom(): String? = sharedPreferences.getString(KEY_USER_NOM, null)
    
    fun getUserPrenom(): String? = sharedPreferences.getString(KEY_USER_PRENOM, null)
    
    fun isLoggedIn(): Boolean = getToken() != null
    
    fun clearAuthData() {
        sharedPreferences.edit().clear().apply()
    }
}
