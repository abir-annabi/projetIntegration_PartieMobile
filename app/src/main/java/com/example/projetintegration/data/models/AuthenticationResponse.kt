package com.example.projetintegration.data.models

data class AuthenticationResponse(
    val token: String,
    val type: String,
    val userId: Int,
    val nom: String,
    val prenom: String,
    val adresseEmail: String
)
