package com.example.projetintegration.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.model.*
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.data.repository.ChatBotRepository
import kotlinx.coroutines.launch

class ChatBotViewModel(application: Application) : AndroidViewModel(application) {
    
    private val chatBotRepository = ChatBotRepository()
    private val preferencesManager = PreferencesManager(application)
    
    private val _conversations = MutableLiveData<List<Conversation>>()
    val conversations: LiveData<List<Conversation>> = _conversations
    
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _newConversationId = MutableLiveData<Long?>()
    val newConversationId: LiveData<Long?> = _newConversationId
    
    fun loadConversations() {
        val userId = preferencesManager.getUserId()?.toLong()
        if (userId == null) {
            _error.value = "Utilisateur non connecté"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            chatBotRepository.getConversations(userId).fold(
                onSuccess = { conversationsList ->
                    _conversations.value = conversationsList
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = "Erreur lors du chargement des conversations: ${exception.message}"
                }
            )
            _isLoading.value = false
        }
    }
    
    fun loadConversation(conversationId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            chatBotRepository.getConversation(conversationId).fold(
                onSuccess = { conversation ->
                    _messages.value = conversation.messages
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = "Erreur lors du chargement de la conversation: ${exception.message}"
                }
            )
            _isLoading.value = false
        }
    }
    
    fun sendMessage(conversationId: Long?, message: String) {
        val userId = preferencesManager.getUserId()?.toLong()
        if (userId == null) {
            _error.value = "Utilisateur non connecté"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val request = ChatRequest(conversationId, message)
            
            chatBotRepository.sendMessage(userId, request).fold(
                onSuccess = { response ->
                    // Si c'était une nouvelle conversation, sauvegarder l'ID
                    if (conversationId == null) {
                        _newConversationId.value = response.conversationId
                    }
                    
                    // Ajouter les messages à la liste actuelle
                    val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                    
                    // Ajouter le message utilisateur
                    currentMessages.add(
                        Message(
                            id = System.currentTimeMillis(),
                            role = "user",
                            contenu = response.userMessage,
                            timestamp = response.timestamp
                        )
                    )
                    
                    // Ajouter la réponse de l'assistant
                    currentMessages.add(
                        Message(
                            id = System.currentTimeMillis() + 1,
                            role = "assistant",
                            contenu = response.assistantMessage,
                            timestamp = response.timestamp
                        )
                    )
                    
                    _messages.value = currentMessages
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = "Erreur lors de l'envoi du message: ${exception.message}"
                }
            )
            _isLoading.value = false
        }
    }
    
    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            chatBotRepository.deleteConversation(conversationId).fold(
                onSuccess = {
                    // Recharger la liste des conversations
                    loadConversations()
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = "Erreur lors de la suppression: ${exception.message}"
                }
            )
            _isLoading.value = false
        }
    }
}