package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.model.OllamaStatusResponse
import com.example.projetintegration.data.model.OllamaTestRequest
import com.example.projetintegration.data.model.OllamaTestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OllamaRepository {
    
    private val api = RetrofitClient.chatBotApiService
    
    suspend fun checkOllamaStatus(): Result<OllamaStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.checkOllamaStatus()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun testOllamaGeneration(prompt: String): Result<OllamaTestResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = OllamaTestRequest(prompt)
                val response = api.testOllamaGeneration(request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}