package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.AuthenticationResponse
import com.example.projetintegration.data.models.InscriptionRequest
import com.example.projetintegration.data.repository.AuthRepository
import com.example.projetintegration.utils.ValidationUtils
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    
    private val repository = AuthRepository()
    
    private val _signupResult = MutableLiveData<Result<AuthenticationResponse>>()
    val signupResult: LiveData<Result<AuthenticationResponse>> = _signupResult
    
    private val _nomError = MutableLiveData<String?>()
    val nomError: LiveData<String?> = _nomError
    
    private val _prenomError = MutableLiveData<String?>()
    val prenomError: LiveData<String?> = _prenomError
    
    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError
    
    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    private val _dateError = MutableLiveData<String?>()
    val dateError: LiveData<String?> = _dateError
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun signup(
        nom: String,
        prenom: String,
        phone: String,
        email: String,
        password: String,
        dateNaissance: String
    ) {
        if (!validateInputs(nom, prenom, phone, email, password, dateNaissance)) {
            return
        }
        
        _isLoading.value = true
        viewModelScope.launch {
            val request = InscriptionRequest(nom, prenom, phone, email, password, dateNaissance)
            val result = repository.inscription(request)
            _signupResult.value = result
            _isLoading.value = false
        }
    }
    
    private fun validateInputs(
        nom: String,
        prenom: String,
        phone: String,
        email: String,
        password: String,
        dateNaissance: String
    ): Boolean {
        var isValid = true
        
        if (!ValidationUtils.isNotEmpty(nom)) {
            _nomError.value = "Ce champ est obligatoire"
            isValid = false
        } else {
            _nomError.value = null
        }
        
        if (!ValidationUtils.isNotEmpty(prenom)) {
            _prenomError.value = "Ce champ est obligatoire"
            isValid = false
        } else {
            _prenomError.value = null
        }
        
        if (!ValidationUtils.isNotEmpty(phone)) {
            _phoneError.value = "Ce champ est obligatoire"
            isValid = false
        } else if (!ValidationUtils.isValidPhone(phone)) {
            _phoneError.value = "Numéro de téléphone invalide (format: 06XXXXXXXX)"
            isValid = false
        } else {
            _phoneError.value = null
        }
        
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
        } else if (!ValidationUtils.isValidPassword(password)) {
            _passwordError.value = "Le mot de passe doit contenir au moins 6 caractères"
            isValid = false
        } else {
            _passwordError.value = null
        }
        
        if (!ValidationUtils.isNotEmpty(dateNaissance)) {
            _dateError.value = "Ce champ est obligatoire"
            isValid = false
        } else if (!ValidationUtils.isValidDate(dateNaissance)) {
            _dateError.value = "Date de naissance invalide"
            isValid = false
        } else {
            _dateError.value = null
        }
        
        return isValid
    }
}
