package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.AssignerProgrammeRequest
import com.example.projetintegration.data.models.EnregistrerProgressionRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.models.ProgressionJournaliere
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.utils.NetworkErrorHandler

class ProgrammeRepository {
    
    private val programmeApiService = RetrofitClient.programmeApiService
    
    suspend fun getAllProgrammes(): Result<List<Programme>> {
        return try {
            val programmes = programmeApiService.getAllProgrammes()
            android.util.Log.d("ProgrammeRepository", "Programmes chargés: ${programmes.size}")
            Result.success(programmes)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getAllProgrammes: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getProgrammeById(id: Int): Result<Programme> {
        return try {
            val programme = programmeApiService.getProgrammeById(id)
            android.util.Log.d("ProgrammeRepository", "Programme chargé: ${programme.nom}")
            Result.success(programme)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getProgrammeById($id): ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun assignerProgramme(request: AssignerProgrammeRequest): Result<UserProgramme> {
        return try {
            val userProgramme = programmeApiService.assignerProgramme(request)
            Result.success(userProgramme)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProgrammeActif(): Result<UserProgramme> {
        return try {
            val userProgramme = programmeApiService.getProgrammeActif()
            android.util.Log.d("ProgrammeRepository", "Programme actif: ${userProgramme.programme.nom}")
            Result.success(userProgramme)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getProgrammeActif: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getStatistiques(): Result<Statistiques> {
        return try {
            val stats = programmeApiService.getStatistiques()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun enregistrerProgression(request: EnregistrerProgressionRequest): Result<ProgressionJournaliere> {
        return try {
            val progression = programmeApiService.enregistrerProgression(request)
            Result.success(progression)
        } catch (e: retrofit2.HttpException) {
            // Récupérer le message d'erreur détaillé du serveur
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("ProgrammeRepository", "Erreur HTTP ${e.code()}: $errorBody")
            
            val errorMessage = try {
                val gson = com.google.gson.Gson()
                val messageResponse = gson.fromJson(errorBody, com.example.projetintegration.data.models.MessageResponse::class.java)
                messageResponse.message ?: "Erreur HTTP ${e.code()}"
            } catch (parseException: Exception) {
                "Erreur HTTP ${e.code()}: ${e.message()}"
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur lors de l'enregistrement de progression", e)
            Result.failure(e)
        }
    }
    
    suspend fun getHistoriqueProgression(): Result<List<ProgressionJournaliere>> {
        return try {
            val historique = programmeApiService.getHistoriqueProgression()
            Result.success(historique)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProgressionAujourdhui(): Result<ProgressionJournaliere> {
        return try {
            val progression = programmeApiService.getProgressionAujourdhui()
            Result.success(progression)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProgressionByDate(date: String): Result<ProgressionJournaliere> {
        return try {
            val progression = programmeApiService.getProgressionByDate(date)
            android.util.Log.d("ProgrammeRepository", "Progression chargée pour la date: $date")
            Result.success(progression)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getProgressionByDate($date): ${e.message}", e)
            Result.failure(e)
        }
    }
    
    // ✅ NOUVELLE MÉTHODE: Progression pour un programme spécifique
    suspend fun getProgressionByDateForUserProgramme(date: String, userProgrammeId: Int): Result<ProgressionJournaliere> {
        return try {
            val progression = programmeApiService.getProgressionByDateForUserProgramme(date, userProgrammeId)
            android.util.Log.d("ProgrammeRepository", "Progression chargée pour la date: $date, programme: $userProgrammeId")
            Result.success(progression)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getProgressionByDateForUserProgramme($date, $userProgrammeId): ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getHistoriqueProgrammes(): Result<List<UserProgramme>> {
        return try {
            android.util.Log.d("ProgrammeRepository", "🔄 Appel API: /api/programmes/historique")
            
            val historique = programmeApiService.getHistoriqueProgrammes()
            
            android.util.Log.d("ProgrammeRepository", "✅ Réponse API reçue: ${historique.size} programmes")
            
            // 🔍 DIAGNOSTIC DÉTAILLÉ de la réponse API
            historique.forEachIndexed { index, userProgramme ->
                android.util.Log.d("ProgrammeRepository", "API Programme ${index + 1}: ${userProgramme.programme.nom} (ID: ${userProgramme.id})")
                android.util.Log.d("ProgrammeRepository", "  Statut: ${userProgramme.statut}")
                android.util.Log.d("ProgrammeRepository", "  Utilisateur: ${userProgramme.user.id}")
                android.util.Log.d("ProgrammeRepository", "  Plats: ${userProgramme.programme.plats?.size ?: 0}")
                android.util.Log.d("ProgrammeRepository", "  Activités: ${userProgramme.programme.activites?.size ?: 0}")
            }
            
            if (historique.size < 3) {
                android.util.Log.w("ProgrammeRepository", "🚨 ATTENTION: API retourne seulement ${historique.size} programmes")
                android.util.Log.w("ProgrammeRepository", "   Causes possibles:")
                android.util.Log.w("ProgrammeRepository", "   1. L'utilisateur n'a que ${historique.size} programmes assignés")
                android.util.Log.w("ProgrammeRepository", "   2. Problème de filtrage côté backend")
                android.util.Log.w("ProgrammeRepository", "   3. Problème d'authentification JWT")
                android.util.Log.w("ProgrammeRepository", "   4. Données manquantes en base de données")
            }
            
            Result.success(historique)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "❌ Erreur getHistoriqueProgrammes: ${e.message}", e)
            
            // 🔍 DIAGNOSTIC D'ERREUR DÉTAILLÉ
            when {
                e.message?.contains("404") == true -> {
                    android.util.Log.e("ProgrammeRepository", "   Erreur 404: Endpoint non trouvé ou utilisateur sans programmes")
                }
                e.message?.contains("403") == true -> {
                    android.util.Log.e("ProgrammeRepository", "   Erreur 403: Problème d'authentification JWT")
                }
                e.message?.contains("500") == true -> {
                    android.util.Log.e("ProgrammeRepository", "   Erreur 500: Problème serveur backend")
                }
                e.message?.contains("timeout") == true -> {
                    android.util.Log.e("ProgrammeRepository", "   Timeout: Serveur trop lent ou inaccessible")
                }
                e.message?.contains("connect") == true -> {
                    android.util.Log.e("ProgrammeRepository", "   Connexion: Serveur backend non démarré")
                }
                else -> {
                    android.util.Log.e("ProgrammeRepository", "   Erreur inconnue: ${e.javaClass.simpleName}")
                }
            }
            
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun getUserProgrammeById(id: Int): Result<UserProgramme> {
        return try {
            val userProgramme = programmeApiService.getUserProgrammeById(id)
            android.util.Log.d("ProgrammeRepository", "UserProgramme chargé: ${userProgramme.programme.nom}")
            Result.success(userProgramme)
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "Erreur getUserProgrammeById($id): ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
    suspend fun terminerProgramme(): Result<MessageResponse> {
        return try {
            val response = programmeApiService.terminerProgramme()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun pauserProgramme(): Result<MessageResponse> {
        return try {
            val response = programmeApiService.pauserProgramme()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun reprendreProgramme(): Result<MessageResponse> {
        return try {
            val response = programmeApiService.reprendreProgramme()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun supprimerProgramme(userProgrammeId: Int): Result<MessageResponse> {
        return try {
            android.util.Log.d("ProgrammeRepository", "🗑️ Suppression programme ID: $userProgrammeId")
            android.util.Log.d("ProgrammeRepository", "   - URL: POST api/programmes/user/$userProgrammeId/supprimer")
            android.util.Log.d("ProgrammeRepository", "   - Méthode: POST (pas DELETE)")
            
            val response = programmeApiService.supprimerProgramme(userProgrammeId)
            android.util.Log.d("ProgrammeRepository", "✅ Programme supprimé: ${response.message}")
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            android.util.Log.e("ProgrammeRepository", "❌ Erreur HTTP ${e.code()}: ${e.message()}")
            
            // Récupérer le message d'erreur détaillé du serveur
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("ProgrammeRepository", "   - Corps de l'erreur: $errorBody")
            
            val errorMessage = when (e.code()) {
                404 -> "Programme non trouvé"
                403 -> "Accès refusé - Vérifiez vos permissions"
                500 -> "Erreur serveur interne - Contactez l'administrateur"
                else -> "Erreur HTTP ${e.code()}: ${e.message()}"
            }
            
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            android.util.Log.e("ProgrammeRepository", "❌ Erreur suppression programme: ${e.message}", e)
            Result.failure(Exception(NetworkErrorHandler.getErrorMessage(e)))
        }
    }
    
}
