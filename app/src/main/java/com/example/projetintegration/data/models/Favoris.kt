package com.example.projetintegration.data.models

// Response models for favorites API
data class FavoriResponse(
    val success: Boolean,
    val message: String,
    val isFavorite: Boolean,
    val totalFavorites: Long? = null
)

data class FavoriProgrammeResponse(
    val id: Long,
    val programmeId: Long,
    val nom: String,
    val description: String,
    val dureeJours: Int,
    val objectif: String,
    val imageUrl: String?,
    val dateAjout: String,
    val isFavorite: Boolean
)

data class FavoriPlatResponse(
    val id: Long,
    val platId: Long,
    val nom: String,
    val description: String,
    val calories: Int,
    val imageUrl: String?,
    val typePlat: String,
    val dateAjout: String,
    val isFavorite: Boolean
)

data class FavoriteStatusResponse(
    val isFavorite: Boolean,
    val programmeId: Long? = null,
    val platId: Long? = null
)

data class FavorisStatsResponse(
    val totalProgrammesFavoris: Long,
    val totalPlatsFavoris: Long,
    val totalFavoris: Long
)