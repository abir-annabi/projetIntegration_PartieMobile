package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class ProgrammeViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _programmes = MutableLiveData<List<Programme>>()
    val programmes: LiveData<List<Programme>> = _programmes
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadAllProgrammes() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getAllProgrammes()
            _isLoading.value = false
            
            result.onSuccess { programmesList ->
                _programmes.value = programmesList
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des programmes"
            }
        }
    }
    
    fun loadProgrammesByObjectif(objectif: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            // ⚠️ getProgrammesByObjectif n'existe pas - on charge tous les programmes
            val result = repository.getAllProgrammes()
            _isLoading.value = false
            
            result.onSuccess { allProgrammes ->
                // Filtrer côté client
                val filtered = allProgrammes.filter { it.objectif.equals(objectif, ignoreCase = true) }
                _programmes.value = filtered
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des programmes"
            }
        }
    }
}
