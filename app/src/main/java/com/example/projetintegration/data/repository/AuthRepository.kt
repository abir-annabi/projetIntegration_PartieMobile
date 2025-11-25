package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.AuthenticationRequest
import com.example.projetintegration.data.models.AuthenticationResponse
import com.example.projetintegration.data.models.InscriptionRequest
import com.example.projetintegration.data.models.MessageResponse
import com.google.gson.Gson
import retrofit2.Response

class AuthRepository {
    
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
            val response = apiService.authentification(request)
            handleResponse(response)
        } catch (e: Exception) {
            Result.failure(Exception("Erreur réseau: ${e.message}"))
        }
    }
    
    private fun handleResponse(response: Response<AuthenticationResponse>): Result<AuthenticationResponse> {
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            val errorMessage = try {
                val errorBody = response.errorBody()?.string()
                val messageResponse = Gson().fromJson(errorBody, MessageResponse::class.java)
                messageResponse.message
            } catch (e: Exception) {
                "Erreur inconnue"
            }
            Result.failure(Exception(errorMessage))
        }
    }
}
