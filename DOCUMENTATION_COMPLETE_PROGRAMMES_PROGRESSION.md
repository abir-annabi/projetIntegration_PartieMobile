# 📚 Documentation Complète - Système de Programmes et Progression

## 🎯 Vue d'ensemble

Cette documentation présente toutes les classes Android impliquées dans le processus de :
- **Programmes** : Gestion des programmes d'entraînement disponibles
- **Programmes Assignés** : Programmes assignés aux utilisateurs (UserProgramme)
- **Progression** : Suivi quotidien des activités et plats consommés

---

## 🏗️ Architecture du Système

### **Couches de l'Application**
```
┌─────────────────────────────────────────┐
│              UI LAYER                   │
│  Activities + ViewModels + Adapters     │
├─────────────────────────────────────────┤
│            DOMAIN LAYER                 │
│         Repositories + Models           │
├─────────────────────────────────────────┤
│             DATA LAYER                  │
│        API Services + Network           │
└─────────────────────────────────────────┘
```

### **Relations entre les Entités**
```
User ──┐
       ├── UserProgramme ──── Programme ──┬── Plat[]
       │                                  └── ActiviteSportive[]
       └── ProgressionJournaliere ────────┬── Plat[] (consommés)
                                          └── ActiviteSportive[] (réalisées)
```

---

## 📊 Modèles de Données (Data Models)

### 1. **Programme.kt** - Modèles Principaux
```kotlin
package com.example.projetintegration.data.models

data class Programme(
    val id: Int,
    val nom: String,
    val description: String,
    val dureeJours: Int,
    val objectif: String,  // "perte-poids", "prise-masse", "maintien", "endurance"
    val plats: List<Plat> = emptyList(), // 🔧 CORRECTION: Valeur par défaut
    val activites: List<ActiviteSportive> = emptyList(), // 🔧 CORRECTION: Valeur par défaut
    val conseils: List<String> = emptyList(), // 🔧 CORRECTION: Valeur par défaut
    val imageUrl: String
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
    val userProgramme: UserProgramme,
    val date: String,
    val jourProgramme: Int,
    val platsConsommes: List<Plat>? = emptyList(),
    val activitesRealisees: List<ActiviteSportive>? = emptyList(),
    val poidsJour: Double?,
    val notes: String?,
    val statutJour: String = "NON_FAIT", // ✅ Valeur par défaut
    val scoreJour: Int = 0, // ✅ Valeur par défaut
    val caloriesConsommees: Int? = 0 // ✅ Valeur par défaut
)

// ✅ DTO pour enregistrer la progression quotidienne
data class EnregistrerProgressionRequest(
    val date: String?,           // Format "2025-12-01"
    val platIds: List<Int>?,     // IDs des plats consommés
    val activiteIds: List<Int>?, // IDs des activités réalisées
    val poidsJour: Double?,      // Poids du jour
    val notes: String?           // Notes personnelles
)

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
```

### 2. **Plat.kt** - Modèle des Plats

```kotlin
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
```

### 3. **ActiviteSportive.kt** - Modèle des Activités

```kotlin
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
```

---

## 🌐 Services API (Data Layer)

### 1. **ProgrammeApiService.kt** - Interface API Programmes
```kotlin
package com.example.projetintegration.data.repository

import com.example.projetintegration.data.api.RetrofitClient
import com.example.projetintegration.data.models.ChangePasswordRequest
import com.example.projetintegration.data.models.MessageResponse
import com.example.projetintegration.data.models.UpdateProfileRequest
import com.example.projetintegration.data.models.User

class UserRepository {
    
    private val userApiService = RetrofitClient.userApiService
    
    // Le backend identifie l'utilisateur via le token JWT
    // Plus besoin de passer userId
    
    suspend fun getProfile(): Result<User> {
        return try {
            android.util.Log.d("UserRepository", "Appel API getProfile...")
            val user = userApiService.getProfile()
            android.util.Log.d("UserRepository", "Profil reçu: ${user.nom} ${user.prenom}")
            Result.success(user)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Erreur getProfile: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(request: UpdateProfileRequest): Result<User> {
        return try {
            val user = userApiService.updateProfile(request)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(request: ChangePasswordRequest): Result<MessageResponse> {
        return try {
            val response = userApiService.changePassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAccount(): Result<MessageResponse> {
        return try {
            val response = userApiService.deleteAccount()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 🎭 ViewModels (Presentation Layer)

### 1. **ProgrammeViewModel.kt** - ViewModel Liste des Programmes

```kotlin
package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class ProgrammeViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _programmes = MutableLiveData<List<Programme>>()
    val programmes: LiveData<List<Programme>> = _programmes
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun loadAllProgrammes() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getAllProgrammes()
            _isLoading.value = false
            
            result.onSuccess { programmesList ->
                _programmes.value = programmesList
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des programmes"
            }
        }
    }
    
    fun loadProgrammesByObjectif(objectif: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            // ⚠️ getProgrammesByObjectif n'existe pas - on charge tous les programmes
            val result = repository.getAllProgrammes()
            _isLoading.value = false
            
            result.onSuccess { allProgrammes ->
                // Filtrer côté client
                val filtered = allProgrammes.filter { it.objectif.equals(objectif, ignoreCase = true) }
                _programmes.value = filtered
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des programmes"
            }
        }
    }
}
```

### 2. **ProgrammeDetailViewModel.kt** - ViewModel Détail Programme
```kotlin
package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.AssignerProgrammeRequest
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class ProgrammeDetailViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _programme = MutableLiveData<Programme>()
    val programme: LiveData<Programme> = _programme
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _inscriptionResult = MutableLiveData<Result<UserProgramme>>()
    val inscriptionResult: LiveData<Result<UserProgramme>> = _inscriptionResult
    
    fun loadProgramme(programmeId: Int) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getProgrammeById(programmeId)
            _isLoading.value = false
            
            result.onSuccess { prog ->
                _programme.value = prog
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement du programme"
            }
        }
    }
    
    fun inscrireAuProgramme(programmeId: Int, poidsDebut: Double?, poidsObjectif: Double?) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val request = AssignerProgrammeRequest(
                programmeId = programmeId,
                dateDebut = java.time.LocalDate.now().toString(), // Format "2025-12-28"
                objectifPersonnel = "Objectif personnel" // Valeur par défaut
            )
            val result = repository.assignerProgramme(request)
            _isLoading.value = false
            _inscriptionResult.value = result
        }
    }
}
```

### 3. **MesProgrammesViewModel.kt** - ViewModel Mes Programmes

```kotlin
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
        
        viewModelScope.launch {
            val result = repository.getHistoriqueProgrammes()
            _isLoading.value = false
            
            result.onSuccess { programmes ->
                android.util.Log.d("MesProgrammesViewModel", "Programmes chargés: ${programmes.size}")
                _mesProgrammes.value = programmes
            }.onFailure { exception ->
                android.util.Log.e("MesProgrammesViewModel", "Erreur chargement programmes: ${exception.message}")
                _error.value = exception.message
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
}
```

### 4. **MonProgrammeDetailViewModel.kt** - ViewModel Détail Mon Programme
```kotlin
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
        
        viewModelScope.launch {
            // Charger le programme actif
            val result = repository.getProgrammeActif()
            
            result.onSuccess { userProgramme ->
                _userProgramme.value = userProgramme
                
                // 🔧 SOLUTION: Si le programme n'a pas de plats/activités, les charger séparément
                if (userProgramme.programme.plats.isEmpty() || userProgramme.programme.activites.isEmpty()) {
                    android.util.Log.w("MonProgrammeDetailViewModel", "Programme sans contenu, chargement des détails...")
                    loadProgrammeDetails(userProgramme.programme.id)
                }
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement du programme"
            }
            
            _isLoading.value = false
        }
    }
    
    // 🔧 NOUVELLE MÉTHODE: Charger les détails complets du programme
    private fun loadProgrammeDetails(programmeId: Int) {
        viewModelScope.launch {
            val result = repository.getProgrammeById(programmeId)
            
            result.onSuccess { programmeComplet ->
                // Mettre à jour le UserProgramme avec le programme complet
                _userProgramme.value?.let { currentUserProgramme ->
                    val userProgrammeComplet = currentUserProgramme.copy(
                        programme = programmeComplet
                    )
                    _userProgramme.value = userProgrammeComplet
                    android.util.Log.d("MonProgrammeDetailViewModel", "Programme complet chargé: ${programmeComplet.plats.size} plats, ${programmeComplet.activites.size} activités")
                }
            }.onFailure { exception ->
                android.util.Log.e("MonProgrammeDetailViewModel", "Erreur chargement détails programme: ${exception.message}")
            }
        }
    }
    
    fun loadProgressionJour(date: String) {
        viewModelScope.launch {
            // ⚠️ On utilise getProgressionAujourdhui() car getProgressionJour() n'existe pas
            val result = repository.getProgressionAujourdhui()
            
            result.onSuccess { progression ->
                _progressionJour.value = progression
            }.onFailure {
                // Pas de progression pour ce jour, c'est normal
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
            val result = repository.enregistrerProgression(request)
            _isLoading.value = false
            
            result.onSuccess {
                _ajoutSuccess.value = true
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de l'enregistrement"
                _ajoutSuccess.value = false
            }
        }
    }
    
    // Enregistrer le poids séparément (optionnel)
    fun enregistrerPoidsSeul(date: String, poids: Double) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val request = com.example.projetintegration.data.models.EnregistrerProgressionRequest(
                date = date,
                platIds = null,
                activiteIds = null,
                poidsJour = poids,
                notes = null
            )
            
            val result = repository.enregistrerProgression(request)
            _isLoading.value = false
            
            result.onSuccess {
                _ajoutSuccess.value = true
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de l'enregistrement du poids"
                _ajoutSuccess.value = false
            }
        }
    }
}
```

### 5. **StatistiquesViewModel.kt** - ViewModel Statistiques
```kotlin
package com.example.projetintegration.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetintegration.data.models.EnregistrerProgressionRequest
import com.example.projetintegration.data.models.ProgressionJournaliere
import com.example.projetintegration.data.models.Statistiques
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.repository.ProgrammeRepository
import kotlinx.coroutines.launch

