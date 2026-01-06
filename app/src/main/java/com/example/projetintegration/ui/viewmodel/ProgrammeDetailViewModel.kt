package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.AssignerProgrammeRequest
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class ProgrammeDetailViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _programme = MutableLiveData<Programme>()
    val programme: LiveData<Programme> = _programme
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _inscriptionResult = MutableLiveData<Result<UserProgramme>>()
    val inscriptionResult: LiveData<Result<UserProgramme>> = _inscriptionResult
    
    fun loadProgramme(programmeId: Int) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getProgrammeById(programmeId)
            _isLoading.value = false
            
            result.onSuccess { prog ->
                _programme.value = prog
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement du programme"
            }
        }
    }
    
    fun inscrireAuProgramme(programmeId: Int, poidsDebut: Double?, poidsObjectif: Double?) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val request = AssignerProgrammeRequest(
                programmeId = programmeId,
                dateDebut = java.time.LocalDate.now().toString(), // Format "2025-12-28"
                objectifPersonnel = "Objectif personnel" // Valeur par défaut
            )
            val result = repository.assignerProgramme(request)
            _isLoading.value = false
            _inscriptionResult.value = result
        }
    }
}
