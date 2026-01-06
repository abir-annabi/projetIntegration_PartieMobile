package com.example.projetintegration.data.models

data class ChatRequest(
    val conversationId: Long?,
    val message: String
)

data class ChatResponse(
    val conversationId: Long,
    val userMessage: String,
    val assistantMessage: String,
    val timestamp: String
)

data class ChatMessage(
    val id: Long,
    val role: String, // "user" ou "assistant"
    val contenu: String,
    val timestamp: String
)

data class Conversation(
    val id: Long,
    val titre: String,
    val dateCreation: String,
    val derniereActivite: String,
    val messages: List<ChatMessage>
)