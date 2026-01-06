package com.example.projetintegration.data.model

data class OllamaStatusResponse(
    val available: Boolean,
    val message: String,
    val models: List<String>?
)