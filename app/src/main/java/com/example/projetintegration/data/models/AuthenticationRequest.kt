package com.example.projetintegration.data.models

import com.google.gson.annotations.SerializedName

data class AuthenticationRequest(
    @SerializedName("adresseEmail")
    val adresseEmail: String,
    @SerializedName("motDePasse")
    val motDePasse: String
)
