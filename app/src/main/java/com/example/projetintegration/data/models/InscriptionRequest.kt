package com.example.projetintegration.data.models

import com.google.gson.annotations.SerializedName

data class InscriptionRequest(
    @SerializedName("nom")
    val nom: String,
    @SerializedName("prenom")
    val prenom: String,
    @SerializedName("numTel")
    val numtel: String,  // Backend expects "numTel"
    @SerializedName("adresseEmail")
    val adresseemail: String,  // Backend expects "adresseEmail"
    @SerializedName("motDePasse")
    val mdp: String,  // Backend expects "motDePasse"
    @SerializedName("dateNaissance")
    val dateNaissance: String
)
