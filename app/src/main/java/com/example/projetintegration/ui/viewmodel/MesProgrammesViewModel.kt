package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class MesProgrammesViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _mesProgrammes = MutableLiveData<List<UserProgramme>>()
    val mesProgrammes: LiveData<List<UserProgramme>> = _mesProgrammes
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _statistiques = MutableLiveData<Statistiques?>()
    val statistiques: LiveData<Statistiques?> = _statistiques
    
    // Le backend identifie l'utilisateur via le token JWT
    // Utilise getHistoriqueProgrammes() qui existe dans le backend
    fun loadMesProgrammes() {
        _isLoading.value = true
        _error.value = null
        
        android.util.Log.d("MesProgrammesViewModel", "🔄 Chargement de l'historique des programmes")
        
        viewModelScope.launch {
            try {
                val result = repository.getHistoriqueProgrammes()
                
                result.onSuccess { programmes ->
                    android.util.Log.d("MesProgrammesViewModel", "✅ Programmes chargés: ${programmes.size}")
                    
                    // 🔧 DIAGNOSTIC DÉTAILLÉ: Analyser chaque programme
                    programmes.forEachIndexed { index, userProgramme ->
                        android.util.Log.d("MesProgrammesViewModel", "=== PROGRAMME ${index + 1}/${programmes.size} ===")
                        android.util.Log.d("MesProgrammesViewModel", "ID: ${userProgramme.id}")
                        android.util.Log.d("MesProgrammesViewModel", "Nom: ${userProgramme.programme.nom}")
                        android.util.Log.d("MesProgrammesViewModel", "Description: ${userProgramme.programme.description}")
                        android.util.Log.d("MesProgrammesViewModel", "Durée: ${userProgramme.programme.dureeJours} jours")
                        android.util.Log.d("MesProgrammesViewModel", "Objectif: ${userProgramme.programme.objectif}")
                        android.util.Log.d("MesProgrammesViewModel", "Date début: ${userProgramme.dateDebut}")
                        android.util.Log.d("MesProgrammesViewModel", "Date fin prévue: ${userProgramme.dateFinPrevue}")
                        android.util.Log.d("MesProgrammesViewModel", "Date fin réelle: ${userProgramme.dateFin}")
                        android.util.Log.d("MesProgrammesViewModel", "Statut: ${userProgramme.statut}")
                        android.util.Log.d("MesProgrammesViewModel", "Utilisateur: ${userProgramme.user.nom} ${userProgramme.user.prenom}")
                        
                        val nbPlats = userProgramme.programme.plats?.size ?: 0
                        val nbActivites = userProgramme.programme.activites?.size ?: 0
                        
                        android.util.Log.d("MesProgrammesViewModel", "Plats: $nbPlats")
                        android.util.Log.d("MesProgrammesViewModel", "Activités: $nbActivites")
                        
                        if (nbPlats == 0 && nbActivites == 0) {
                            android.util.Log.w("MesProgrammesViewModel", "⚠️ PROGRAMME SANS CONTENU!")
                        }
                        
                        // Afficher les détails des plats et activités
                        userProgramme.programme.plats?.take(3)?.forEach { plat ->
                            android.util.Log.d("MesProgrammesViewModel", "  Plat: ${plat.nom}")
                        }
                        userProgramme.programme.activites?.take(3)?.forEach { activite ->
                            android.util.Log.d("MesProgrammesViewModel", "  Activité: ${activite.nom}")
                        }
                        
                        android.util.Log.d("MesProgrammesViewModel", "=====================================")
                    }
                    
                    // 🔍 VÉRIFICATION: Pourquoi seulement 2 programmes?
                    if (programmes.size < 3) {
                        android.util.Log.w("MesProgrammesViewModel", "🚨 ATTENTION: Seulement ${programmes.size} programmes retournés!")
                        android.util.Log.w("MesProgrammesViewModel", "   - Vérifier l'endpoint backend: /api/programmes/historique")
                        android.util.Log.w("MesProgrammesViewModel", "   - Vérifier l'authentification JWT")
                        android.util.Log.w("MesProgrammesViewModel", "   - Vérifier les données en base pour cet utilisateur")
                    }
                    
                    _mesProgrammes.value = programmes
                    
                    // ✅ CORRECTION: Charger automatiquement les statistiques après avoir chargé les programmes
                    android.util.Log.d("MesProgrammesViewModel", "🔄 Chargement automatique des statistiques")
                    loadStatistiques()
                }.onFailure { exception ->
                    android.util.Log.e("MesProgrammesViewModel", "❌ Erreur chargement programmes", exception)
                    
                    val errorMessage = when {
                        exception.message?.contains("404") == true -> "Aucun programme trouvé"
                        exception.message?.contains("403") == true -> "Accès refusé - Vérifiez votre connexion"
                        exception.message?.contains("timeout") == true -> "Timeout - Serveur inaccessible"
                        exception.message?.contains("connect") == true -> "Impossible de se connecter au serveur"
                        else -> exception.message ?: "Erreur lors du chargement"
                    }
                    
                    _error.value = errorMessage
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesViewModel", "💥 Exception non gérée dans loadMesProgrammes", e)
                _error.value = "Erreur critique: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadStatistiques() {
        android.util.Log.d("MesProgrammesViewModel", "🔄 Chargement des statistiques (nouvelle logique simple)")
        viewModelScope.launch {
            val result = repository.getStatistiques()
            
            result.onSuccess { stats ->
                android.util.Log.d("MesProgrammesViewModel", "=== STATISTIQUES DEBUG (NOUVELLE LOGIQUE) ===")
                android.util.Log.d("MesProgrammesViewModel", "PROGRESSION GLOBALE SIMPLE: ${stats.progressionGlobale}%")
                android.util.Log.d("MesProgrammesViewModel", "Formule: (Éléments terminés / Éléments attendus) × 100")
                android.util.Log.d("MesProgrammesViewModel", "Jour actuel: ${stats.jourActuel}/${stats.joursTotal}")
                android.util.Log.d("MesProgrammesViewModel", "===============================================")
                _statistiques.value = stats
            }.onFailure { exception ->
                android.util.Log.e("MesProgrammesViewModel", "❌ Erreur chargement statistiques: ${exception.message}")
                android.util.Log.e("MesProgrammesViewModel", "   Backend doit implémenter la nouvelle logique simple")
                _statistiques.value = null
            }
        }
    }
    
    fun supprimerProgramme(userProgramme: UserProgramme) {
        _isLoading.value = true
        _error.value = null
        
        android.util.Log.d("MesProgrammesViewModel", "🗑️ Suppression du programme: ${userProgramme.programme.nom}")
        
        viewModelScope.launch {
            try {
                val result = repository.supprimerProgramme(userProgramme.id)
                
                result.onSuccess { response ->
                    android.util.Log.d("MesProgrammesViewModel", "✅ Programme supprimé: ${response.message}")
                    
                    // Recharger la liste des programmes
                    loadMesProgrammes()
                    
                }.onFailure { exception ->
                    android.util.Log.e("MesProgrammesViewModel", "❌ Erreur suppression programme", exception)
                    
                    val errorMessage = when {
                        exception.message?.contains("404") == true -> "Programme non trouvé"
                        exception.message?.contains("403") == true -> "Vous n'avez pas l'autorisation de supprimer ce programme"
                        exception.message?.contains("500") == true -> "Erreur serveur lors de la suppression"
                        else -> exception.message ?: "Erreur lors de la suppression"
                    }
                    
                    _error.value = errorMessage
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesViewModel", "💥 Exception non gérée dans supprimerProgramme", e)
                _error.value = "Erreur critique: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
