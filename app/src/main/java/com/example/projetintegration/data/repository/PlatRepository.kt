package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.Plat

class PlatRepository {
    
    private val platApiService = RetrofitClient.platApiService
    
    suspend fun getAllPlats(): Result<List<Plat>> {
        return try {
            println("PlatRepository: Making API call to getAllPlats")
            val plats = platApiService.getAllPlats()
            println("PlatRepository: API call completed successfully")
            println("PlatRepository: Received response of type: ${plats::class.java.simpleName}")
            println("PlatRepository: Response is null: ${plats == null}")
            
            if (plats != null) {
                println("PlatRepository: Received ${plats.size} plats from API")
                plats.forEachIndexed { index, plat ->
                    println("PlatRepository: Plat $index - ID: ${plat.id}, Nom: ${plat.nom}, Categorie: ${plat.categorie}")
                }
                Result.success(plats)
            } else {
                println("PlatRepository: Received null response from API")
                Result.failure(Exception("Réponse null de l'API"))
            }
        } catch (e: Exception) {
            println("PlatRepository: Exception in getAllPlats - ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getPlatById(id: Int): Result<Plat> {
        return try {
            val plat = platApiService.getPlatById(id)
            Result.success(plat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPlatsByCategorie(categorie: String): Result<List<Plat>> {
        return try {
            val plats = platApiService.getPlatsByCategorie(categorie)
            Result.success(plats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
