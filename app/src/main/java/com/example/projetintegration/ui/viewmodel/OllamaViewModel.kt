package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.model.OllamaStatusResponse
import com.example.projetintegration.data.repository.OllamaRepository
import kotlinx.coroutines.launch

class OllamaViewModel : ViewModel() {
    
    private val repository = OllamaRepository()
    
    private val _ollamaStatus = MutableLiveData<OllamaStatusResponse?>()
    val ollamaStatus: LiveData<OllamaStatusResponse?> = _ollamaStatus
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun checkOllamaStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.checkOllamaStatus()
                .onSuccess { response ->
                    _ollamaStatus.value = response
                }
                .onFailure { exception ->
                    _error.value = "Erreur de connexion à Ollama: ${exception.message}"
                    _ollamaStatus.value = null
                }
            
            _isLoading.value = false
        }
    }
    
    fun testOllamaGeneration(prompt: String = "Bonjour, comment allez-vous ?") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.testOllamaGeneration(prompt)
                .onSuccess { response ->
                    // Test réussi, on peut mettre à jour le statut
                    _ollamaStatus.value = OllamaStatusResponse(
                        available = true,
                        message = "Test réussi: ${response.response.take(50)}...",
                        models = null
                    )
                }
                .onFailure { exception ->
                    _error.value = "Erreur de test Ollama: ${exception.message}"
                }
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}