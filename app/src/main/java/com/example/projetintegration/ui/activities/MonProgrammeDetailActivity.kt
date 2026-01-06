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
        
        try {
            binding = ActivityMonProgrammeDetailBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            userProgrammeId = intent.getIntExtra("USER_PROGRAMME_ID", 0)
            if (userProgrammeId == 0) {
                android.util.Log.w("MonProgrammeDetail", "⚠️ USER_PROGRAMME_ID manquant ou invalide")
                Toast.makeText(this, "Erreur: Programme non trouvé", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            android.util.Log.d("MonProgrammeDetail", "✅ Initialisation avec USER_PROGRAMME_ID: $userProgrammeId")
            
            viewModel = ViewModelProvider(this)[MonProgrammeDetailViewModel::class.java]
            
            setupRecyclerViews()
            setupObservers()
            setupClickListeners()
            loadData()
            
        } catch (e: Exception) {
            android.util.Log.e("MonProgrammeDetail", "💥 CRASH lors de onCreate", e)
            Toast.makeText(this, "Erreur critique lors de l'initialisation", Toast.LENGTH_LONG).show()
            finish()
        }
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
            
            // Vérifier le statut du programme
            // ✅ DIAGNOSTIC: Afficher les informations du programme
        android.util.Log.d("MonProgrammeDetail", "=== INFORMATIONS PROGRAMME ===")
        android.util.Log.d("MonProgrammeDetail", "Programme: ${userProgramme.programme.nom}")
        android.util.Log.d("MonProgrammeDetail", "Date début: ${userProgramme.dateDebut}")
        android.util.Log.d("MonProgrammeDetail", "Date fin prévue: ${userProgramme.dateFinPrevue}")
        android.util.Log.d("MonProgrammeDetail", "Statut: ${userProgramme.statut}")
        android.util.Log.d("MonProgrammeDetail", "Durée programme: ${userProgramme.programme.dureeJours} jours")
        
        // ✅ BACKEND SIMPLIFIÉ: Le backend accepte maintenant toutes les dates
        // Plus besoin de validation complexe - utiliser simplement la date actuelle
        try {
            val dateDebut = java.time.LocalDate.parse(userProgramme.dateDebut)
            val dateFinPrevue = java.time.LocalDate.parse(userProgramme.dateFinPrevue)
            val dateActuelle = java.time.LocalDate.now()
            
            android.util.Log.d("MonProgrammeDetail", "🔍 Informations programme:")
            android.util.Log.d("MonProgrammeDetail", "   Programme: $dateDebut à $dateFinPrevue")
            android.util.Log.d("MonProgrammeDetail", "   Date actuelle: $dateActuelle")
            
            // Utiliser la date actuelle - le backend accepte toutes les dates
            android.util.Log.i("MonProgrammeDetail", "✅ Utilisation de la date actuelle: $dateActuelle")
            currentDate.set(dateActuelle.year, dateActuelle.monthValue - 1, dateActuelle.dayOfMonth)
            
        } catch (e: Exception) {
            android.util.Log.e("MonProgrammeDetail", "Erreur parsing dates", e)
            // Fallback: utiliser la date actuelle du système
            val today = java.time.LocalDate.now()
            currentDate.set(today.year, today.monthValue - 1, today.dayOfMonth)
        }
        android.util.Log.d("MonProgrammeDetail", "===============================")
        
        when (userProgramme.statut.uppercase()) {
                "EN_COURS" -> {
                    binding.btnEnregistrerJournee.isEnabled = true
                    binding.btnEnregistrerJournee.text = "✅ ENREGISTRER MA JOURNÉE"
                }
                "PAUSE" -> {
                    binding.btnEnregistrerJournee.isEnabled = false
                    binding.btnEnregistrerJournee.text = "⏸️ Programme en pause"
                    Toast.makeText(this, "Programme en pause - Enregistrement désactivé", Toast.LENGTH_LONG).show()
                }
                "TERMINE" -> {
                    binding.btnEnregistrerJournee.isEnabled = false
                    binding.btnEnregistrerJournee.text = "🏁 Programme terminé"
                }
                "ABANDONNE" -> {
                    binding.btnEnregistrerJournee.isEnabled = false
                    binding.btnEnregistrerJournee.text = "❌ Programme abandonné"
                }
                else -> {
                    binding.btnEnregistrerJournee.isEnabled = false
                    binding.btnEnregistrerJournee.text = "❓ Statut inconnu"
                }
            }
            
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
            
            // 🔧 PROTECTION CONTRE NULL - Le backend peut retourner null
            val plats = userProgramme.programme.plats ?: emptyList()
            val activites = userProgramme.programme.activites ?: emptyList()
            
            if (plats.isEmpty() && activites.isEmpty()) {
                android.util.Log.e("MonProgrammeDetail", "⚠️ PROBLÈME BACKEND: Programme sans contenu!")
                Toast.makeText(this, "⚠️ Programme sans contenu - Contactez le support", Toast.LENGTH_LONG).show()
                binding.btnEnregistrerJournee.isEnabled = false
                binding.btnEnregistrerJournee.text = "❌ Programme sans contenu"
            }
            
            // Plats et activités du programme
            platsAdapter.submitList(plats)
            activitesAdapter.submitList(activites)
            
            // ✅ MAINTENANT charger la progression avec la date corrigée
            loadProgressionJour()
        }

        viewModel.progressionJour.observe(this) { progression ->
            try {
                if (progression != null) {
                    // 🔧 PROTECTION CONTRE NULL - Le backend peut retourner null
                    val platsConsommesIds = progression.platsConsommes?.map { it.id } ?: emptyList()
                    platsAdapter.setPlatsConsommes(platsConsommesIds)

                    val activitesRealisesIds = progression.activitesRealisees?.map { it.id } ?: emptyList()
                    activitesAdapter.setActivitesRealisees(activitesRealisesIds)

                    // 🔧 PROTECTION: statutJour peut être null
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
            } catch (e: Exception) {
                android.util.Log.e("MonProgrammeDetail", "Erreur lors de l'affichage de la progression", e)
                
                // Fallback sécurisé
                platsAdapter.setPlatsConsommes(emptyList())
                activitesAdapter.setActivitesRealisees(emptyList())
                binding.tvStatutJour.text = "⚠️ Erreur de chargement"
                binding.tvCalories.text = "Calories: 0 kcal"
                
                Toast.makeText(this, "Erreur lors du chargement de la progression", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.statistiques.observe(this) { stats ->
            stats?.let {
                // ✅ NOUVELLE LOGIQUE SIMPLE: Affichage direct de la progression backend
                binding.progressBar.progress = it.progressionGlobale
                binding.tvProgression.text = "${it.progressionGlobale}%"
                
                android.util.Log.d("MonProgrammeDetail", "=== PROGRESSION SIMPLE ===")
                android.util.Log.d("MonProgrammeDetail", "Progression: ${it.progressionGlobale}%")
                android.util.Log.d("MonProgrammeDetail", "Formule: Éléments terminés/attendus")
                android.util.Log.d("MonProgrammeDetail", "========================")
                
                // Afficher les informations complémentaires si disponibles
                binding.tvTauxCompletion.text = "Jour: ${it.jourActuel}/${it.joursTotal}"
                binding.tvTauxRepas.text = "Plats: ${it.totalPlatsConsommes ?: 0} consommés"
                binding.tvTauxActivites.text = "Activités: ${it.totalActivitesRealisees ?: 0} réalisées"
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
                
                // ✅ CORRECTION: Le rechargement se fait maintenant automatiquement dans le ViewModel
                // Plus besoin de recharger manuellement ici - évite les doublons
                android.util.Log.d("MonProgrammeDetail", "✅ Rechargement automatique en cours dans le ViewModel")
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
        // ✅ loadProgressionJour() est maintenant appelé depuis l'observer userProgramme
        viewModel.loadStatistiques()
    }
    
    private fun loadProgressionJour() {
        // Réinitialiser l'état avant de charger
        platsAdapter.setPlatsConsommes(emptyList())
        activitesAdapter.setActivitesRealisees(emptyList())
        binding.tvStatutJour.text = "⏳ Chargement..."
        binding.tvCalories.text = "Calories: 0 kcal"
        
        // ✅ BACKEND SIMPLIFIÉ: Plus de validation de dates complexe
        // Le backend accepte maintenant toutes les dates sans restriction
        val dateStr = dateFormat.format(currentDate.time)
        binding.tvDate.text = "📅 $dateStr"
        
        android.util.Log.d("MonProgrammeDetail", "🔄 Chargement progression pour date: $dateStr")
        viewModel.loadProgressionJour(dateStr)
    }
    
    private fun showDatePicker() {
        val userProgramme = viewModel.userProgramme.value
        if (userProgramme == null) {
            Toast.makeText(this, "Programme non chargé", Toast.LENGTH_SHORT).show()
            return
        }
        
        // ✅ BACKEND SIMPLIFIÉ: Le backend accepte maintenant toutes les dates
        // Plus de validation complexe - permettre la sélection libre
        android.util.Log.d("MonProgrammeDetail", "📅 Ouverture DatePicker - backend accepte toutes les dates")
        
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedDate = java.time.LocalDate.of(year, month + 1, day)
                
                android.util.Log.d("MonProgrammeDetail", "📅 Date sélectionnée: $selectedDate")
                
                // Vérifier seulement le statut du programme
                if (userProgramme.statut.uppercase() != "EN_COURS") {
                    val message = "Programme ${userProgramme.statut.lowercase()} - Enregistrement impossible"
                    android.util.Log.w("MonProgrammeDetail", "⚠️ $message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    return@DatePickerDialog
                }
                
                android.util.Log.d("MonProgrammeDetail", "✅ Date acceptée: $selectedDate")
                currentDate.set(year, month, day)
                loadProgressionJour()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        
        // ✅ BACKEND SIMPLIFIÉ: Pas de limitation de dates
        // L'utilisateur peut sélectionner n'importe quelle date
        android.util.Log.d("MonProgrammeDetail", "📅 DatePicker configuré sans restrictions de dates")
        
        datePicker.show()
    }
    
    // ✅ NOUVELLE MÉTHODE: Enregistrer TOUTE la journée en UN SEUL appel
    private fun enregistrerJourneeComplete() {
        // Vérifier que le programme est actif
        val userProgramme = viewModel.userProgramme.value
        if (userProgramme == null) {
            Toast.makeText(this, "Programme non chargé", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (userProgramme.statut.uppercase() != "EN_COURS") {
            Toast.makeText(this, "Programme non actif", Toast.LENGTH_SHORT).show()
            return
        }
        
        // ✅ BACKEND SIMPLIFIÉ: Plus de validation de dates complexe
        // Le backend accepte maintenant toutes les dates sans restriction
        android.util.Log.d("MonProgrammeDetail", "✅ Enregistrement autorisé - backend accepte toutes les dates")
        
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
        android.util.Log.d("MonProgrammeDetail", "📤 Enregistrement progression pour date: $dateStr")
        val request = com.example.projetintegration.data.models.EnregistrerProgressionRequest(
            date = dateStr,
            platIds = platIds.ifEmpty { null },
            activiteIds = activiteIds.ifEmpty { null },
            poidsJour = null, // Sera ajouté séparément avec le bouton poids
            notes = null,
            userProgrammeId = null // ✅ NOUVEAU: Sera ajouté par le ViewModel
        )
        
        // 5. Envoyer UN SEUL appel API
        viewModel.enregistrerProgressionComplete(request)
    }
    
    private fun showEnregistrerPoidsDialog() {
        // ✅ VALIDATION: Vérifier que le programme est actif
        val userProgramme = viewModel.userProgramme.value
        if (userProgramme == null) {
            Toast.makeText(this, "Programme non chargé", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (userProgramme.statut.uppercase() != "EN_COURS") {
            Toast.makeText(this, "Programme non actif", Toast.LENGTH_SHORT).show()
            return
        }
        
        // ✅ BACKEND SIMPLIFIÉ: Plus de validation de dates complexe
        // Le backend accepte maintenant toutes les dates sans restriction
        android.util.Log.d("MonProgrammeDetail", "✅ Enregistrement poids autorisé - backend accepte toutes les dates")
        
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
                        android.util.Log.d("MonProgrammeDetail", "📤 Enregistrement poids pour date: $dateStr, poids: $poids")
                        viewModel.enregistrerPoidsSeul(dateStr, poids)
                    } else {
                        Toast.makeText(this, "Poids invalide", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun formatStatutJour(statut: String?): String {
        return when (statut?.uppercase()) {
            "COMPLETE" -> "✅ Journée complète"
            "PARTIEL" -> "⚠️ Journée partielle"
            "NON_FAIT" -> "❌ Aucune activité"
            null -> "❓ Statut non défini"
            else -> "❓ Statut inconnu: $statut"
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
    
    override fun onDestroy() {
        try {
            // Nettoyer les références pour éviter les fuites mémoire
            if (::platsAdapter.isInitialized) {
                binding.rvPlats.adapter = null
            }
            if (::activitesAdapter.isInitialized) {
                binding.rvActivites.adapter = null
            }
            
            // Supprimer les callbacks en attente
            binding.btnEnregistrerJournee.removeCallbacks(null)
            
            android.util.Log.d("MonProgrammeDetail", "✅ Nettoyage des ressources terminé")
        } catch (e: Exception) {
            android.util.Log.e("MonProgrammeDetail", "⚠️ Erreur lors du nettoyage", e)
        }
        
        super.onDestroy()
    }
    
    override fun onPause() {
        super.onPause()
        try {
            // Arrêter les animations en cours pour éviter les crashes
            binding.btnEnregistrerJournee.clearAnimation()
        } catch (e: Exception) {
            android.util.Log.e("MonProgrammeDetail", "⚠️ Erreur lors de onPause", e)
        }
    }
}
