package com.example.projetintegration.data.model

data class Message(
    val id: Long,
    val role: String, // "user" ou "assistant"
    val contenu: String,
    val timestamp: String
)