package com.example.projetintegration.data.models

data class Programme(
    val id: Int,
    val nom: String,
    val description: String,
    val dureeJours: Int,
    val objectif: String,  // "perte-poids", "prise-masse", "maintien", "endurance"
    val plats: List<Plat>?,  // ⚠️ PEUT ÊTRE NULL - Backend peut retourner null
    val activites: List<ActiviteSportive>?,  // ⚠️ PEUT ÊTRE NULL - Backend peut retourner null
    val conseils: List<String>?,
    val imageUrl: String?
)

data class UserProgramme(
    val id: Int,
    val user: User,
    val programme: Programme,
    val dateDebut: String,       // Format "2025-12-01"
    val dateFinPrevue: String,   // Format "2025-12-30"
    val dateFin: String?,        // Peut être null
    val statut: String,          // "EN_COURS", "TERMINE", "ABANDONNE", "PAUSE"
    val poidsDebut: Double?,
    val poidsActuel: Double?,
    val poidsObjectif: Double?
    // ⚠️ PAS de champ "progression" - il vient des Statistiques!
)

data class AssignerProgrammeRequest(
    val programmeId: Int,
    val dateDebut: String,  // Requis selon la doc backend
    val objectifPersonnel: String?  // Selon la doc backend
)

data class ProgressionJournaliere(
    val id: Int,
    val userProgramme: UserProgramme?,  // ⚠️ PEUT ÊTRE NULL
    val date: String,
    val jourProgramme: Int,
    val platsConsommes: List<Plat>?,  // ⚠️ PEUT ÊTRE NULL - Backend peut retourner null
    val activitesRealisees: List<ActiviteSportive>?,  // ⚠️ PEUT ÊTRE NULL - Backend peut retourner null
    val poidsJour: Double?,
    val notes: String?,
    val statutJour: String?,  // ⚠️ PEUT ÊTRE NULL
    val scoreJour: Int?,
    val caloriesConsommees: Int?
)

// ✅ DTO pour enregistrer la progression quotidienne
data class EnregistrerProgressionRequest(
    val date: String?,           // Format "2025-12-01"
    val platIds: List<Int>?,     // IDs des plats consommés
    val activiteIds: List<Int>?, // IDs des activités réalisées
    val poidsJour: Double?,      // Poids du jour
    val notes: String?,          // Notes personnelles
    val userProgrammeId: Int?    // ✅ NOUVEAU: ID du programme spécifique (optionnel)
)

// ✅ Ces modèles ont été supprimés - on utilise EnregistrerProgressionRequest à la place

data class Statistiques(
    val progressionGlobale: Int,
    val tauxCompletion: Int,
    val tauxRepas: Int,
    val tauxActivites: Int,
    val evolutionPhysique: Int,
    val streakActuel: Int,
    val meilleurStreak: Int,
    val joursActifs: Int,
    val jourActuel: Int,
    val joursTotal: Int,
    val joursRestants: Int,
    val poidsDebut: Double?,
    val poidsActuel: Double?,
    val poidsObjectif: Double?,
    val evolutionPoids: Double?,
    val caloriesMoyennes: Int,
    val totalPlatsConsommes: Int,
    val totalActivitesRealisees: Int,
    val badges: List<Badge>
)

data class Badge(
    val id: Int,
    val nom: String,
    val titre: String,
    val description: String,
    val icone: String,
    val dateObtention: String
)

data class User(
    val id: Int,
    val nom: String,
    val prenom: String,
    val numTel: String,
    val adresseEmail: String,
    val dateNaissance: String,
    val taille: Double?,
    val poids: Double?,
    val sexe: String?,
    val objectif: String?,
    val niveauActivite: String?,
    val imc: Double?,
    val age: Int?
)

data class UpdateProfileRequest(
    val nom: String,
    val prenom: String,
    val numTel: String,
    val adresseEmail: String,
    val dateNaissance: String,
    val taille: Double?,
    val poids: Double?,
    val sexe: String?,
    val objectif: String?,
    val niveauActivite: String?
)

data class ChangePasswordRequest(
    val ancienMotDePasse: String,
    val nouveauMotDePasse: String
)


