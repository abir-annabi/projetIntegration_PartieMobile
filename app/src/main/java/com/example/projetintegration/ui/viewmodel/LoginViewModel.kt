package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.AuthenticationRequest
import com.example.projetintegration.data.models.AuthenticationResponse
import com.example.projetintegration.data.repository.AuthRepository
import com.example.projetintegration.utils.ValidationUtils
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    
    private val repository = AuthRepository()
    
    private val _loginResult = MutableLiveData<Result<AuthenticationResponse>>()
    val loginResult: LiveData<Result<AuthenticationResponse>> = _loginResult
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) {
            return
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            val request = AuthenticationRequest(email, password)
            val result = repository.authentification(request)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
    
    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        
        if (!ValidationUtils.isNotEmpty(email)) {
            _emailError.value = "Ce champ est obligatoire"
            isValid = false
        } else if (!ValidationUtils.isValidEmail(email)) {
            _emailError.value = "Email invalide"
            isValid = false
        } else {
            _emailError.value = null
        }
        
        if (!ValidationUtils.isNotEmpty(password)) {
            _passwordError.value = "Ce champ est obligatoire"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        return isValid
    }
}
