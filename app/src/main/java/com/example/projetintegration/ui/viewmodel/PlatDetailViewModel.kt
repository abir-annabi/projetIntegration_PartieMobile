package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.data.repository.PlatRepository
import kotlinx.coroutines.launch

class PlatDetailViewModel : ViewModel() {
    
    private val repository = PlatRepository()
    
    private val _plat = MutableLiveData<Plat>()
    val plat: LiveData<Plat> = _plat
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadPlat(platId: Int) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getPlatById(platId)
            _isLoading.value = false
            
            result.onSuccess { platData ->
                _plat.value = platData
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement du plat"
            }
        }
    }
}
