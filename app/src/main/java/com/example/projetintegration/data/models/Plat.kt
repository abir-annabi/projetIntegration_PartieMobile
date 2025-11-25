package com.example.projetintegration.data.models

data class Plat(
    val id: Int,
    val nom: String,
    val description: String,
    val ingredients: List<String>,
    val calories: Int,
    val categorie: String,  // "petit-dejeuner", "dejeuner", "diner", "collation"
    val imageUrl: String,
    val tempsPreparation: Int
)
