package com.example.projetintegration.data.model

data class ChatResponse(
    val conversationId: Long,
    val userMessage: String,
    val assistantMessage: String,
    val timestamp: String
)