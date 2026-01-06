package com.example.projetintegration.data.api

import com.example.projetintegration.data.models.MessageRequest
import com.example.projetintegration.data.models.CommunityMessageResponse
import com.example.projetintegration.data.models.PageResponse
import retrofit2.http.*

interface MessageApiService {
    
    @POST("api/messages")
    suspend fun creerMessage(@Body request: MessageRequest): CommunityMessageResponse
    
    @GET("api/messages")
    suspend fun getMessages(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "recent"
    ): PageResponse<CommunityMessageResponse>
    
    @GET("api/messages/search")
    suspend fun searchMessages(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<CommunityMessageResponse>
    
    @GET("api/messages/{messageId}/replies")
    suspend fun getReplies(@Path("messageId") messageId: Long): List<CommunityMessageResponse>
    
    @POST("api/messages/{messageId}/like")
    suspend fun toggleLike(@Path("messageId") messageId: Long): CommunityMessageResponse
    
    @PUT("api/messages/{messageId}")
    suspend fun modifierMessage(
        @Path("messageId") messageId: Long,
        @Body request: MessageRequest
    ): CommunityMessageResponse
    
    @DELETE("api/messages/{messageId}")
    suspend fun supprimerMessage(@Path("messageId") messageId: Long)
    
    @GET("api/messages/my-messages")
    suspend fun getMyMessages(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<CommunityMessageResponse>
}