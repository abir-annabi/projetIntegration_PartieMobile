package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityMesProgrammesBinding
import com.example.projetintegration.ui.adapters.MesProgrammesAdapter
import com.example.projetintegration.ui.viewmodel.MesProgrammesViewModel
import com.example.projetintegration.ui.viewmodel.FavoriViewModel

class MesProgrammesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMesProgrammesBinding
    private lateinit var viewModel: MesProgrammesViewModel
    private lateinit var favoriViewModel: FavoriViewModel
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mesProgrammesAdapter: MesProgrammesAdapter
    
    // État du filtre favoris
    private var isFilteringFavorites = false
    private var allProgrammes: List<UserProgramme> = emptyList()
    private var favoritesProgrammeIds: Set<Long> = emptySet()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMesProgrammesBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            viewModel = ViewModelProvider(this)[MesProgrammesViewModel::class.java]
            favoriViewModel = ViewModelProvider(this)[FavoriViewModel::class.java]
            preferencesManager = PreferencesManager(this)
            
            setupRecyclerView()
            setupObservers()
            setupClickListeners()
            loadMesProgrammes()
            
            android.util.Log.d("MesProgrammesActivity", "Activity créée avec succès")
        } catch (e: Exception) {
            android.util.Log.e("MesProgrammesActivity", "Erreur critique lors de la création de l'activity", e)
            
            // Fallback: fermer l'activité proprement
            Toast.makeText(this, "Erreur lors du chargement. Veuillez réessayer.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupObservers() {
        viewModel.mesProgrammes.observe(this) { mesProgrammes ->
            try {
                // 🔧 PROTECTION: Vérifier que la liste n'est pas null
                val programmes = mesProgrammes ?: emptyList()
                
                android.util.Log.d("MesProgrammesActivity", "📱 Observer déclenché: ${programmes.size} programmes reçus")
                
                // Stocker tous les programmes
                allProgrammes = programmes
                
                // Appliquer le filtre si nécessaire
                val programmesToShow = if (isFilteringFavorites) {
                    filterFavoritesProgrammes(programmes)
                } else {
                    programmes
                }
                
                if (programmesToShow.isEmpty()) {
                    android.util.Log.w("MesProgrammesActivity", "⚠️ Aucun programme à afficher")
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.text = if (isFilteringFavorites) {
                        "Aucun programme favori.\n\n💡 Ajoutez des programmes à vos favoris en cliquant sur le cœur!"
                    } else {
                        "Aucun programme trouvé.\n\n💡 Allez dans 'Programmes' pour vous inscrire à un programme!"
                    }
                    binding.rvMesProgrammes.visibility = View.GONE
                } else {
                    android.util.Log.d("MesProgrammesActivity", "✅ Affichage de ${programmesToShow.size} programmes")
                    
                    // 🔍 DIAGNOSTIC: Afficher les détails des programmes reçus
                    programmesToShow.forEachIndexed { index, userProgramme ->
                        android.util.Log.d("MesProgrammesActivity", "Programme ${index + 1}: ${userProgramme.programme.nom} (ID: ${userProgramme.id})")
                    }
                    
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvMesProgrammes.visibility = View.VISIBLE
                    
                    // 🔧 PROTECTION: Vérifier que l'adapter est initialisé
                    if (::mesProgrammesAdapter.isInitialized) {
                        android.util.Log.d("MesProgrammesActivity", "🔄 Mise à jour de l'adapter avec ${programmesToShow.size} programmes")
                        mesProgrammesAdapter.submitList(programmesToShow)
                    } else {
                        android.util.Log.e("MesProgrammesActivity", "❌ Adapter non initialisé! Réinitialisation...")
                        setupRecyclerView()
                        mesProgrammesAdapter.submitList(programmesToShow)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesActivity", "💥 Erreur lors de l'affichage des programmes", e)
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = "⚠️ Erreur lors du chargement\n\nVeuillez réessayer"
                binding.rvMesProgrammes.visibility = View.GONE
            }
        }
        
        // Observer les programmes favoris pour le filtre
        favoriViewModel.favorisProgrammes.observe(this) { favoris ->
            favoritesProgrammeIds = favoris.map { it.programmeId }.toSet()
            android.util.Log.d("MesProgrammesActivity", "💖 Programmes favoris mis à jour: ${favoritesProgrammeIds.size}")
            
            // Réappliquer le filtre si actif
            if (isFilteringFavorites && allProgrammes.isNotEmpty()) {
                val filteredProgrammes = filterFavoritesProgrammes(allProgrammes)
                mesProgrammesAdapter.submitList(filteredProgrammes)
                updateEmptyState(filteredProgrammes.isEmpty())
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            try {
                binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesActivity", "Erreur lors de l'affichage du loading", e)
            }
        }
        
        viewModel.error.observe(this) { error ->
            try {
                error?.let {
                    if (it.contains("Aucun programme", ignoreCase = true)) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        binding.tvEmpty.text = "Aucun programme trouvé.\n\n" +
                                "💡 Allez dans 'Programmes' pour vous inscrire à un programme!"
                        binding.rvMesProgrammes.visibility = View.GONE
                    } else {
                        Toast.makeText(this, "Erreur: $it", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesActivity", "Erreur lors de l'affichage de l'erreur", e)
            }
        }
        
        viewModel.statistiques.observe(this) { stats ->
            // ✅ NOUVELLE LOGIQUE SIMPLE: Passer les statistiques à l'adapter
            if (::mesProgrammesAdapter.isInitialized) {
                mesProgrammesAdapter.updateStatistiques(stats)
                android.util.Log.d("MesProgrammesActivity", "📊 Statistiques mises à jour (nouvelle logique simple)")
                if (stats != null) {
                    android.util.Log.d("MesProgrammesActivity", "   - Progression simple: ${stats.progressionGlobale}%")
                    android.util.Log.d("MesProgrammesActivity", "   - Formule: Éléments terminés/attendus")
                } else {
                    android.util.Log.w("MesProgrammesActivity", "   - Statistiques non disponibles")
                }
            }
        }
        
        // Observer les messages de succès des favoris
        favoriViewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                favoriViewModel.clearSuccessMessage()
            }
        }
        
        // Observer les erreurs des favoris
        favoriViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Erreur favoris: $it", Toast.LENGTH_LONG).show()
                favoriViewModel.clearError()
            }
        }
    }
    
    private fun setupRecyclerView() {
        try {
            mesProgrammesAdapter = MesProgrammesAdapter(
                favoriViewModel = favoriViewModel,
                lifecycleOwner = this,
                onDeleteProgramme = { userProgramme ->
                    android.util.Log.d("MesProgrammesActivity", "🗑️ Demande de suppression: ${userProgramme.programme.nom}")
                    viewModel.supprimerProgramme(userProgramme)
                }
            )
            
            binding.rvMesProgrammes.apply {
                layoutManager = LinearLayoutManager(this@MesProgrammesActivity)
                adapter = mesProgrammesAdapter
            }
            
            android.util.Log.d("MesProgrammesActivity", "RecyclerView configuré avec succès")
        } catch (e: Exception) {
            android.util.Log.e("MesProgrammesActivity", "Erreur lors de la configuration du RecyclerView", e)
            
            // Fallback: afficher un message d'erreur
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "⚠️ Erreur de configuration\n\nVeuillez redémarrer l'application"
            binding.rvMesProgrammes.visibility = View.GONE
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // Bouton filtre favoris
        binding.btnFilterFavorites.setOnClickListener {
            toggleFavoritesFilter()
        }
        
        // 🔧 BOUTON DE DEBUG: Forcer le rechargement
        binding.root.setOnLongClickListener {
            android.util.Log.d("MesProgrammesActivity", "🔄 RECHARGEMENT FORCÉ (long press)")
            Toast.makeText(this, "Rechargement forcé...", Toast.LENGTH_SHORT).show()
            loadMesProgrammes()
            true
        }
        
        // 🔧 BOUTON DE TEST: Double tap pour tester tous les programmes
        var lastTapTime = 0L
        binding.root.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime < 500) {
                // Double tap détecté
                android.util.Log.d("MesProgrammesActivity", "🧪 TEST: Vérification de tous les programmes disponibles")
                testAllProgrammes()
            }
            lastTapTime = currentTime
        }
    }
    
    private fun loadMesProgrammes() {
        try {
            android.util.Log.d("MesProgrammesActivity", "🔄 DÉBUT du chargement des programmes")
            
            // 🔍 DIAGNOSTIC UTILISATEUR
            val preferencesManager = PreferencesManager(this)
            val token = preferencesManager.getToken()
            val userId = preferencesManager.getUserId()
            val userEmail = preferencesManager.getUserEmail()
            val userNom = preferencesManager.getUserNom()
            val userPrenom = preferencesManager.getUserPrenom()
            
            android.util.Log.d("MesProgrammesActivity", "=== DIAGNOSTIC UTILISATEUR ===")
            android.util.Log.d("MesProgrammesActivity", "Utilisateur connecté:")
            android.util.Log.d("MesProgrammesActivity", "- ID: $userId")
            android.util.Log.d("MesProgrammesActivity", "- Email: $userEmail")
            android.util.Log.d("MesProgrammesActivity", "- Nom: $userNom $userPrenom")
            android.util.Log.d("MesProgrammesActivity", "- Token présent: ${token != null}")
            android.util.Log.d("MesProgrammesActivity", "- Token début: ${token?.take(50)}...")
            
            // 🔍 DIAGNOSTIC FILTRE FAVORIS
            android.util.Log.d("MesProgrammesActivity", "=== DIAGNOSTIC FILTRES ===")
            android.util.Log.d("MesProgrammesActivity", "- Filtre favoris actif: $isFilteringFavorites")
            android.util.Log.d("MesProgrammesActivity", "- Nombre favoris connus: ${favoritesProgrammeIds.size}")
            android.util.Log.d("MesProgrammesActivity", "- IDs favoris: $favoritesProgrammeIds")
            android.util.Log.d("MesProgrammesActivity", "===============================")
            
            if (token != null) {
                android.util.Log.d("MesProgrammesActivity", "✅ Token présent - Lancement de l'appel API")
                android.util.Log.d("MesProgrammesActivity", "URL: http://10.0.2.2:8100/api/programmes/historique")
                android.util.Log.d("MesProgrammesActivity", "Méthode: GET")
                android.util.Log.d("MesProgrammesActivity", "Headers: Authorization: Bearer ${token.take(20)}...")
            } else {
                android.util.Log.e("MesProgrammesActivity", "❌ AUCUN TOKEN! L'utilisateur n'est pas connecté")
                Toast.makeText(this, "Erreur d'authentification. Veuillez vous reconnecter.", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            
            // Le backend identifie l'utilisateur via le token JWT
            android.util.Log.d("MesProgrammesActivity", "🔄 Appel ViewModel.loadMesProgrammes()")
            viewModel.loadMesProgrammes()
            
            android.util.Log.d("MesProgrammesActivity", "🔄 Appel ViewModel.loadStatistiques()")
            viewModel.loadStatistiques()
            
            // Charger les favoris pour le filtre
            android.util.Log.d("MesProgrammesActivity", "🔄 Chargement des favoris pour le filtre")
            favoriViewModel.loadFavorisProgrammes()
            
            android.util.Log.d("MesProgrammesActivity", "✅ Tous les appels lancés - En attente des réponses...")
        } catch (e: Exception) {
            android.util.Log.e("MesProgrammesActivity", "💥 Erreur lors du lancement du chargement", e)
            
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "⚠️ Erreur de connexion\n\nVérifiez votre connexion internet"
            binding.rvMesProgrammes.visibility = View.GONE
        }
    }
    
    // ===== GESTION DU FILTRE FAVORIS =====
    
    private fun toggleFavoritesFilter() {
        isFilteringFavorites = !isFilteringFavorites
        
        android.util.Log.d("MesProgrammesActivity", "💖 Toggle filtre favoris: $isFilteringFavorites")
        
        // Mettre à jour l'icône du bouton
        updateFilterButton()
        
        // Charger les favoris si nécessaire
        if (isFilteringFavorites && favoritesProgrammeIds.isEmpty()) {
            favoriViewModel.loadFavorisProgrammes()
        }
        
        // Appliquer le filtre
        val programmesToShow = if (isFilteringFavorites) {
            filterFavoritesProgrammes(allProgrammes)
        } else {
            allProgrammes
        }
        
        mesProgrammesAdapter.submitList(programmesToShow)
        updateEmptyState(programmesToShow.isEmpty())
        
        // Message utilisateur
        val message = if (isFilteringFavorites) {
            "Affichage des programmes favoris uniquement"
        } else {
            "Affichage de tous les programmes"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    private fun updateFilterButton() {
        val iconRes = if (isFilteringFavorites) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_outline
        }
        binding.btnFilterFavorites.setImageResource(iconRes)
    }
    
    private fun filterFavoritesProgrammes(programmes: List<UserProgramme>): List<UserProgramme> {
        return programmes.filter { userProgramme ->
            favoritesProgrammeIds.contains(userProgramme.programme.id.toLong())
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty && !viewModel.isLoading.value!!) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = if (isFilteringFavorites) {
                "Aucun programme favori.\n\n💡 Ajoutez des programmes à vos favoris en cliquant sur le cœur!"
            } else {
                "Aucun programme trouvé.\n\n💡 Allez dans 'Programmes' pour vous inscrire à un programme!"
            }
            binding.rvMesProgrammes.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvMesProgrammes.visibility = View.VISIBLE
        }
    }
    
    // 🧪 MÉTHODE DE TEST: Vérifier tous les programmes disponibles
    private fun testAllProgrammes() {
        android.util.Log.d("MesProgrammesActivity", "🧪 === TEST DIAGNOSTIC COMPLET ===")
        
        // Test 1: Vérifier l'authentification
        val preferencesManager = PreferencesManager(this)
        android.util.Log.d("MesProgrammesActivity", "🧪 TEST 1 - Authentification:")
        android.util.Log.d("MesProgrammesActivity", "  - Token valide: ${preferencesManager.getToken() != null}")
        android.util.Log.d("MesProgrammesActivity", "  - Utilisateur ID: ${preferencesManager.getUserId()}")
        android.util.Log.d("MesProgrammesActivity", "  - Email: ${preferencesManager.getUserEmail()}")
        
        // Test 2: Vérifier l'état des filtres
        android.util.Log.d("MesProgrammesActivity", "🧪 TEST 2 - État des filtres:")
        android.util.Log.d("MesProgrammesActivity", "  - Filtre favoris actif: $isFilteringFavorites")
        android.util.Log.d("MesProgrammesActivity", "  - Programmes totaux: ${allProgrammes.size}")
        android.util.Log.d("MesProgrammesActivity", "  - Favoris connus: ${favoritesProgrammeIds.size}")
        
        // Test 3: Désactiver le filtre favoris si actif
        if (isFilteringFavorites) {
            android.util.Log.d("MesProgrammesActivity", "🧪 TEST 3 - Désactivation du filtre favoris")
            toggleFavoritesFilter()
            Toast.makeText(this, "Filtre favoris désactivé pour diagnostic", Toast.LENGTH_LONG).show()
        }
        
        // Test 4: Forcer un nouveau chargement avec logs détaillés
        android.util.Log.d("MesProgrammesActivity", "🧪 TEST 4 - Rechargement avec diagnostic:")
        loadMesProgrammes()
        
        Toast.makeText(this, "Diagnostic complet lancé - Voir les logs", Toast.LENGTH_LONG).show()
    }
}
