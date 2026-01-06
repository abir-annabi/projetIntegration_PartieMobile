package com.example.projetintegration.data.models

import com.google.gson.annotations.SerializedName

data class Plat(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nom")
    val nom: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("ingredients")
    val ingredients: List<String>,
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("categorie")
    val categorie: String,  // "petit-dejeuner", "dejeuner", "diner", "collation"
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("tempsPreparation")
    val tempsPreparation: Int
)
