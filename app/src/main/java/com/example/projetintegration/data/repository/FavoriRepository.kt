package com.example.projetintegration.data.repository

import android.util.Log
import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.*
import com.example.projetintegration.utils.NetworkErrorHandler

class FavoriRepository {
    
    private val favoriApiService = RetrofitClient.favoriApiService
    
    // ===== PROGRAMMES FAVORIS =====
    
    suspend fun toggleFavoriProgramme(programmeId: Long): Result<FavoriResponse> {
        return try {
            Log.d("FavoriRepository", "🔄 Toggle favori programme ID: $programmeId")
            Log.d("FavoriRepository", "   - URL: POST api/favoris/programmes/$programmeId")
            Log.d("FavoriRepository", "   - Service: ${favoriApiService::class.java.simpleName}")
            
            val response = favoriApiService.toggleFavoriProgramme(programmeId)
            
            Log.d("FavoriRepository", "✅ Toggle réussi: ${response.message}, isFavorite: ${response.isFavorite}")
            Log.d("FavoriRepository", "   - Response success: ${response.success}")
            Log.d("FavoriRepository", "   - Total favorites: ${response.totalFavorites}")
            
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur toggle favori programme: ${e.message}", e)
            Log.e("FavoriRepository", "   - Exception type: ${e::class.java.simpleName}")
            Log.e("FavoriRepository", "   - Cause: ${e.cause?.message}")
            
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun isProgrammeFavorite(programmeId: Long): Result<Boolean> {
        return try {
            Log.d("FavoriRepository", "🔍 Vérification statut favori programme ID: $programmeId")
            val response = favoriApiService.getProgrammeFavoriteStatus(programmeId)
            Log.d("FavoriRepository", "✅ Statut favori: ${response.isFavorite}")
            Result.success(response.isFavorite)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur vérification statut favori: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getFavorisProgrammes(page: Int = 0): Result<PageResponse<FavoriProgrammeResponse>> {
        return try {
            Log.d("FavoriRepository", "🔄 Chargement programmes favoris, page: $page")
            val response = favoriApiService.getFavorisProgrammes(page)
            Log.d("FavoriRepository", "✅ Programmes favoris chargés: ${response.content.size} éléments")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur chargement programmes favoris: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getAllFavorisProgrammes(): Result<List<FavoriProgrammeResponse>> {
        return try {
            Log.d("FavoriRepository", "🔄 Chargement tous les programmes favoris")
            val response = favoriApiService.getAllFavorisProgrammes()
            Log.d("FavoriRepository", "✅ Tous les programmes favoris chargés: ${response.size} éléments")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur chargement tous programmes favoris: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    // ===== PLATS FAVORIS =====
    
    suspend fun toggleFavoriPlat(platId: Long): Result<FavoriResponse> {
        return try {
            Log.d("FavoriRepository", "🔄 Toggle favori plat ID: $platId")
            val response = favoriApiService.toggleFavoriPlat(platId)
            Log.d("FavoriRepository", "✅ Toggle réussi: ${response.message}, isFavorite: ${response.isFavorite}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur toggle favori plat: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun isPlatFavorite(platId: Long): Result<Boolean> {
        return try {
            Log.d("FavoriRepository", "🔍 Vérification statut favori plat ID: $platId")
            val response = favoriApiService.getPlatFavoriteStatus(platId)
            Log.d("FavoriRepository", "✅ Statut favori: ${response.isFavorite}")
            Result.success(response.isFavorite)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur vérification statut favori plat: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getFavorisPlats(page: Int = 0): Result<PageResponse<FavoriPlatResponse>> {
        return try {
            Log.d("FavoriRepository", "🔄 Chargement plats favoris, page: $page")
            val response = favoriApiService.getFavorisPlats(page)
            Log.d("FavoriRepository", "✅ Plats favoris chargés: ${response.content.size} éléments")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur chargement plats favoris: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getAllFavorisPlats(): Result<List<FavoriPlatResponse>> {
        return try {
            Log.d("FavoriRepository", "🔄 Chargement tous les plats favoris")
            val response = favoriApiService.getAllFavorisPlats()
            Log.d("FavoriRepository", "✅ Tous les plats favoris chargés: ${response.size} éléments")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur chargement tous plats favoris: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    // ===== STATISTIQUES =====
    
    suspend fun getFavorisStats(): Result<FavorisStatsResponse> {
        return try {
            Log.d("FavoriRepository", "🔄 Chargement statistiques favoris")
            val response = favoriApiService.getFavorisStats()
            Log.d("FavoriRepository", "✅ Statistiques favoris chargées: ${response.totalFavoris} total")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("FavoriRepository", "❌ Erreur chargement statistiques favoris: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
}