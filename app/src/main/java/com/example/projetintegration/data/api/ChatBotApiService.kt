package com.example.projetintegration.data.api

import com.example.projetintegration.data.model.*
import com.example.projetintegration.data.models.MessageResponse
import retrofit2.http.*

interface ChatBotApiService {
    
    @POST("api/chatbot/message/{userId}")
    suspend fun sendMessage(
        @Path("userId") userId: Long,
        @Body request: ChatRequest
    ): ChatResponse
    
    @GET("api/chatbot/conversations/{userId}")
    suspend fun getConversations(
        @Path("userId") userId: Long
    ): List<Conversation>
    
    @GET("api/chatbot/conversation/{conversationId}")
    suspend fun getConversation(
        @Path("conversationId") conversationId: Long
    ): Conversation
    
    @DELETE("api/chatbot/conversation/{conversationId}")
    suspend fun deleteConversation(
        @Path("conversationId") conversationId: Long
    ): MessageResponse
    
    // Nouveaux endpoints Ollama
    @GET("api/ollama/status")
    suspend fun checkOllamaStatus(): OllamaStatusResponse
    
    @POST("api/ollama/test")
    suspend fun testOllamaGeneration(
        @Body request: OllamaTestRequest
    ): OllamaTestResponse
}