package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.CommunityMessageResponse
import com.example.projetintegration.data.repository.MessageRepository
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {
    
    private val repository = MessageRepository()
    
    private val _messages = MutableLiveData<List<CommunityMessageResponse>>()
    val messages: LiveData<List<CommunityMessageResponse>> = _messages
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> = _createSuccess
    
    private val _replies = MutableLiveData<List<CommunityMessageResponse>>()
    val replies: LiveData<List<CommunityMessageResponse>> = _replies
    
    private var currentPage = 0
    private var isLastPage = false
    private var currentSort = "recent"
    private var isSearchMode = false
    private var currentKeyword = ""
    
    fun loadMessages(sort: String = "recent", refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            isLastPage = false
            currentSort = sort
            isSearchMode = false
        }
        
        if (isLastPage && !refresh) return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.getMessages(currentPage, currentSort)
                
                result.onSuccess { response ->
                    val currentMessages = if (refresh) emptyList() else _messages.value ?: emptyList()
                    val newMessages = currentMessages + response.content
                    
                    // Charger les réponses pour chaque message si nécessaire
                    val messagesWithReplies = newMessages.map { message ->
                        if (message.repliesCount > 0 && message.replies.isNullOrEmpty()) {
                            // Charger les réponses de manière asynchrone
                            loadRepliesForMessage(message)
                            message
                        } else {
                            message
                        }
                    }
                    
                    _messages.value = messagesWithReplies
                    currentPage++
                    isLastPage = response.last
                    
                    android.util.Log.d("MessageViewModel", "Messages chargés: ${response.content.size}, page: $currentPage, dernière: $isLastPage")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur loadMessages: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception loadMessages", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadRepliesForMessage(message: CommunityMessageResponse) {
        viewModelScope.launch {
            try {
                val result = repository.getReplies(message.id)
                result.onSuccess { replies ->
                    // Mettre à jour le message avec ses réponses
                    val currentMessages = _messages.value?.toMutableList() ?: return@onSuccess
                    val index = currentMessages.indexOfFirst { it.id == message.id }
                    if (index != -1) {
                        currentMessages[index] = message.copy(replies = replies)
                        _messages.value = currentMessages
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MessageViewModel", "Erreur chargement réponses pour message ${message.id}", e)
            }
        }
    }
    
    fun creerMessage(contenu: String, anonyme: Boolean, parentId: Long? = null) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.creerMessage(contenu, anonyme, parentId)
                
                result.onSuccess { newMessage ->
                    _createSuccess.value = true
                    
                    // Ajouter le nouveau message en haut de la liste si c'est un message principal
                    if (parentId == null && !isSearchMode) {
                        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
                        currentMessages.add(0, newMessage)
                        _messages.value = currentMessages
                    }
                    
                    android.util.Log.d("MessageViewModel", "Message créé avec succès: ${newMessage.id}")
                }.onFailure { exception ->
                    _error.value = exception.message
                    _createSuccess.value = false
                    android.util.Log.e("MessageViewModel", "Erreur creerMessage: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors de la création: ${e.message}"
                _createSuccess.value = false
                android.util.Log.e("MessageViewModel", "Exception creerMessage", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleLike(messageId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.toggleLike(messageId)
                
                result.onSuccess { updatedMessage ->
                    // Mettre à jour le message dans la liste
                    val currentMessages = _messages.value?.toMutableList() ?: return@onSuccess
                    val index = currentMessages.indexOfFirst { it.id == messageId }
                    if (index != -1) {
                        currentMessages[index] = updatedMessage
                        _messages.value = currentMessages
                    }
                    
                    android.util.Log.d("MessageViewModel", "Like togglé pour message $messageId: ${updatedMessage.likesCount} likes")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur toggleLike: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors du like: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception toggleLike", e)
            }
        }
    }
    
    fun searchMessages(keyword: String) {
        currentKeyword = keyword
        isSearchMode = true
        currentPage = 0
        isLastPage = false
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.searchMessages(keyword, currentPage)
                
                result.onSuccess { response ->
                    _messages.value = response.content
                    currentPage++
                    isLastPage = response.last
                    
                    android.util.Log.d("MessageViewModel", "Recherche '$keyword': ${response.content.size} résultats")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur searchMessages: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors de la recherche: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception searchMessages", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadMoreSearchResults() {
        if (isLastPage || !isSearchMode) return
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val result = repository.searchMessages(currentKeyword, currentPage)
                
                result.onSuccess { response ->
                    val currentMessages = _messages.value ?: emptyList()
                    _messages.value = currentMessages + response.content
                    currentPage++
                    isLastPage = response.last
                }.onFailure { exception ->
                    _error.value = exception.message
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getReplies(messageId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.getReplies(messageId)
                
                result.onSuccess { repliesList ->
                    _replies.value = repliesList
                    android.util.Log.d("MessageViewModel", "Réponses chargées pour message $messageId: ${repliesList.size}")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur getReplies: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement des réponses: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception getReplies", e)
            }
        }
    }
    
    fun modifierMessage(messageId: Long, nouveauContenu: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.modifierMessage(messageId, nouveauContenu)
                
                result.onSuccess { updatedMessage ->
                    // Mettre à jour le message dans la liste
                    val currentMessages = _messages.value?.toMutableList() ?: return@onSuccess
                    val index = currentMessages.indexOfFirst { it.id == messageId }
                    if (index != -1) {
                        currentMessages[index] = updatedMessage
                        _messages.value = currentMessages
                    }
                    
                    android.util.Log.d("MessageViewModel", "Message $messageId modifié avec succès")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur modifierMessage: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors de la modification: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception modifierMessage", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun supprimerMessage(messageId: Long) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.supprimerMessage(messageId)
                
                result.onSuccess {
                    // Supprimer le message de la liste
                    val currentMessages = _messages.value?.toMutableList() ?: return@onSuccess
                    currentMessages.removeAll { it.id == messageId }
                    _messages.value = currentMessages
                    
                    android.util.Log.d("MessageViewModel", "Message $messageId supprimé avec succès")
                }.onFailure { exception ->
                    _error.value = exception.message
                    android.util.Log.e("MessageViewModel", "Erreur supprimerMessage: ${exception.message}")
                }
                
            } catch (e: Exception) {
                _error.value = "Erreur lors de la suppression: ${e.message}"
                android.util.Log.e("MessageViewModel", "Exception supprimerMessage", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}