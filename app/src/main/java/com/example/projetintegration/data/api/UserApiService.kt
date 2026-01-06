package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.ChangePasswordRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.UpdateProfileRequest
import com.example.projetintegration.data.models.User
import retrofit2.http.*

interface UserApiService {
    
    // Le backend utilise SecurityUtils.getCurrentUserId() pour identifier l'utilisateur
    // Plus besoin de passer userId en paramètre
    
    @GET("api/user/profile")
    suspend fun getProfile(): User
    
    @PUT("api/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): User
    
    @PUT("api/user/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): MessageResponse
    
    @DELETE("api/user/profile")
    suspend fun deleteAccount(): MessageResponse
}
