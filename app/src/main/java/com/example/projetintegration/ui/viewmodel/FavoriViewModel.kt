package com.example.projetintegration.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.*
import com.example.projetintegration.data.repository.FavoriRepository
import kotlinx.coroutines.launch

class FavoriViewModel : ViewModel() {
    
    private val repository = FavoriRepository()
    
    private val _favorisProgrammes = MutableLiveData<List<FavoriProgrammeResponse>>()
    val favorisProgrammes: LiveData<List<FavoriProgrammeResponse>> = _favorisProgrammes
    
    private val _favorisPlats = MutableLiveData<List<FavoriPlatResponse>>()
    val favorisPlats: LiveData<List<FavoriPlatResponse>> = _favorisPlats
    
    private val _favorisStats = MutableLiveData<FavorisStatsResponse>()
    val favorisStats: LiveData<FavorisStatsResponse> = _favorisStats
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // ===== PROGRAMMES FAVORIS =====
    
    fun toggleFavoriProgramme(programmeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("FavoriViewModel", "🔄 Toggle favori programme ID: $programmeId")
                Log.d("FavoriViewModel", "   - Thread: ${Thread.currentThread().name}")
                Log.d("FavoriViewModel", "   - Appel repository.toggleFavoriProgramme($programmeId)")
                
                val result = repository.toggleFavoriProgramme(programmeId)
                
                result.onSuccess { response ->
                    Log.d("FavoriViewModel", "✅ Toggle réussi: ${response.message}")
                    Log.d("FavoriViewModel", "   - isFavorite: ${response.isFavorite}")
                    Log.d("FavoriViewModel", "   - success: ${response.success}")
                    _successMessage.value = response.message
                    
                    // Recharger les listes pour mettre à jour l'interface
                    Log.d("FavoriViewModel", "🔄 Rechargement des listes après toggle...")
                    loadFavorisProgrammes()
                    loadFavorisStats()
                }.onFailure { exception ->
                    Log.e("FavoriViewModel", "❌ Erreur toggle favori programme", exception)
                    Log.e("FavoriViewModel", "   - Exception type: ${exception::class.java.simpleName}")
                    Log.e("FavoriViewModel", "   - Message: ${exception.message}")
                    _error.value = "Erreur lors de la modification des favoris: ${exception.message}"
                }
                
            } catch (e: Exception) {
                Log.e("FavoriViewModel", "💥 Exception dans toggleFavoriProgramme", e)
                Log.e("FavoriViewModel", "   - Exception type: ${e::class.java.simpleName}")
                Log.e("FavoriViewModel", "   - Message: ${e.message}")
                Log.e("FavoriViewModel", "   - Stack trace: ${e.stackTrace.take(5).joinToString("\n")}")
                _error.value = "Erreur inattendue: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavoriPlat(platId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("FavoriViewModel", "🔄 Toggle favori plat ID: $platId")
                
                val result = repository.toggleFavoriPlat(platId)
                
                result.onSuccess { response ->
                    Log.d("FavoriViewModel", "✅ Toggle réussi: ${response.message}")
                    _successMessage.value = response.message
                    
                    // Recharger les listes pour mettre à jour l'interface
                    loadFavorisPlats()
                    loadFavorisStats()
                }.onFailure { exception ->
                    Log.e("FavoriViewModel", "❌ Erreur toggle favori plat", exception)
                    _error.value = "Erreur lors de la modification des favoris: ${exception.message}"
                }
                
            } catch (e: Exception) {
                Log.e("FavoriViewModel", "💥 Exception dans toggleFavoriPlat", e)
                _error.value = "Erreur inattendue: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadFavorisProgrammes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("FavoriViewModel", "🔄 Chargement programmes favoris")
                
                val result = repository.getAllFavorisProgrammes()
                
                result.onSuccess { favoris ->
                    Log.d("FavoriViewModel", "✅ Programmes favoris chargés: ${favoris.size}")
                    _favorisProgrammes.value = favoris
                }.onFailure { exception ->
                    Log.e("FavoriViewModel", "❌ Erreur chargement programmes favoris", exception)
                    _error.value = "Erreur lors du chargement des programmes favoris: ${exception.message}"
                    _favorisProgrammes.value = emptyList()
                }
                
            } catch (e: Exception) {
                Log.e("FavoriViewModel", "💥 Exception dans loadFavorisProgrammes", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _favorisProgrammes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadFavorisPlats() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("FavoriViewModel", "🔄 Chargement plats favoris")
                
                val result = repository.getAllFavorisPlats()
                
                result.onSuccess { favoris ->
                    Log.d("FavoriViewModel", "✅ Plats favoris chargés: ${favoris.size}")
                    _favorisPlats.value = favoris
                }.onFailure { exception ->
                    Log.e("FavoriViewModel", "❌ Erreur chargement plats favoris", exception)
                    _error.value = "Erreur lors du chargement des plats favoris: ${exception.message}"
                    _favorisPlats.value = emptyList()
                }
                
            } catch (e: Exception) {
                Log.e("FavoriViewModel", "💥 Exception dans loadFavorisPlats", e)
                _error.value = "Erreur inattendue: ${e.message}"
                _favorisPlats.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadFavorisStats() {
        viewModelScope.launch {
            try {
                Log.d("FavoriViewModel", "🔄 Chargement statistiques favoris")
                
                val result = repository.getFavorisStats()
                
                result.onSuccess { stats ->
                    Log.d("FavoriViewModel", "✅ Statistiques favoris chargées: ${stats.totalFavoris} total")
                    _favorisStats.value = stats
                }.onFailure { exception ->
                    Log.e("FavoriViewModel", "❌ Erreur chargement statistiques favoris", exception)
                    // Ne pas afficher d'erreur pour les statistiques, c'est optionnel
                }
                
            } catch (e: Exception) {
                Log.e("FavoriViewModel", "💥 Exception dans loadFavorisStats", e)
                // Ne pas afficher d'erreur pour les statistiques, c'est optionnel
            }
        }
    }
    
    // ===== VÉRIFICATION STATUT FAVORIS =====
    
    suspend fun isProgrammeFavorite(programmeId: Long): Boolean {
        return try {
            val result = repository.isProgrammeFavorite(programmeId)
            result.getOrElse { false }
        } catch (e: Exception) {
            Log.e("FavoriViewModel", "Erreur vérification statut favori programme", e)
            false
        }
    }
    
    suspend fun isPlatFavorite(platId: Long): Boolean {
        return try {
            val result = repository.isPlatFavorite(platId)
            result.getOrElse { false }
        } catch (e: Exception) {
            Log.e("FavoriViewModel", "Erreur vérification statut favori plat", e)
            false
        }
    }
    
    // ===== UTILITAIRES =====
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}