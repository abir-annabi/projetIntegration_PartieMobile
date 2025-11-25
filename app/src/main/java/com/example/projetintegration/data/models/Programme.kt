package com.example.projetintegration.data.models

data class Programme(
    val id: Int,
    val nom: String,
    val description: String,
    val dureeJours: Int,
    val objectif: String,  // "perte-poids", "prise-masse", "maintien", "endurance"
    val plats: List<Plat>,
    val activites: List<ActiviteSportive>,
    val conseils: List<String>,
    val imageUrl: String
)

data class UserProgramme(
    val id: Int,
    val user: User,
    val programme: Programme,
    val dateDebut: String,
    val dateFin: String?,
    val statut: String,  // "en-cours", "termine", "abandonne"
    val progression: Int
)

data class User(
    val id: Int,
    val nom: String,
    val prenom: String,
    val numTel: String,
    val adresseEmail: String,
    val dateNaissance: String
)
