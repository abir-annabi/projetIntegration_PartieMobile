package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.ProgressionJournaliere
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class MonProgrammeDetailViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _userProgramme = MutableLiveData<UserProgramme>()
    val userProgramme: LiveData<UserProgramme> = _userProgramme
    
    private val _progressionJour = MutableLiveData<ProgressionJournaliere?>()
    val progressionJour: LiveData<ProgressionJournaliere?> = _progressionJour
    
    private val _statistiques = MutableLiveData<Statistiques?>()
    val statistiques: LiveData<Statistiques?> = _statistiques
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _ajoutSuccess = MutableLiveData<Boolean>()
    val ajoutSuccess: LiveData<Boolean> = _ajoutSuccess
    
    fun loadUserProgramme(userProgrammeId: Int) {
        _isLoading.value = true
        _error.value = null
        
        android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Chargement UserProgramme ID: $userProgrammeId")
        
        viewModelScope.launch {
            try {
                val result = if (userProgrammeId > 0) {
                    // Charger un UserProgramme spécifique par son ID
                    android.util.Log.d("MonProgrammeDetailViewModel", "📋 Chargement programme spécifique: $userProgrammeId")
                    repository.getUserProgrammeById(userProgrammeId)
                } else {
                    // Charger le programme actif si aucun ID spécifique
                    android.util.Log.d("MonProgrammeDetailViewModel", "📋 Chargement programme actif")
                    repository.getProgrammeActif()
                }
                
                result.onSuccess { userProgramme ->
                    android.util.Log.d("MonProgrammeDetailViewModel", "✅ Programme chargé: ${userProgramme.programme.nom}")
                    
                    // 🔧 DIAGNOSTIC: Vérifier le contenu du programme
                    val nbPlats = userProgramme.programme.plats?.size ?: 0
                    val nbActivites = userProgramme.programme.activites?.size ?: 0
                    
                    android.util.Log.d("MonProgrammeDetailViewModel", "📊 Contenu programme:")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Plats: $nbPlats")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Activités: $nbActivites")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Statut: ${userProgramme.statut}")
                    
                    if (nbPlats == 0 && nbActivites == 0) {
                        android.util.Log.w("MonProgrammeDetailViewModel", "⚠️ PROBLÈME BACKEND: Programme sans contenu!")
                        android.util.Log.w("MonProgrammeDetailViewModel", "   Le backend doit retourner les plats et activités")
                    }
                    
                    _userProgramme.value = userProgramme
                }.onFailure { exception ->
                    android.util.Log.e("MonProgrammeDetailViewModel", "❌ Erreur chargement programme", exception)
                    
                    val errorMessage = when {
                        exception.message?.contains("404") == true -> "Programme non trouvé"
                        exception.message?.contains("403") == true -> "Accès refusé - Vérifiez votre connexion"
                        exception.message?.contains("timeout") == true -> "Timeout - Backend non accessible"
                        exception.message?.contains("connect") == true -> "Impossible de se connecter au serveur"
                        else -> exception.message ?: "Erreur lors du chargement du programme"
                    }
                    
                    _error.value = errorMessage
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MonProgrammeDetailViewModel", "💥 Exception non gérée dans loadUserProgramme", e)
                _error.value = "Erreur critique: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 🔧 SUPPRIMÉ: Cette méthode ne devrait pas exister
    // Le backend doit retourner des programmes complets
    
    fun loadProgressionJour(date: String) {
        android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Chargement progression pour: $date")
        
        viewModelScope.launch {
            try {
                // ✅ CORRECTION: Utiliser le programme spécifique si disponible
                val currentUserProgramme = _userProgramme.value
                val result = if (currentUserProgramme != null) {
                    android.util.Log.d("MonProgrammeDetailViewModel", "📋 Chargement progression pour programme spécifique: ${currentUserProgramme.id}")
                    repository.getProgressionByDateForUserProgramme(date, currentUserProgramme.id)
                } else {
                    android.util.Log.d("MonProgrammeDetailViewModel", "📋 Chargement progression pour programme actif")
                    repository.getProgressionByDate(date)
                }
                
                result.onSuccess { progression ->
                    android.util.Log.d("MonProgrammeDetailViewModel", "✅ Progression trouvée pour $date")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Plats consommés: ${progression.platsConsommes?.size ?: 0}")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Activités réalisées: ${progression.activitesRealisees?.size ?: 0}")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Statut jour: ${progression.statutJour}")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   - Score: ${progression.scoreJour}")
                    
                    // ✅ CORRECTION: Vérifier que la progression appartient au programme actuel
                    if (currentUserProgramme != null) {
                        val progressionProgrammeId = progression.userProgramme?.id
                        val currentProgrammeId = currentUserProgramme.id
                        
                        android.util.Log.d("MonProgrammeDetailViewModel", "🔍 Vérification programme:")
                        android.util.Log.d("MonProgrammeDetailViewModel", "   - Programme actuel: $currentProgrammeId (${currentUserProgramme.programme.nom})")
                        android.util.Log.d("MonProgrammeDetailViewModel", "   - Programme progression: $progressionProgrammeId")
                        
                        if (progressionProgrammeId == currentProgrammeId) {
                            android.util.Log.d("MonProgrammeDetailViewModel", "✅ Progression correspond au programme actuel")
                            _progressionJour.value = progression
                        } else {
                            android.util.Log.w("MonProgrammeDetailViewModel", "⚠️ Progression d'un autre programme - ignorée")
                            android.util.Log.w("MonProgrammeDetailViewModel", "   Cette progression appartient au programme: ${progression.userProgramme?.programme?.nom}")
                            _progressionJour.value = null
                        }
                    } else {
                        // Si pas de programme actuel chargé, accepter la progression
                        android.util.Log.w("MonProgrammeDetailViewModel", "⚠️ Aucun programme actuel - acceptation de la progression")
                        _progressionJour.value = progression
                    }
                }.onFailure { exception ->
                    android.util.Log.d("MonProgrammeDetailViewModel", "ℹ️ Aucune progression pour $date (normal)")
                    android.util.Log.d("MonProgrammeDetailViewModel", "   Erreur: ${exception.message}")
                    
                    // Pas de progression pour ce jour, c'est normal
                    _progressionJour.value = null
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MonProgrammeDetailViewModel", "💥 Exception dans loadProgressionJour", e)
                _progressionJour.value = null
            }
        }
    }
    
    fun loadProgressionAujourdhui() {
        viewModelScope.launch {
            val result = repository.getProgressionAujourdhui()
            
            result.onSuccess { progression ->
                _progressionJour.value = progression
                android.util.Log.d("MonProgrammeDetailViewModel", "Progression d'aujourd'hui chargée")
            }.onFailure {
                android.util.Log.d("MonProgrammeDetailViewModel", "Aucune progression aujourd'hui")
                _progressionJour.value = null
            }
        }
    }
    
    fun loadStatistiques() {
        viewModelScope.launch {
            val result = repository.getStatistiques()
            
            result.onSuccess { stats ->
                _statistiques.value = stats
            }.onFailure {
                _statistiques.value = null
            }
        }
    }
    
    // ✅ NOUVELLE MÉTHODE: Enregistrer TOUTE la progression en UN SEUL appel
    fun enregistrerProgressionComplete(request: com.example.projetintegration.data.models.EnregistrerProgressionRequest) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            // ✅ CORRECTION: Ajouter l'ID du programme actuel à la requête
            val currentUserProgramme = _userProgramme.value
            val requestWithProgramme = if (currentUserProgramme != null) {
                android.util.Log.d("MonProgrammeDetailViewModel", "📋 Enregistrement pour programme spécifique: ${currentUserProgramme.id}")
                request.copy(userProgrammeId = currentUserProgramme.id)
            } else {
                android.util.Log.w("MonProgrammeDetailViewModel", "⚠️ Enregistrement sans programme spécifique")
                request
            }
            
            val result = repository.enregistrerProgression(requestWithProgramme)
            _isLoading.value = false
            
            result.onSuccess { progressionEnregistree ->
                android.util.Log.d("MonProgrammeDetailViewModel", "✅ Enregistrement réussi - rechargement automatique")
                
                _ajoutSuccess.value = true
                
                // ✅ CORRECTION: Recharger AUTOMATIQUEMENT la progression du jour
                val dateEnregistree = requestWithProgramme.date
                if (dateEnregistree != null) {
                    android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Rechargement automatique progression pour: $dateEnregistree")
                    loadProgressionJour(dateEnregistree)
                }
                
                // ✅ CORRECTION: Recharger les statistiques après enregistrement
                android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Rechargement automatique des statistiques")
                loadStatistiques()
            }.onFailure { exception ->
                android.util.Log.e("MonProgrammeDetailViewModel", "❌ Erreur enregistrement: ${exception.message}")
                _error.value = exception.message ?: "Erreur lors de l'enregistrement"
                _ajoutSuccess.value = false
            }
        }
    }
    
    // Enregistrer le poids séparément (optionnel)
    fun enregistrerPoidsSeul(date: String, poids: Double) {
        _isLoading.value = true
        
        viewModelScope.launch {
            // ✅ CORRECTION: Ajouter l'ID du programme actuel à la requête
            val currentUserProgramme = _userProgramme.value
            val request = com.example.projetintegration.data.models.EnregistrerProgressionRequest(
                date = date,
                platIds = null,
                activiteIds = null,
                poidsJour = poids,
                notes = null,
                userProgrammeId = currentUserProgramme?.id // ✅ NOUVEAU: ID du programme
            )
            
            val result = repository.enregistrerProgression(request)
            _isLoading.value = false
            
            result.onSuccess { progressionEnregistree ->
                android.util.Log.d("MonProgrammeDetailViewModel", "✅ Enregistrement poids réussi - rechargement automatique")
                
                _ajoutSuccess.value = true
                
                // ✅ CORRECTION: Recharger AUTOMATIQUEMENT la progression du jour
                android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Rechargement automatique progression pour: $date")
                loadProgressionJour(date)
                
                // ✅ CORRECTION: Recharger les statistiques après enregistrement du poids
                android.util.Log.d("MonProgrammeDetailViewModel", "🔄 Rechargement automatique des statistiques")
                loadStatistiques()
            }.onFailure { exception ->
                android.util.Log.e("MonProgrammeDetailViewModel", "❌ Erreur enregistrement poids: ${exception.message}")
                _error.value = exception.message ?: "Erreur lors de l'enregistrement du poids"
                _ajoutSuccess.value = false
            }
        }
    }
}
