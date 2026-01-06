package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.MessageRequest
import com.example.projetintegration.data.models.CommunityMessageResponse
import com.example.projetintegration.data.models.PageResponse
import com.example.projetintegration.utils.NetworkErrorHandler

class MessageRepository {
    
    private val messageApiService = RetrofitClient.messageApiService
    
    suspend fun creerMessage(contenu: String, anonyme: Boolean, parentId: Long? = null): Result<CommunityMessageResponse> {
        return try {
            val request = MessageRequest(contenu, anonyme, parentId)
            val message = messageApiService.creerMessage(request)
            android.util.Log.d("MessageRepository", "Message créé: ${message.id}")
            Result.success(message)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur création message: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getMessages(page: Int = 0, sort: String = "recent"): Result<PageResponse<CommunityMessageResponse>> {
        return try {
            val response = messageApiService.getMessages(page = page, sort = sort)
            android.util.Log.d("MessageRepository", "Messages chargés: ${response.content.size} sur page $page")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur chargement messages: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun searchMessages(keyword: String, page: Int = 0): Result<PageResponse<CommunityMessageResponse>> {
        return try {
            val response = messageApiService.searchMessages(keyword, page)
            android.util.Log.d("MessageRepository", "Recherche '$keyword': ${response.content.size} résultats")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur recherche messages: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getReplies(messageId: Long): Result<List<CommunityMessageResponse>> {
        return try {
            val replies = messageApiService.getReplies(messageId)
            android.util.Log.d("MessageRepository", "Réponses chargées pour message $messageId: ${replies.size}")
            Result.success(replies)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur chargement réponses: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun toggleLike(messageId: Long): Result<CommunityMessageResponse> {
        return try {
            val updatedMessage = messageApiService.toggleLike(messageId)
            android.util.Log.d("MessageRepository", "Like togglé pour message $messageId: ${updatedMessage.likesCount} likes")
            Result.success(updatedMessage)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur toggle like: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun modifierMessage(messageId: Long, contenu: String): Result<CommunityMessageResponse> {
        return try {
            val request = MessageRequest(contenu)
            val updatedMessage = messageApiService.modifierMessage(messageId, request)
            android.util.Log.d("MessageRepository", "Message $messageId modifié")
            Result.success(updatedMessage)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur modification message: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun supprimerMessage(messageId: Long): Result<Unit> {
        return try {
            messageApiService.supprimerMessage(messageId)
            android.util.Log.d("MessageRepository", "Message $messageId supprimé")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur suppression message: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getMyMessages(page: Int = 0): Result<PageResponse<CommunityMessageResponse>> {
        return try {
            val response = messageApiService.getMyMessages(page)
            android.util.Log.d("MessageRepository", "Mes messages chargés: ${response.content.size}")
            Result.success(response)
        } catch (e: Exception) {
            android.util.Log.e("MessageRepository", "Erreur chargement mes messages: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
}