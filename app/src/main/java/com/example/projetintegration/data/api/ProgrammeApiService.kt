package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.AssignerProgrammeRequest
import com.example.projetintegration.data.models.EnregistrerProgressionRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.models.ProgressionJournaliere
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProgrammeApiService {
    
    // ✅ Endpoints qui EXISTENT dans le backend
    
    @GET("api/programmes")
    suspend fun getAllProgrammes(): List<Programme>
    
    @GET("api/programmes/{id}")
    suspend fun getProgrammeById(@Path("id") id: Int): Programme
    
    @POST("api/programmes/assigner")
    suspend fun assignerProgramme(@Body request: AssignerProgrammeRequest): UserProgramme
    
    @GET("api/programmes/actif")
    suspend fun getProgrammeActif(): UserProgramme
    
    @GET("api/programmes/statistiques")
    suspend fun getStatistiques(): Statistiques
    
    @POST("api/progression/enregistrer")
    suspend fun enregistrerProgression(@Body request: EnregistrerProgressionRequest): ProgressionJournaliere
    
    @GET("api/progression/historique")
    suspend fun getHistoriqueProgression(): List<ProgressionJournaliere>
    
    @GET("api/progression/aujourd-hui")
    suspend fun getProgressionAujourdhui(): ProgressionJournaliere
    
    @GET("api/progression/date")
    suspend fun getProgressionByDate(@Query("date") date: String): ProgressionJournaliere
    
    // ✅ NOUVELLE MÉTHODE: Progression pour un programme spécifique
    @GET("api/progression/date")
    suspend fun getProgressionByDateForUserProgramme(
        @Query("date") date: String,
        @Query("userProgrammeId") userProgrammeId: Int
    ): ProgressionJournaliere
    
    @GET("api/programmes/historique")
    suspend fun getHistoriqueProgrammes(): List<UserProgramme>
    
    @GET("api/programmes/user/{id}")
    suspend fun getUserProgrammeById(@Path("id") id: Int): UserProgramme
    
    @PUT("api/programmes/terminer")
    suspend fun terminerProgramme(): MessageResponse
    
    @PUT("api/programmes/pauser")
    suspend fun pauserProgramme(): MessageResponse
    
    @PUT("api/programmes/reprendre")
    suspend fun reprendreProgramme(): MessageResponse
    
    @POST("api/programmes/user/{id}/supprimer")
    suspend fun supprimerProgramme(@Path("id") userProgrammeId: Int): MessageResponse
}
