package com.example.projetintegration.data.models

data class ActiviteSportive(
    val id: Int,
    val nom: String,
    val description: String,
    val duree: Int,
    val caloriesBrulees: Int,
    val niveau: String,  // "debutant", "intermediaire", "avance"
    val videoUrl: String
)
