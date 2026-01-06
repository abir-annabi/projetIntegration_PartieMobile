package com.example.projetintegration.data.model

data class Conversation(
    val id: Long,
    val titre: String,
    val dateCreation: String,
    val derniereActivite: String,
    val messages: List<Message> = emptyList()
)