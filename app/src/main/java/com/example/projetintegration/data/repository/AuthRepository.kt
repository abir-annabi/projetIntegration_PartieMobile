package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.AuthenticationRequest
import com.example.projetintegration.data.models.AuthenticationResponse
import com.example.projetintegration.data.models.InscriptionRequest
import com.example.projetintegration.data.models.MessageResponse
import com.google.gson.Gson
import retrofit2.Response

class AuthRepository {
    //un pont entre Retrofit et ta logique métier.
    //✔ envoyer les requêtes d'inscription et de login et ✔ récupérer les réponses du serveur
    private val apiService = RetrofitClient.authApiService

    suspend fun inscription(request: InscriptionRequest): Result<AuthenticationResponse> {
        return try {
            val response = apiService.inscription(request)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur réseau: ${e.message}"))
        }
    }

    suspend fun authentification(request: AuthenticationRequest): Result<AuthenticationResponse> {
        return try {
            android.util.Log.d("AuthRepository", "Tentative de connexion avec: ${request.adresseEmail}")
            android.util.Log.d("AuthRepository", "Request object: $request")
            val response = apiService.authentification(request)
            handleResponse(response)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Exception lors de l'authentification: ${e.message}", e)
            Result.failure(Exception("Erreur réseau: ${e.message}"))
        }
    }

    private fun handleResponse(response: Response<AuthenticationResponse>): Result<AuthenticationResponse> {
        return if (response.isSuccessful && response.body() != null) {
            android.util.Log.d("AuthRepository", "Login réussi: ${response.body()}")
            Result.success(response.body()!!)
        } else {
            android.util.Log.e("AuthRepository", "Erreur login: Code ${response.code()}")
            val errorMessage = try {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepository", "Corps erreur brut: $errorBody")
                
                // Vérifier si la réponse est vide ou invalide
                if (errorBody.isNullOrBlank()) {
                    android.util.Log.e("AuthRepository", "Corps de réponse vide")
                    when (response.code()) {
                        401 -> "Email ou mot de passe incorrect"
                        403 -> "Accès refusé"
                        404 -> "Service non trouvé - vérifiez l'URL du serveur"
                        500 -> "Erreur serveur interne"
                        else -> "Erreur inconnue (${response.code()})"
                    }
                } else {
                    // Tenter de parser le JSON d'erreur
                    val messageResponse = Gson().fromJson(errorBody, com.example.projetintegration.data.models.MessageResponse::class.java)
                    messageResponse.message ?: "Erreur inconnue"
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Erreur parsing JSON: ${e.message}", e)
                when (response.code()) {
                    401 -> "Email ou mot de passe incorrect"
                    403 -> "Accès refusé"
                    404 -> "Service non trouvé - vérifiez que le serveur est démarré"
                    500 -> "Erreur serveur interne"
                    else -> "Erreur de communication avec le serveur (${response.code()})"
                }
            }
            Result.failure(Exception(errorMessage))
        }
    }
}
