package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.ChangePasswordRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.UpdateProfileRequest
import com.example.projetintegration.data.models.User

class UserRepository {
    
    private val userApiService = RetrofitClient.userApiService
    
    // Le backend identifie l'utilisateur via le token JWT
    // Plus besoin de passer userId
    
    suspend fun getProfile(): Result<User> {
        return try {
            android.util.Log.d("UserRepository", "Appel API getProfile...")
            val user = userApiService.getProfile()
            android.util.Log.d("UserRepository", "Profil reçu: ${user.nom} ${user.prenom}")
            Result.success(user)
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                500 -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    android.util.Log.e("UserRepository", "Erreur serveur 500 - Endpoint profile non configuré")
                    android.util.Log.e("UserRepository", "Détail erreur: $errorBody")
                    
                    if (errorBody?.contains("No static resource") == true) {
                        Result.failure(Exception("⚠️ Endpoint profile non configuré sur le serveur. Contactez l'administrateur."))
                    } else {
                        Result.failure(Exception("Erreur serveur temporaire. Veuillez réessayer plus tard."))
                    }
                }
                401 -> {
                    android.util.Log.e("UserRepository", "Token expiré ou invalide")
                    Result.failure(Exception("Session expirée. Veuillez vous reconnecter."))
                }
                403 -> {
                    android.util.Log.e("UserRepository", "Accès refusé")
                    Result.failure(Exception("Accès refusé. Vérifiez vos permissions."))
                }
                else -> {
                    android.util.Log.e("UserRepository", "Erreur HTTP ${e.code()}: ${e.message()}")
                    Result.failure(Exception("Erreur réseau (${e.code()}). Vérifiez votre connexion."))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Erreur getProfile: ${e.message}", e)
            Result.failure(Exception("Erreur de connexion. Vérifiez votre réseau."))
        }
    }
    
    suspend fun updateProfile(request: UpdateProfileRequest): Result<User> {
        return try {
            val user = userApiService.updateProfile(request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(request: ChangePasswordRequest): Result<MessageResponse> {
        return try {
            val response = userApiService.changePassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAccount(): Result<MessageResponse> {
        return try {
            val response = userApiService.deleteAccount()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
