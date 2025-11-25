package com.example.projetintegration.data.models

data class InscriptionRequest(
    val nom: String,
    val prenom: String,
    val numTel: String,
    val adresseEmail: String,
    val motDePasse: String,
    val dateNaissance: String
)
