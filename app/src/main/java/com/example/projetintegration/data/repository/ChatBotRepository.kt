package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.model.*
import com.example.projetintegration.data.models.MessageResponse

class ChatBotRepository {
    
    private val chatBotApiService = RetrofitClient.chatBotApiService
    
    suspend fun sendMessage(userId: Long, request: ChatRequest): Result<ChatResponse> {
        return try {
            val response = chatBotApiService.sendMessage(userId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversations(userId: Long): Result<List<Conversation>> {
        return try {
            val conversations = chatBotApiService.getConversations(userId)
            Result.success(conversations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversation(conversationId: Long): Result<Conversation> {
        return try {
            val conversation = chatBotApiService.getConversation(conversationId)
            Result.success(conversation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteConversation(conversationId: Long): Result<MessageResponse> {
        return try {
            val response = chatBotApiService.deleteConversation(conversationId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}