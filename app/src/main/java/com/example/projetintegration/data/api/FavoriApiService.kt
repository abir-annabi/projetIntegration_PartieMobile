package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.*
import retrofit2.http.*

interface FavoriApiService {
    
    // ===== PROGRAMMES FAVORIS =====
    
    @POST("api/favoris/programmes/{programmeId}")
    suspend fun toggleFavoriProgramme(@Path("programmeId") programmeId: Long): FavoriResponse
    
    @GET("api/favoris/programmes/{programmeId}/status")
    suspend fun getProgrammeFavoriteStatus(@Path("programmeId") programmeId: Long): FavoriteStatusResponse
    
    @GET("api/favoris/programmes")
    suspend fun getFavorisProgrammes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<FavoriProgrammeResponse>
    
    @GET("api/favoris/programmes/all")
    suspend fun getAllFavorisProgrammes(): List<FavoriProgrammeResponse>
    
    // ===== PLATS FAVORIS =====
    
    @POST("api/favoris/plats/{platId}")
    suspend fun toggleFavoriPlat(@Path("platId") platId: Long): FavoriResponse
    
    @GET("api/favoris/plats/{platId}/status")
    suspend fun getPlatFavoriteStatus(@Path("platId") platId: Long): FavoriteStatusResponse
    
    @GET("api/favoris/plats")
    suspend fun getFavorisPlats(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<FavoriPlatResponse>
    
    @GET("api/favoris/plats/all")
    suspend fun getAllFavorisPlats(): List<FavoriPlatResponse>
    
    // ===== STATISTIQUES =====
    
    @GET("api/favoris/stats")
    suspend fun getFavorisStats(): FavorisStatsResponse
}