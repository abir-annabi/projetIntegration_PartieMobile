package com.example.projetintegration.data.model

data class ChatRequest(
    val conversationId: Long?, // null = nouvelle conversation
    val message: String
)