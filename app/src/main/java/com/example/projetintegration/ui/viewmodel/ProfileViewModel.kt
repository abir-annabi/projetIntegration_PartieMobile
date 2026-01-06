package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.ChangePasswordRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.UpdateProfileRequest
import com.example.projetintegration.data.models.User
import com.example.projetintegration.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    
    private val repository = UserRepository()
    
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess
    
    private val _passwordChangeResult = MutableLiveData<Result<MessageResponse>>()
    val passwordChangeResult: LiveData<Result<MessageResponse>> = _passwordChangeResult
    
    // Le backend identifie l'utilisateur via le token JWT
    // Plus besoin de passer userId
    
    fun loadProfile() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            android.util.Log.d("ProfileViewModel", "Chargement du profil...")
            val result = repository.getProfile()
            _isLoading.value = false
            
            result.onSuccess { userData ->
                android.util.Log.d("ProfileViewModel", "Profil chargé: ${userData.nom} ${userData.prenom}")
                _user.value = userData
            }.onFailure { exception ->
                android.util.Log.e("ProfileViewModel", "Erreur: ${exception.message}", exception)
                _error.value = exception.message ?: "Erreur lors du chargement du profil"
            }
        }
    }
    
    fun updateProfile(request: UpdateProfileRequest) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.updateProfile(request)
            _isLoading.value = false
            
            result.onSuccess { userData ->
                _user.value = userData
                _updateSuccess.value = true
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de la mise à jour"
                _updateSuccess.value = false
            }
        }
    }
    
    fun changePassword(request: ChangePasswordRequest) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.changePassword(request)
            _isLoading.value = false
            _passwordChangeResult.value = result
        }
    }
    
    fun deleteAccount(onSuccess: () -> Unit) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.deleteAccount()
            _isLoading.value = false
            
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de la suppression du compte"
            }
        }
    }
}
