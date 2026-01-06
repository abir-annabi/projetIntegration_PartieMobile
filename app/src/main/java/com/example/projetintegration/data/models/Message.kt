package com.example.projetintegration.data.models

data class MessageRequest(
    val contenu: String,
    val anonyme: Boolean = false,
    val parentMessageId: Long? = null
)

data class CommunityMessageResponse(
    val id: Long,
    val contenu: String,
    val anonyme: Boolean,
    val dateCreation: String,
    val dateModification: String?,
    val nomAuteur: String?,
    val prenomAuteur: String?,
    val nomAnonyme: String?,
    val likesCount: Int,
    val repliesCount: Int,
    val likedByCurrentUser: Boolean,
    val parentMessageId: Long?,
    val replies: List<CommunityMessageResponse>?,
    val isOwner: Boolean,
    val isDeleted: Boolean
)