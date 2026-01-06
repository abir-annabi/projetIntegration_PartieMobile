package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.EnregistrerProgressionRequest
import com.example.projetintegration.data.models.ProgressionJournaliere
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class StatistiquesViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _statistiques = MutableLiveData<Statistiques>()
    val statistiques: LiveData<Statistiques> = _statistiques
    
    private val _programmeActif = MutableLiveData<UserProgramme>()
    val programmeActif: LiveData<UserProgramme> = _programmeActif
    
    private val _progressionAujourdhui = MutableLiveData<ProgressionJournaliere?>()
    val progressionAujourdhui: LiveData<ProgressionJournaliere?> = _progressionAujourdhui
    
    private val _historiqueProgression = MutableLiveData<List<ProgressionJournaliere>>()
    val historiqueProgression: LiveData<List<ProgressionJournaliere>> = _historiqueProgression
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _enregistrementSuccess = MutableLiveData<Boolean>()
    val enregistrementSuccess: LiveData<Boolean> = _enregistrementSuccess
    
    fun loadStatistiques() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getStatistiques()
            _isLoading.value = false
            
            result.onSuccess { stats ->
                _statistiques.value = stats
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des statistiques"
            }
        }
    }
    
    fun loadProgrammeActif() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getProgrammeActif()
            _isLoading.value = false
            
            result.onSuccess { programme ->
                _programmeActif.value = programme
            }.onFailure { exception ->
                _error.value = exception.message ?: "Aucun programme actif"
            }
        }
    }
    
    fun loadProgressionAujourdhui() {
        viewModelScope.launch {
            val result = repository.getProgressionAujourdhui()
            
            result.onSuccess { progression ->
                _progressionAujourdhui.value = progression
            }.onFailure {
                _progressionAujourdhui.value = null
            }
        }
    }
    
    fun loadHistoriqueProgression() {
        viewModelScope.launch {
            val result = repository.getHistoriqueProgression()
            
            result.onSuccess { historique ->
                _historiqueProgression.value = historique
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
    
    fun enregistrerProgression(request: EnregistrerProgressionRequest) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.enregistrerProgression(request)
            _isLoading.value = false
            
            result.onSuccess {
                _enregistrementSuccess.value = true
                // Recharger les statistiques
                loadStatistiques()
                loadProgressionAujourdhui()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de l'enregistrement"
                _enregistrementSuccess.value = false
            }
        }
    }
    
    fun terminerProgramme(onSuccess: () -> Unit) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.terminerProgramme()
            _isLoading.value = false
            
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
}