class StatistiquesViewModel : ViewModel() {
    
    private val repository = ProgrammeRepository()
    
    private val _statistiques = MutableLiveData<Statistiques>()
    val statistiques: LiveData<Statistiques> = _statistiques
    
    private val _programmeActif = MutableLiveData<UserProgramme>()
    val programmeActif: LiveData<UserProgramme> = _programmeActif
    
    private val _progressionAujourdhui = MutableLiveData<ProgressionJournaliere?>()
    val progressionAujourdhui: LiveData<ProgressionJournaliere?> = _progressionAujourdhui
    
    private val _historiqueProgression = MutableLiveData<List<ProgressionJournaliere>>()
    val historiqueProgression: LiveData<List<ProgressionJournaliere>> = _historiqueProgression
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _enregistrementSuccess = MutableLiveData<Boolean>()
    val enregistrementSuccess: LiveData<Boolean> = _enregistrementSuccess
    
    fun loadStatistiques() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getStatistiques()
            _isLoading.value = false
            
            result.onSuccess { stats ->
                _statistiques.value = stats
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors du chargement des statistiques"
            }
        }
    }
    
    fun loadProgrammeActif() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getProgrammeActif()
            _isLoading.value = false
            
            result.onSuccess { programme ->
                _programmeActif.value = programme
            }.onFailure { exception ->
                _error.value = exception.message ?: "Aucun programme actif"
            }
        }
    }
    
    fun loadProgressionAujourdhui() {
        viewModelScope.launch {
            val result = repository.getProgressionAujourdhui()
            
            result.onSuccess { progression ->
                _progressionAujourdhui.value = progression
            }.onFailure {
                _progressionAujourdhui.value = null
            }
        }
    }
    
    fun loadHistoriqueProgression() {
        viewModelScope.launch {
            val result = repository.getHistoriqueProgression()
            
            result.onSuccess { historique ->
                _historiqueProgression.value = historique
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
    
    fun enregistrerProgression(request: EnregistrerProgressionRequest) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.enregistrerProgression(request)
            _isLoading.value = false
            
            result.onSuccess {
                _enregistrementSuccess.value = true
                // Recharger les statistiques
                loadStatistiques()
                loadProgressionAujourdhui()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erreur lors de l'enregistrement"
                _enregistrementSuccess.value = false
            }
        }
    }
    
    fun terminerProgramme(onSuccess: () -> Unit) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.terminerProgramme()
            _isLoading.value = false
            
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }
}
```

---

## 📱 Activities (UI Layer)

### 1. **ProgrammesActivity.kt** - Liste des Programmes Disponibles
```kotlin
package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.databinding.ActivityProgrammesBinding
import com.example.projetintegration.ui.adapters.ProgrammesAdapter
import com.example.projetintegration.ui.viewmodel.ProgrammeViewModel

class ProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammesBinding
    private lateinit var viewModel: ProgrammeViewModel
    private lateinit var programmesAdapter: ProgrammesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProgrammeViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadProgrammes()
    }
    
    private fun setupObservers() {
        viewModel.programmes.observe(this) { programmes ->
            programmesAdapter.submitList(programmes)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupRecyclerView() {
        programmesAdapter = ProgrammesAdapter { programme ->
            val intent = Intent(this, ProgrammeDetailActivity::class.java)
            intent.putExtra("PROGRAMME_ID", programme.id)
            startActivity(intent)
        }
        
        binding.rvProgrammes.apply {
            layoutManager = LinearLayoutManager(this@ProgrammesActivity)
            adapter = programmesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnMesProgrammes.setOnClickListener {
            startActivity(Intent(this, MesProgrammesActivity::class.java))
        }
    }
    
    private fun loadProgrammes() {
        viewModel.loadAllProgrammes()
    }
}
```

### 2. **ProgrammeDetailActivity.kt** - Détail d'un Programme (Inscription)
```kotlin
package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityProgrammeDetailBinding
import com.example.projetintegration.ui.viewmodel.ProgrammeDetailViewModel

class ProgrammeDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgrammeDetailBinding
    private lateinit var viewModel: ProgrammeDetailViewModel
    private lateinit var preferencesManager: PreferencesManager
    private var programmeId: Int = 0
    private var programme: Programme? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProgrammeDetailViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        programmeId = intent.getIntExtra("PROGRAMME_ID", 0)
        
        setupObservers()
        setupClickListeners()
        loadProgrammeDetail()
    }
    
    private fun setupObservers() {
        viewModel.programme.observe(this) { prog ->
            programme = prog
            displayProgrammeDetail(prog)
            binding.scrollView.visibility = View.VISIBLE
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.scrollView.visibility = View.GONE
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                finish()
            }
        }
        
        viewModel.inscriptionResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Inscription réussie!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { exception ->
                binding.btnInscrire.isEnabled = true
                binding.btnInscrire.text = "S'inscrire"
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnInscrire.setOnClickListener {
            inscrireAuProgramme()
        }
    }
    
    private fun loadProgrammeDetail() {
        viewModel.loadProgramme(programmeId)
    }
    
    private fun displayProgrammeDetail(programme: Programme) {
        binding.tvNom.text = programme.nom
        binding.tvDescription.text = programme.description
        binding.tvDuree.text = "${programme.dureeJours} jours"
        binding.tvObjectif.text = formatObjectif(programme.objectif)
        
        // Plats
        binding.tvPlatsCount.text = "Plats recommandés (${programme.plats.size})"
        val platsText = if (programme.plats.isEmpty()) {
            "Aucun plat disponible"
        } else {
            programme.plats.joinToString("\n\n") { plat ->
                "🍽️ ${plat.nom}\n   ${plat.calories} kcal • ${plat.tempsPreparation} min\n   ${plat.description}"
            }
        }
        binding.tvPlatsList.text = platsText
        
        // Activités
        binding.tvActivitesCount.text = "Activités sportives (${programme.activites.size})"
        val activitesText = if (programme.activites.isEmpty()) {
            "Aucune activité disponible"
        } else {
            programme.activites.joinToString("\n\n") { activite ->
                "💪 ${activite.nom}\n   ${activite.duree} min • ${activite.caloriesBrulees} kcal\n   Niveau: ${formatNiveau(activite.niveau)}\n   ${activite.description}"
            }
        }
        binding.tvActivitesList.text = activitesText
        
        // Conseils
        val conseilsText = if (programme.conseils.isEmpty()) {
            "Aucun conseil disponible"
        } else {
            programme.conseils.joinToString("\n") { "• $it" }
        }
        binding.tvConseilsList.text = conseilsText
    }
    
    private fun inscrireAuProgramme() {
        val userId = preferencesManager.getUserId()
        if (userId == null) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnInscrire.isEnabled = false
        binding.btnInscrire.text = "Inscription..."
        
        viewModel.inscrireAuProgramme(programmeId, null, null)
    }
    
    private fun formatObjectif(objectif: String): String {
        return when (objectif.lowercase()) {
            "perte-poids", "perte_poids" -> "Perte de poids"
            "prise-masse", "prise_masse" -> "Prise de masse"
            "maintien" -> "Maintien"
            "endurance" -> "Endurance"
            else -> objectif
        }
    }
    
    private fun formatNiveau(niveau: String): String {
        return when (niveau.lowercase()) {
            "debutant" -> "Débutant"
            "intermediaire", "intermédiaire" -> "Intermédiaire"
            "avance", "avancé" -> "Avancé"
            else -> niveau
        }
    }
}
```

### 3. **MesProgrammesActivity.kt** - Liste de Mes Programmes
```kotlin
package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityMesProgrammesBinding
import com.example.projetintegration.ui.adapters.MesProgrammesAdapter
import com.example.projetintegration.ui.viewmodel.MesProgrammesViewModel

class MesProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMesProgrammesBinding
    private lateinit var viewModel: MesProgrammesViewModel
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mesProgrammesAdapter: MesProgrammesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesProgrammesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[MesProgrammesViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadMesProgrammes()
    }
    
    private fun setupObservers() {
        viewModel.mesProgrammes.observe(this) { mesProgrammes ->
            if (mesProgrammes.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = "Aucun programme trouvé.\n\n" +
                        "💡 Allez dans 'Programmes' pour vous inscrire à un programme!"
                binding.rvMesProgrammes.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvMesProgrammes.visibility = View.VISIBLE
                mesProgrammesAdapter.submitList(mesProgrammes)
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                if (it.contains("Aucun programme", ignoreCase = true)) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = "Aucun programme trouvé.\n\n" +
                            "💡 Allez dans 'Programmes' pour vous inscrire à un programme!"
                    binding.rvMesProgrammes.visibility = View.GONE
                } else {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }
        }
        
        viewModel.statistiques.observe(this) { stats ->
            // Les statistiques sont chargées automatiquement par le backend
            // La progression est calculée automatiquement
        }
    }
    
    private fun setupRecyclerView() {
        mesProgrammesAdapter = MesProgrammesAdapter()
        
        binding.rvMesProgrammes.apply {
            layoutManager = LinearLayoutManager(this@MesProgrammesActivity)
            adapter = mesProgrammesAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadMesProgrammes() {
        // Le backend identifie l'utilisateur via le token JWT
        // Plus besoin de passer userId
        viewModel.loadMesProgrammes()
        viewModel.loadStatistiques()
    }
}
```

### 4. **MonProgrammeDetailActivity.kt** - Détail Mon Programme (Progression Quotidienne)
```kotlin
package com.example.projetintegration.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.databinding.ActivityMonProgrammeDetailBinding
import com.example.projetintegration.ui.adapters.PlatsSelectionAdapter
import com.example.projetintegration.ui.adapters.ActivitesSelectionAdapter
import com.example.projetintegration.ui.viewmodel.MonProgrammeDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonProgrammeDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMonProgrammeDetailBinding
    private lateinit var viewModel: MonProgrammeDetailViewModel
    private lateinit var platsAdapter: PlatsSelectionAdapter
    private lateinit var activitesAdapter: ActivitesSelectionAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentDate = Calendar.getInstance()
    private var userProgrammeId: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonProgrammeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userProgrammeId = intent.getIntExtra("USER_PROGRAMME_ID", 0)
        if (userProgrammeId == 0) {
            Toast.makeText(this, "Erreur: Programme non trouvé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        viewModel = ViewModelProvider(this)[MonProgrammeDetailViewModel::class.java]
        
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
        loadData()
    }
    
    private fun setupRecyclerViews() {
        // ✅ AMÉLIORATION: Les adapters mettent à jour le résumé en temps réel
        platsAdapter = PlatsSelectionAdapter { plat, isChecked ->
            updateResumeTempReel()
        }
        
        binding.rvPlats.apply {
            layoutManager = LinearLayoutManager(this@MonProgrammeDetailActivity)
            adapter = platsAdapter
        }
        
        activitesAdapter = ActivitesSelectionAdapter { activite, isChecked ->
            updateResumeTempReel()
        }
        
        binding.rvActivites.apply {
            layoutManager = LinearLayoutManager(this@MonProgrammeDetailActivity)
            adapter = activitesAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.userProgramme.observe(this) { userProgramme ->
            binding.tvProgrammeName.text = userProgramme.programme.nom
            binding.tvDescription.text = userProgramme.programme.description
            binding.tvDuree.text = "Durée: ${userProgramme.programme.dureeJours} jours"
            binding.tvObjectif.text = "Objectif: ${userProgramme.programme.objectif}"
            
            // 🔍 DIAGNOSTIC: Vérifier ce qui arrive depuis l'API
            android.util.Log.d("MonProgrammeDetail", "Programme reçu: ${userProgramme.programme.nom}")
            android.util.Log.d("MonProgrammeDetail", "Nombre de plats: ${userProgramme.programme.plats.size}")
            android.util.Log.d("MonProgrammeDetail", "Nombre d'activités: ${userProgramme.programme.activites.size}")
            
            if (userProgramme.programme.plats.isEmpty()) {
                android.util.Log.w("MonProgrammeDetail", "⚠️ PROBLÈME: Aucun plat trouvé dans le programme!")
                Toast.makeText(this, "⚠️ Aucun plat trouvé dans ce programme", Toast.LENGTH_LONG).show()
            }
            
            if (userProgramme.programme.activites.isEmpty()) {
                android.util.Log.w("MonProgrammeDetail", "⚠️ PROBLÈME: Aucune activité trouvée dans le programme!")
                Toast.makeText(this, "⚠️ Aucune activité trouvée dans ce programme", Toast.LENGTH_LONG).show()
            }
            
            // ⚠️ Progression vient des STATISTIQUES, pas de UserProgramme
            // On l'affichera quand les stats seront chargées
            
            // Poids
            userProgramme.poidsDebut?.let { debut ->
                binding.tvPoidsDebut.text = "Début: ${debut}kg"
            }
            userProgramme.poidsActuel?.let { actuel ->
                binding.tvPoidsActuel.text = "Actuel: ${actuel}kg"
            }
            userProgramme.poidsObjectif?.let { objectif ->
                binding.tvPoidsObjectif.text = "Objectif: ${objectif}kg"
            }
            
            // Plats et activités du programme
            platsAdapter.submitList(userProgramme.programme.plats ?: emptyList())
            activitesAdapter.submitList(userProgramme.programme.activites ?: emptyList())
            
            // 🔧 SOLUTION: Afficher/masquer les messages d'aide selon le contenu
            val plats = userProgramme.programme.plats ?: emptyList()
            val activites = userProgramme.programme.activites ?: emptyList()
            
            if (plats.isEmpty()) {
                binding.tvPlatsEmpty.visibility = View.VISIBLE
                binding.layoutPlatsActions.visibility = View.GONE
                binding.rvPlats.visibility = View.GONE
            } else {
                binding.tvPlatsEmpty.visibility = View.GONE
                binding.layoutPlatsActions.visibility = View.VISIBLE
                binding.rvPlats.visibility = View.VISIBLE
            }
            
            if (activites.isEmpty()) {
                binding.tvActivitesEmpty.visibility = View.VISIBLE
                binding.layoutActivitesActions.visibility = View.GONE
                binding.rvActivites.visibility = View.GONE
            } else {
                binding.tvActivitesEmpty.visibility = View.GONE
                binding.layoutActivitesActions.visibility = View.VISIBLE
                binding.rvActivites.visibility = View.VISIBLE
            }
            
            // 🔍 DIAGNOSTIC: Afficher un message si les listes sont vides
            if (plats.isEmpty() && activites.isEmpty()) {
                binding.tvStatutJour.text = "⚠️ Aucun contenu trouvé dans ce programme"
                binding.btnEnregistrerJournee.isEnabled = false
                binding.btnEnregistrerJournee.text = "❌ Programme sans contenu"
            } else {
                binding.btnEnregistrerJournee.isEnabled = true
                binding.btnEnregistrerJournee.text = "✅ ENREGISTRER MA JOURNÉE"
            }
        }

        viewModel.progressionJour.observe(this) { progression ->
            if (progression != null) {
                // ✅ CORRIGÉ : Utilisation de ?.map avec fallback à emptyList()
                val platsConsommesIds = progression.platsConsommes?.map { it.id } ?: emptyList()
                platsAdapter.setPlatsConsommes(platsConsommesIds)

                val activitesRealisesIds = progression.activitesRealisees?.map { it.id } ?: emptyList()
                activitesAdapter.setActivitesRealisees(activitesRealisesIds)

                // ✅ CORRIGÉ : Passer statutJour (peut être null)
                binding.tvStatutJour.text = formatStatutJour(progression.statutJour)

                // Afficher les calories
                progression.caloriesConsommees?.let {
                    binding.tvCalories.text = "Calories: ${it} kcal"
                } ?: run {
                    binding.tvCalories.text = "Calories: 0 kcal"
                }
            } else {
                // Réinitialiser les sélections
                platsAdapter.setPlatsConsommes(emptyList())
                activitesAdapter.setActivitesRealisees(emptyList())
                binding.tvStatutJour.text = "❌ Aucune activité enregistrée"
                binding.tvCalories.text = "Calories: 0 kcal"
            }
        }
        viewModel.statistiques.observe(this) { stats ->
            stats?.let {
                // Mettre à jour la progression avec les stats calculées
                binding.progressBar.progress = it.progressionGlobale
                binding.tvProgression.text = "${it.progressionGlobale}%"
                
                // Afficher les détails de progression
                binding.tvTauxCompletion.text = "Complétion: ${it.tauxCompletion}%"
                binding.tvTauxRepas.text = "Repas: ${it.tauxRepas}%"
                binding.tvTauxActivites.text = "Activités: ${it.tauxActivites}%"
                binding.tvStreak.text = "🔥 Série: ${it.streakActuel} jours"
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                if (it.contains("Aucun programme actif", ignoreCase = true)) {
                    Toast.makeText(this, "⚠️ Vous devez d'abord vous inscrire à un programme!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }
        }
        
        viewModel.ajoutSuccess.observe(this) { success ->
            if (success) {
                // ✅ AMÉLIORATION: Feedback visuel de succès
                binding.btnEnregistrerJournee.text = "✅ Enregistré avec succès!"
                binding.btnEnregistrerJournee.backgroundTintList = 
                    android.content.res.ColorStateList.valueOf(getColor(R.color.green))
                
                // Animation de succès
                val scaleAnimation = android.view.animation.ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f)
                scaleAnimation.duration = 200
                scaleAnimation.repeatCount = 1
                scaleAnimation.repeatMode = android.view.animation.Animation.REVERSE
                binding.btnEnregistrerJournee.startAnimation(scaleAnimation)
                
                Toast.makeText(this, "✅ Enregistré avec succès!", Toast.LENGTH_SHORT).show()
                
                // Remettre le bouton normal après 2 secondes
                binding.btnEnregistrerJournee.postDelayed({
                    binding.btnEnregistrerJournee.isEnabled = true
                    updateResumeTempReel()
                }, 2000)
                
                // Recharger les données
                loadProgressionJour()
                viewModel.loadStatistiques()
            } else {
                // ✅ AMÉLIORATION: Feedback visuel d'erreur
                binding.btnEnregistrerJournee.text = "❌ Erreur - Réessayer"
                binding.btnEnregistrerJournee.backgroundTintList = 
                    android.content.res.ColorStateList.valueOf(getColor(R.color.red))
                binding.btnEnregistrerJournee.isEnabled = true
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnDatePicker.setOnClickListener {
            showDatePicker()
        }
        
        binding.btnEnregistrerPoids.setOnClickListener {
            showEnregistrerPoidsDialog()
        }
        
        binding.btnVoirStatistiques.setOnClickListener {
            // TODO: Ouvrir StatistiquesActivity
            Toast.makeText(this, "Statistiques détaillées", Toast.LENGTH_SHORT).show()
        }
        
        // ✅ BOUTON PRINCIPAL: Enregistrer ma journée
        binding.btnEnregistrerJournee.setOnClickListener {
            enregistrerJourneeComplete()
        }
        
        // ✅ NOUVEAUX BOUTONS: Sélection rapide des plats
        binding.btnToutSelectionnerPlats.setOnClickListener {
            platsAdapter.selectAll()
            updateResumeTempReel()
        }
        
        binding.btnToutDeselectionnerPlats.setOnClickListener {
            platsAdapter.deselectAll()
            updateResumeTempReel()
        }
        
        binding.btnSelectionnerPetitDej.setOnClickListener {
            platsAdapter.selectByCategory("PETIT_DEJEUNER")
            updateResumeTempReel()
        }
        
        // ✅ NOUVEAUX BOUTONS: Sélection rapide des activités
        binding.btnToutSelectionnerActivites.setOnClickListener {
            activitesAdapter.selectAll()
            updateResumeTempReel()
        }
        
        binding.btnToutDeselectionnerActivites.setOnClickListener {
            activitesAdapter.deselectAll()
            updateResumeTempReel()
        }
        
        binding.btnSelectionnerCardio.setOnClickListener {
            activitesAdapter.selectByType("CARDIO")
            updateResumeTempReel()
        }
    }
    
    private fun loadData() {
        if (userProgrammeId == 0) {
            // Pas d'ID spécifique, charger le programme actif
            viewModel.loadUserProgramme(0)
        } else {
            viewModel.loadUserProgramme(userProgrammeId)
        }
        loadProgressionJour()
        viewModel.loadStatistiques()
    }
    
    private fun loadProgressionJour() {
        val dateStr = dateFormat.format(currentDate.time)
        binding.tvDate.text = "📅 $dateStr"
        viewModel.loadProgressionJour(dateStr)
    }
    
    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                currentDate.set(year, month, day)
                loadProgressionJour()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    // ✅ NOUVELLE MÉTHODE: Enregistrer TOUTE la journée en UN SEUL appel
    private fun enregistrerJourneeComplete() {
        // 1. Récupérer les IDs des plats cochés
        val platIds = platsAdapter.getSelectedPlatIds()
        
        // 2. Récupérer les IDs des activités cochées
        val activiteIds = activitesAdapter.getSelectedActiviteIds()
        
        // 3. Vérifier qu'il y a au moins quelque chose à enregistrer
        if (platIds.isEmpty() && activiteIds.isEmpty()) {
            Toast.makeText(this, "Veuillez cocher au moins un plat ou une activité", Toast.LENGTH_SHORT).show()
            return
        }
        
        // ✅ AMÉLIORATION: Feedback visuel pendant l'enregistrement
        binding.btnEnregistrerJournee.text = "⏳ Enregistrement en cours..."
        binding.btnEnregistrerJournee.isEnabled = false
        
        // 4. Créer la requête
        val dateStr = dateFormat.format(currentDate.time)
        val request = com.example.projetintegration.data.models.EnregistrerProgressionRequest(
            date = dateStr,
            platIds = platIds.ifEmpty { null },
            activiteIds = activiteIds.ifEmpty { null },
            poidsJour = null, // Sera ajouté séparément avec le bouton poids
            notes = null
        )
        
        // 5. Envoyer UN SEUL appel API
        viewModel.enregistrerProgressionComplete(request)
    }
    
    private fun showEnregistrerPoidsDialog() {
        val builder = AlertDialog.Builder(this)
        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Poids (kg)"
        
        builder.setTitle("Enregistrer le poids")
            .setView(input)
            .setPositiveButton("Enregistrer") { _, _ ->
                val poidsStr = input.text.toString()
                if (poidsStr.isNotEmpty()) {
                    val poids = poidsStr.toDoubleOrNull()
                    if (poids != null && poids > 0) {
                        val dateStr = dateFormat.format(currentDate.time)
                        viewModel.enregistrerPoidsSeul(dateStr, poids)
                    } else {
                        Toast.makeText(this, "Poids invalide", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun formatStatutJour(statut: String?): String { // ✅ Changez en String?
        return when (statut?.uppercase()) { // ✅ Utilisez ?.uppercase()
            "COMPLETE" -> "✅ Journée complète"
            "PARTIEL" -> "⚠️ Journée partielle"
            "NON_FAIT" -> "❌ Aucune activité"
            else -> "❓ Statut inconnu" // ✅ Valeur par défaut
        }
    }
    
    // ✅ NOUVELLE MÉTHODE: Mise à jour du résumé en temps réel
    private fun updateResumeTempReel() {
        val platIds = platsAdapter.getSelectedPlatIds()
        val activiteIds = activitesAdapter.getSelectedActiviteIds()
        
        // Calculer les calories sélectionnées
        val caloriesConsommees = viewModel.userProgramme.value?.programme?.plats
            ?.filter { platIds.contains(it.id) }
            ?.sumOf { it.calories } ?: 0
            
        val caloriesBrulees = viewModel.userProgramme.value?.programme?.activites
            ?.filter { activiteIds.contains(it.id) }
            ?.sumOf { it.caloriesBrulees } ?: 0
        
        // Mettre à jour l'affichage
        binding.tvCalories.text = "📊 ${caloriesConsommees} kcal consommées | ${caloriesBrulees} kcal brûlées"
        
        // Mettre à jour le statut temporaire
        val statutTemp = when {
            platIds.isEmpty() && activiteIds.isEmpty() -> "❌ Aucune sélection"
            platIds.isNotEmpty() && activiteIds.isNotEmpty() -> "✅ Journée complète (non sauvée)"
            else -> "⚠️ Journée partielle (non sauvée)"
        }
        
        // Changer la couleur du bouton selon l'état
        if (platIds.isNotEmpty() || activiteIds.isNotEmpty()) {
            binding.btnEnregistrerJournee.text = "✅ ENREGISTRER MA JOURNÉE (${platIds.size + activiteIds.size} éléments)"
            binding.btnEnregistrerJournee.backgroundTintList = 
                android.content.res.ColorStateList.valueOf(getColor(R.color.organic_primary))
        } else {
            binding.btnEnregistrerJournee.text = "✅ ENREGISTRER MA JOURNÉE"
            binding.btnEnregistrerJournee.backgroundTintList = 
                android.content.res.ColorStateList.valueOf(getColor(R.color.organic_text_secondary))
        }
    }
}
```

### 5. **StatistiquesActivity.kt** - Écran des Statistiques
```kotlin
package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projetintegration.databinding.ActivityStatistiquesBinding
import com.example.projetintegration.ui.adapters.BadgesAdapter
import com.example.projetintegration.ui.viewmodel.StatistiquesViewModel

class StatistiquesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatistiquesBinding
    private lateinit var viewModel: StatistiquesViewModel
    private lateinit var badgesAdapter: BadgesAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatistiquesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[StatistiquesViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        loadData()
    }
    
    private fun setupRecyclerView() {
        badgesAdapter = BadgesAdapter()
        binding.rvBadges.apply {
            layoutManager = GridLayoutManager(this@StatistiquesActivity, 2)
            adapter = badgesAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.statistiques.observe(this) { stats ->
            // Progression globale (Score calculé par le backend)
            binding.progressBar.progress = stats.progressionGlobale
            binding.tvProgressionGlobale.text = "${stats.progressionGlobale}%"
            
            // Jours
            binding.tvJourActuel.text = "Jour ${stats.jourActuel}/${stats.joursTotal}"
            binding.tvJoursRestants.text = "${stats.joursRestants} jours restants"
            
            // Taux de Complétion (40% du score global)
            binding.tvTauxCompletion.text = "${stats.tauxCompletion}%"
            
            // Taux de Repas (30% du score global)
            binding.tvTauxRepas.text = "${stats.tauxRepas}%"
            binding.tvTotalPlats.text = "${stats.totalPlatsConsommes} plats consommés"
            
            // Taux d'Activités (20% du score global)
            binding.tvTauxActivites.text = "${stats.tauxActivites}%"
            binding.tvTotalActivites.text = "${stats.totalActivitesRealisees} activités réalisées"
            
            // Évolution Physique (10% du score global)
            binding.tvEvolutionPhysiqueDetail.text = "${stats.evolutionPhysique}%"
            stats.poidsDebut?.let { debut ->
                binding.tvPoidsDebut.text = String.format("%.1f kg", debut)
            }
            stats.poidsActuel?.let { actuel ->
                binding.tvPoidsActuel.text = String.format("%.1f kg", actuel)
            }
            stats.poidsObjectif?.let { objectif ->
                binding.tvPoidsObjectif.text = String.format("%.1f kg", objectif)
            }
            stats.evolutionPoids?.let { evolution ->
                val signe = if (evolution > 0) "+" else ""
                binding.tvEvolutionPoids.text = String.format("%s%.1f kg", signe, evolution)
                
                // Couleur selon l'évolution
                val color = if (evolution < 0) {
                    android.graphics.Color.parseColor("#4CAF50") // Vert pour perte
                } else {
                    android.graphics.Color.parseColor("#FF9800") // Orange pour gain
                }
                binding.tvEvolutionPoids.setTextColor(color)
            }
            
            // Streak
            binding.tvStreakActuel.text = "${stats.streakActuel}"
            binding.tvMeilleurStreak.text = "Record: ${stats.meilleurStreak} jours"
            
            // Calories moyennes
            binding.tvCaloriesMoyennes.text = "${stats.caloriesMoyennes} kcal"
            
            // Jours actifs
            binding.tvJoursActifs.text = "${stats.joursActifs} jours"
            
            // Badges
            badgesAdapter.submitList(stats.badges)
            
            // Afficher les détails de calcul
            afficherDetailsCalcul(stats)
        }
        
        viewModel.programmeActif.observe(this) { programme ->
            binding.tvProgrammeName.text = programme.programme.nom
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun loadData() {
        viewModel.loadProgrammeActif()
        viewModel.loadStatistiques()
    }
    
    private fun afficherDetailsCalcul(stats: com.example.projetintegration.data.models.Statistiques) {
        // Les détails sont déjà affichés dans le layout
        // Cette méthode peut être utilisée pour des logs ou analytics
    }
}
```

---

## 🔄 Adapters (UI Components)

### 1. **ProgrammesAdapter.kt** - Adapter Liste des Programmes
```kotlin
package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.databinding.ItemProgrammeBinding

class ProgrammesAdapter(
    private val onProgrammeClick: (Programme) -> Unit
) : ListAdapter<Programme, ProgrammesAdapter.ProgrammeViewHolder>(ProgrammeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgrammeViewHolder {
        val binding = ItemProgrammeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProgrammeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ProgrammeViewHolder(
        private val binding: ItemProgrammeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(programme: Programme) {
            binding.tvNom.text = programme.nom
            binding.tvDescription.text = programme.description
            binding.tvDuree.text = "${programme.dureeJours} jours"
            binding.tvObjectif.text = formatObjectif(programme.objectif)
            binding.tvPlatsCount.text = "${programme.plats.size} plats"
            binding.tvActivitesCount.text = "${programme.activites.size} activités"
            
            binding.root.setOnClickListener {
                onProgrammeClick(programme)
            }
        }
        
        private fun formatObjectif(objectif: String): String {
            return when (objectif) {
                "perte-poids" -> "🎯 Perte de poids"
                "prise-masse" -> "💪 Prise de masse"
                "maintien" -> "⚖️ Maintien"
                "endurance" -> "🏃 Endurance"
                else -> objectif
            }
        }
    }
    
    class ProgrammeDiffCallback : DiffUtil.ItemCallback<Programme>() {
        override fun areItemsTheSame(oldItem: Programme, newItem: Programme): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Programme, newItem: Programme): Boolean {
            return oldItem == newItem
        }
    }
}
```

### 2. **MesProgrammesAdapter.kt** - Adapter Mes Programmes

```kotlin
package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.databinding.ItemMesProgrammesBinding
import kotlin.math.min

class MesProgrammesAdapter : ListAdapter<UserProgramme, MesProgrammesAdapter.UserProgrammeViewHolder>(UserProgrammeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProgrammeViewHolder {
        val binding = ItemMesProgrammesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserProgrammeViewHolder(binding, parent.context)
    }
    
    override fun onBindViewHolder(holder: UserProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class UserProgrammeViewHolder(
        private val binding: ItemMesProgrammesBinding,
        private val context: android.content.Context
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(userProgramme: UserProgramme) {
            binding.tvNom.text = userProgramme.programme.nom
            binding.tvDateDebut.text = "Début: ${userProgramme.dateDebut}"
            userProgramme.dateFin?.let {
                binding.tvDateFin.text = "Fin: $it"
            }
            
            binding.tvStatut.text = formatStatut(userProgramme.statut)
            binding.tvStatut.setBackgroundColor(getStatutColor(userProgramme.statut))
            
            // Afficher le nombre de plats et activités (avec protection contre null)
            val nbPlats = userProgramme.programme.plats.size
            val nbActivites = userProgramme.programme.activites.size
            binding.tvProgression.text = "📋 $nbPlats plats • 💪 $nbActivites activités"
            
            // La progression vient des STATISTIQUES, pas de UserProgramme
            binding.progressBar.progress = 0
            
            // Afficher les informations de poids si disponibles
            userProgramme.poidsDebut?.let { debut ->
                userProgramme.poidsObjectif?.let { objectif ->
                    userProgramme.poidsActuel?.let { actuel ->
                        val evolution = debut - actuel
                        val signe = if (evolution > 0) "-" else "+"
                        binding.tvPoids.text = "Poids: ${actuel}kg (${signe}${kotlin.math.abs(evolution)}kg)"
                    }
                }
            }
            
            // Click listener pour ouvrir les détails
            binding.root.setOnClickListener {
                val intent = android.content.Intent(context, com.example.projetintegration.ui.activities.MonProgrammeDetailActivity::class.java)
                intent.putExtra("USER_PROGRAMME_ID", userProgramme.id)
                context.startActivity(intent)
            }
        }
        
        private fun formatStatut(statut: String): String {
            return when (statut.uppercase()) {
                "EN_COURS", "EN-COURS" -> "En cours"
                "TERMINE", "TERMINÉ" -> "Terminé"
                "ABANDONNE", "ABANDONNÉ" -> "Abandonné"
                "PAUSE" -> "En pause"
                else -> statut
            }
        }
        
        private fun getStatutColor(statut: String): Int {
            return when (statut.uppercase()) {
                "EN_COURS", "EN-COURS" -> 0xFF2196F3.toInt()
                "TERMINE", "TERMINÉ" -> 0xFF4CAF50.toInt()
                "ABANDONNE", "ABANDONNÉ" -> 0xFFF44336.toInt()
                "PAUSE" -> 0xFFFF9800.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }
    }
    
    class UserProgrammeDiffCallback : DiffUtil.ItemCallback<UserProgramme>() {
        override fun areItemsTheSame(oldItem: UserProgramme, newItem: UserProgramme): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: UserProgramme, newItem: UserProgramme): Boolean {
            return oldItem == newItem
        }
    }
}
```

### 3. **PlatsSelectionAdapter.kt** - Adapter Sélection des Plats
```kotlin
package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ItemPlatSelectionBinding

class PlatsSelectionAdapter(
    private val onPlatChecked: (Plat, Boolean) -> Unit
) : ListAdapter<Plat, PlatsSelectionAdapter.PlatViewHolder>(PlatDiffCallback()) {
    
    private val platsConsommesIds = mutableSetOf<Int>()
    private val selectedPlatIds = mutableSetOf<Int>()
    
    fun setPlatsConsommes(ids: List<Int>) {
        platsConsommesIds.clear()
        platsConsommesIds.addAll(ids)
        selectedPlatIds.clear()
        selectedPlatIds.addAll(ids)
        notifyDataSetChanged()
    }
    
    // ✅ NOUVELLE MÉTHODE: Récupérer les IDs sélectionnés
    fun getSelectedPlatIds(): List<Int> {
        return selectedPlatIds.toList()
    }
    
    // ✅ NOUVELLES MÉTHODES: Sélection rapide
    fun selectAll() {
        selectedPlatIds.clear()
        selectedPlatIds.addAll(currentList.map { it.id })
        notifyDataSetChanged()
    }
    
    fun deselectAll() {
        selectedPlatIds.clear()
        notifyDataSetChanged()
    }
    
    fun selectByCategory(category: String) {
        selectedPlatIds.clear()
        selectedPlatIds.addAll(
            currentList.filter { 
                it.categorie.equals(category, ignoreCase = true) ||
                it.categorie.replace("_", "-").equals(category.replace("_", "-"), ignoreCase = true)
            }.map { it.id }
        )
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatViewHolder {
        val binding = ItemPlatSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PlatViewHolder, position: Int) {
        holder.bind(getItem(position), platsConsommesIds.contains(getItem(position).id))
    }
    
    inner class PlatViewHolder(
        private val binding: ItemPlatSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(plat: Plat, isConsomme: Boolean) {
            binding.tvNom.text = plat.nom
            binding.tvCalories.text = "${plat.calories} kcal"
            binding.tvCategorie.text = formatCategorie(plat.categorie)
            binding.tvTemps.text = "⏱️ ${plat.tempsPreparation} min"
            
            // Désactiver le listener temporairement
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = isConsomme
            
            // ✅ AMÉLIORATION: Feedback visuel selon l'état
            updateVisualState(isConsomme)
            
            // Réactiver le listener
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                // ✅ AMÉLIORATION: Animation lors du changement
                animateStateChange(isChecked)
                
                if (isChecked) {
                    selectedPlatIds.add(plat.id)
                    onPlatChecked(plat, true)
                } else {
                    selectedPlatIds.remove(plat.id)
                    onPlatChecked(plat, false)
                }
            }
            
            binding.root.setOnClickListener {
                binding.checkbox.isChecked = !binding.checkbox.isChecked
            }
        }
        
        // ✅ NOUVELLE MÉTHODE: Mise à jour visuelle selon l'état
        private fun updateVisualState(isConsomme: Boolean) {
            if (isConsomme) {
                // État "consommé" - style success
                binding.root.alpha = 1.0f
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.holo_green_light).let { color ->
                        android.graphics.Color.argb(30, 
                            android.graphics.Color.red(color),
                            android.graphics.Color.green(color),
                            android.graphics.Color.blue(color)
                        )
                    }
                )
                binding.tvNom.setTextColor(binding.root.context.getColor(com.example.projetintegration.R.color.organic_primary))
            } else {
                // État normal
                binding.root.alpha = 1.0f
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(com.example.projetintegration.R.color.organic_surface)
                )
                binding.tvNom.setTextColor(binding.root.context.getColor(com.example.projetintegration.R.color.organic_text_primary))
            }
        }
        
        // ✅ NOUVELLE MÉTHODE: Animation lors du changement d'état
        private fun animateStateChange(isChecked: Boolean) {
            val scaleAnimation = if (isChecked) {
                android.view.animation.ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f)
            } else {
                android.view.animation.ScaleAnimation(1.05f, 1.0f, 1.05f, 1.0f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f)
            }
            
            scaleAnimation.duration = 150
            scaleAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    updateVisualState(isChecked)
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            
            binding.root.startAnimation(scaleAnimation)
        }
        
        private fun formatCategorie(categorie: String): String {
            return when (categorie.lowercase()) {
                "petit-dejeuner", "petit_dejeuner" -> "🌅 Petit-déjeuner"
                "dejeuner", "déjeuner" -> "☀️ Déjeuner"
                "diner", "dîner" -> "🌙 Dîner"
                "collation" -> "🍎 Collation"
                else -> categorie
            }
        }
    }
    
    class PlatDiffCallback : DiffUtil.ItemCallback<Plat>() {
        override fun areItemsTheSame(oldItem: Plat, newItem: Plat): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Plat, newItem: Plat): Boolean {
            return oldItem == newItem
        }
    }
}
```

### 4. **ActivitesSelectionAdapter.kt** - Adapter Sélection des Activités
```kotlin
package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.ActiviteSportive
import com.example.projetintegration.databinding.ItemActiviteSelectionBinding

class ActivitesSelectionAdapter(
    private val onActiviteChecked: (ActiviteSportive, Boolean) -> Unit
) : ListAdapter<ActiviteSportive, ActivitesSelectionAdapter.ActiviteViewHolder>(ActiviteDiffCallback()) {
    
    private val activitesRealisesIds = mutableSetOf<Int>()
    private val selectedActiviteIds = mutableSetOf<Int>()
    
    fun setActivitesRealisees(ids: List<Int>) {
        activitesRealisesIds.clear()
        activitesRealisesIds.addAll(ids)
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(ids)
        notifyDataSetChanged()
    }
    
    // ✅ NOUVELLE MÉTHODE: Récupérer les IDs sélectionnés
    fun getSelectedActiviteIds(): List<Int> {
        return selectedActiviteIds.toList()
    }
    
    // ✅ NOUVELLES MÉTHODES: Sélection rapide
    fun selectAll() {
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(currentList.map { it.id })
        notifyDataSetChanged()
    }
    
    fun deselectAll() {
        selectedActiviteIds.clear()
        notifyDataSetChanged()
    }
    
    fun selectByType(type: String) {
        // Comme il n'y a pas de propriété 'type' dans le modèle, 
        // on peut sélectionner par niveau ou par nom contenant le type
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(
            currentList.filter { 
                it.nom.contains(type, ignoreCase = true) ||
                it.description.contains(type, ignoreCase = true)
            }.map { it.id }
        )
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiviteViewHolder {
        val binding = ItemActiviteSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActiviteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ActiviteViewHolder, position: Int) {
        holder.bind(getItem(position), activitesRealisesIds.contains(getItem(position).id))
    }
    
    inner class ActiviteViewHolder(
        private val binding: ItemActiviteSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(activite: ActiviteSportive, isRealisee: Boolean) {
            binding.tvNom.text = activite.nom
            binding.tvDuree.text = "⏱️ ${activite.duree} min"
            binding.tvCaloriesBrulees.text = "-${activite.caloriesBrulees} kcal"
            binding.tvNiveau.text = formatNiveau(activite.niveau)
            
            // Désactiver le listener temporairement
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = isRealisee
            
            // ✅ AMÉLIORATION: Feedback visuel selon l'état
            updateVisualState(isRealisee)
            
            // Réactiver le listener
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                // ✅ AMÉLIORATION: Animation lors du changement
                animateStateChange(isChecked)
                
                if (isChecked) {
                    selectedActiviteIds.add(activite.id)
                    onActiviteChecked(activite, true)
                } else {
                    selectedActiviteIds.remove(activite.id)
                    onActiviteChecked(activite, false)
                }
            }
            
            binding.root.setOnClickListener {
                binding.checkbox.isChecked = !binding.checkbox.isChecked
            }
        }
        
        // ✅ NOUVELLE MÉTHODE: Mise à jour visuelle selon l'état
        private fun updateVisualState(isRealisee: Boolean) {
            if (isRealisee) {
                // État "réalisé" - style success
                binding.root.alpha = 1.0f
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.holo_blue_light).let { color ->
                        android.graphics.Color.argb(30, 
                            android.graphics.Color.red(color),
                            android.graphics.Color.green(color),
                            android.graphics.Color.blue(color)
                        )
                    }
                )
                binding.tvNom.setTextColor(binding.root.context.getColor(com.example.projetintegration.R.color.organic_primary))
            } else {
                // État normal
                binding.root.alpha = 1.0f
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(com.example.projetintegration.R.color.organic_surface)
                )
                binding.tvNom.setTextColor(binding.root.context.getColor(com.example.projetintegration.R.color.organic_text_primary))
            }
        }
        
        // ✅ NOUVELLE MÉTHODE: Animation lors du changement d'état
        private fun animateStateChange(isChecked: Boolean) {
            val scaleAnimation = if (isChecked) {
                android.view.animation.ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f)
            } else {
                android.view.animation.ScaleAnimation(1.05f, 1.0f, 1.05f, 1.0f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f)
            }
            
            scaleAnimation.duration = 150
            scaleAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    updateVisualState(isChecked)
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            
            binding.root.startAnimation(scaleAnimation)
        }
        
        private fun formatNiveau(niveau: String): String {
            return when (niveau.lowercase()) {
                "debutant" -> "🟢 Débutant"
                "intermediaire", "intermédiaire" -> "🟡 Intermédiaire"
                "avance", "avancé" -> "🔴 Avancé"
                else -> niveau
            }
        }
    }
    
    class ActiviteDiffCallback : DiffUtil.ItemCallback<ActiviteSportive>() {
        override fun areItemsTheSame(oldItem: ActiviteSportive, newItem: ActiviteSportive): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ActiviteSportive, newItem: ActiviteSportive): Boolean {
            return oldItem == newItem
        }
    }
}
```

### 5. **BadgesAdapter.kt** - Adapter des Badges

```kotlin
package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Badge
import com.example.projetintegration.databinding.ItemBadgeBinding

class BadgesAdapter : ListAdapter<Badge, BadgesAdapter.BadgeViewHolder>(BadgeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BadgeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class BadgeViewHolder(
        private val binding: ItemBadgeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(badge: Badge) {
            binding.tvBadgeIcon.text = badge.icone
            binding.tvBadgeTitre.text = badge.titre
            binding.tvBadgeDescription.text = badge.description
        }
    }
    
    class BadgeDiffCallback : DiffUtil.ItemCallback<Badge>() {
        override fun areItemsTheSame(oldItem: Badge, newItem: Badge): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Badge, newItem: Badge): Boolean {
            return oldItem == newItem
        }
    }
}
```

---

## 🔗 Flux de Données et Relations

### **1. Flux Principal - Inscription à un Programme**
```
ProgrammesActivity → ProgrammeDetailActivity → AssignerProgramme API
                                            ↓
                                      UserProgramme créé
                                            ↓
                              MesProgrammesActivity (affiche le nouveau programme)
```

### **2. Flux Principal - Progression Quotidienne**
```
MesProgrammesActivity → MonProgrammeDetailActivity → Sélection Plats/Activités
                                                   ↓
                                            EnregistrerProgression API
                                                   ↓
                                            ProgressionJournaliere créée
                                                   ↓
                                            Statistiques mises à jour
```

### **3. Relations entre les Endpoints API**
```
GET /api/programmes              → Liste des programmes disponibles
GET /api/programmes/{id}         → Détails d'un programme spécifique
POST /api/programmes/assigner    → Assigner un programme à l'utilisateur
GET /api/programmes/actif        → Programme actuel de l'utilisateur
GET /api/programmes/historique   → Tous les programmes de l'utilisateur
GET /api/programmes/statistiques → Statistiques calculées
POST /api/progression/enregistrer → Enregistrer la progression quotidienne
GET /api/progression/historique  → Historique des progressions
GET /api/progression/aujourd-hui → Progression du jour actuel
```

---

## 🎯 Points Clés du Système

### **✅ Fonctionnalités Implémentées**
1. **Consultation des programmes** disponibles
2. **Inscription** à un programme
3. **Suivi quotidien** des plats et activités
4. **Calcul automatique** des statistiques
5. **Historique** des programmes et progressions
6. **Interface intuitive** avec sélection rapide
7. **Feedback visuel** en temps réel

### **🔧 Améliorations Récentes**
1. **Protection contre les valeurs null** dans les modèles
2. **Chargement automatique** des détails manquants
3. **Interface adaptative** selon le contenu disponible
4. **Enregistrement unifié** de la progression
5. **Animations et feedback** utilisateur

### **⚠️ Points d'Attention**
1. **Contenu des programmes** : Les endpoints `UserProgramme` peuvent retourner des programmes sans plats/activités
2. **Gestion des erreurs** : Protection contre les réponses null ou vides
3. **Synchronisation** : Les statistiques sont calculées côté backend
4. **Performance** : Chargement intelligent des détails seulement si nécessaire

---

## 🚀 Conclusion

Ce système complet gère l'intégralité du processus de programmes et progression dans l'application FitLife, depuis la consultation des programmes disponibles jusqu'au suivi quotidien détaillé avec calcul automatique des statistiques et badges.

**Architecture robuste** : Séparation claire des responsabilités avec Repository Pattern et MVVM
**Interface intuitive** : Adapters avec sélection rapide et feedback visuel
**Gestion d'erreurs** : Protection contre les cas limites et données manquantes
**Évolutivité** : Structure modulaire permettant l'ajout de nouvelles fonctionnalités

**Total : 25+ classes** couvrant tous les aspects du système de programmes et progression ! 🎉