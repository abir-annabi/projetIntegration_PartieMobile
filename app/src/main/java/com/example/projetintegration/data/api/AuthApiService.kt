package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.AuthenticationRequest
import com.example.projetintegration.data.models.AuthenticationResponse
import com.example.projetintegration.data.models.InscriptionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("api/auth/inscription")
    suspend fun inscription(@Body request: InscriptionRequest): Response<AuthenticationResponse>
    
    @POST("api/auth/authentification")
    suspend fun authentification(@Body request: AuthenticationRequest): Response<AuthenticationResponse>
}
