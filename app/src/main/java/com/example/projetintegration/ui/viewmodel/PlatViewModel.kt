package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.data.repository.PlatRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

class PlatViewModel : ViewModel() {
    
    private val repository = PlatRepository()
    
    private val _plats = MutableLiveData<List<Plat>>()
    val plats: LiveData<List<Plat>> = _plats
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadAllPlats() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            println("PlatViewModel: Starting to load plats...")
            try {
                withTimeout(30000) { // 30 second timeout
                    val result = repository.getAllPlats()
                    println("PlatViewModel: Repository call completed")
                    
                    _isLoading.value = false
                    
                    result.onSuccess { platsList ->
                        println("PlatViewModel: Success callback - received ${platsList.size} plats")
                        platsList.forEach { plat ->
                            println("PlatViewModel: Plat - ${plat.nom} (${plat.categorie})")
                        }
                        _plats.value = platsList
                        println("PlatViewModel: LiveData updated with ${platsList.size} plats")
                    }.onFailure { exception ->
                        println("PlatViewModel: Failure callback - ${exception.message}")
                        exception.printStackTrace()
                        _error.value = exception.message ?: "Erreur lors du chargement des plats"
                    }
                }
            } catch (e: TimeoutCancellationException) {
                println("PlatViewModel: Timeout error")
                _isLoading.value = false
                _error.value = "Timeout - Vérifiez votre connexion"
            } catch (e: Exception) {
                println("PlatViewModel: Unexpected error - ${e.message}")
                e.printStackTrace()
                _isLoading.value = false
                _error.value = e.message ?: "Erreur inattendue"
            }
        }
    }
    
    fun loadPlatsByCategorie(categorie: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getPlatsByCategorie(categorie)
            _isLoading.value = false
            
            result.onSuccess { platsList ->
                _plats.value = platsList
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des plats"
            }
        }
    }
}
