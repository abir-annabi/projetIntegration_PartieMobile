package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.models.UserProgramme
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProgrammeApiService {
    
    @GET("api/programmes")
    suspend fun getAllProgrammes(): List<Programme>
    
    @GET("api/programmes/{id}")
    suspend fun getProgrammeById(@Path("id") id: Int): Programme
    
    @GET("api/programmes/objectif/{objectif}")
    suspend fun getProgrammesByObjectif(@Path("objectif") objectif: String): List<Programme>
    
    @POST("api/programmes/{programmeId}/inscrire/{userId}")
    suspend fun inscrireUserAuProgramme(
        @Path("programmeId") programmeId: Int,
        @Path("userId") userId: Int
    ): UserProgramme
    
    @GET("api/programmes/user/{userId}")
    suspend fun getProgrammesUser(@Path("userId") userId: Int): List<UserProgramme>
    
    @PUT("api/programmes/user-programme/{id}/progression")
    suspend fun updateProgression(
        @Path("id") userProgrammeId: Int,
        @Query("progression") progression: Int
    ): UserProgramme
}
