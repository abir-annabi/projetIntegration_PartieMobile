package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.Plat
import retrofit2.http.GET
import retrofit2.http.Path

interface PlatApiService {
    
    @GET("api/plats")
    suspend fun getAllPlats(): List<Plat>
    
    @GET("api/plats/{id}")
    suspend fun getPlatById(@Path("id") id: Int): Plat
    
    @GET("api/plats/categorie/{categorie}")
    suspend fun getPlatsByCategorie(@Path("categorie") categorie: String): List<Plat>
}
