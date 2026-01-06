package com.example.projetintegration.ui.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.databinding.ItemMesProgrammesBinding
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import kotlinx.coroutines.launch
import kotlin.math.min

class MesProgrammesAdapter(
    private val favoriViewModel: FavoriViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onDeleteProgramme: (UserProgramme) -> Unit
) : ListAdapter<UserProgramme, MesProgrammesAdapter.UserProgrammeViewHolder>(UserProgrammeDiffCallback()) {
    
    // ✅ CORRECTION: Stocker les statistiques pour chaque programme
    private var statistiquesMap: Map<Int, com.example.projetintegration.data.models.Statistiques> = emptyMap()
    
    fun updateStatistiques(statistiques: com.example.projetintegration.data.models.Statistiques?) {
        // Pour l'instant, on applique les mêmes statistiques à tous les programmes
        // TODO: Le backend devrait retourner les statistiques par programme
        if (statistiques != null) {
            val currentList = currentList
            if (currentList.isNotEmpty()) {
                // Associer les statistiques au programme actuel (premier de la liste)
                statistiquesMap = mapOf(currentList[0].id to statistiques)
                notifyDataSetChanged()
            }
        }
    }
    
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
            try {
                // 🔧 PROTECTION CONTRE NULL - Données de base
                binding.tvNom.text = userProgramme.programme.nom ?: "Programme sans nom"
                binding.tvDateDebut.text = "Début: ${userProgramme.dateDebut ?: "Date inconnue"}"
                
                userProgramme.dateFin?.let {
                    binding.tvDateFin.text = "Fin: $it"
                } ?: run {
                    binding.tvDateFin.text = ""
                }
                
                binding.tvStatut.text = formatStatut(userProgramme.statut)
                binding.tvStatut.setBackgroundColor(getStatutColor(userProgramme.statut))
                
                // 🔧 PROTECTION CONTRE NULL - Le backend peut retourner null
                val nbPlats = userProgramme.programme.plats?.size ?: 0
                val nbActivites = userProgramme.programme.activites?.size ?: 0
                
                android.util.Log.d("MesProgrammesAdapter", "Programme: ${userProgramme.programme.nom}")
                android.util.Log.d("MesProgrammesAdapter", "  - Plats: $nbPlats")
                android.util.Log.d("MesProgrammesAdapter", "  - Activités: $nbActivites")
                
                if (nbPlats == 0 && nbActivites == 0) {
                    binding.tvProgression.text = "⚠️ Programme sans contenu"
                    android.util.Log.w("MesProgrammesAdapter", "⚠️ Programme ${userProgramme.programme.nom} sans contenu - Backend doit être corrigé")
                } else {
                    binding.tvProgression.text = "📋 $nbPlats plats • 💪 $nbActivites activités"
                }
                
                // ✅ NOUVELLE LOGIQUE SIMPLE: Utiliser uniquement la progression backend
                val statistiques = statistiquesMap[userProgramme.id]
                val progressionBackend = statistiques?.progressionGlobale
                
                android.util.Log.d("MesProgrammesAdapter", "=== DEBUG PROGRESSION ===")
                android.util.Log.d("MesProgrammesAdapter", "Programme: ${userProgramme.programme.nom}")
                android.util.Log.d("MesProgrammesAdapter", "UserProgramme ID: ${userProgramme.id}")
                android.util.Log.d("MesProgrammesAdapter", "Statistiques disponibles: ${statistiques != null}")
                android.util.Log.d("MesProgrammesAdapter", "Progression backend: $progressionBackend%")
                
                if (statistiques != null) {
                    android.util.Log.d("MesProgrammesAdapter", "Détails statistiques:")
                    android.util.Log.d("MesProgrammesAdapter", "  - progressionGlobale: ${statistiques.progressionGlobale}%")
                    android.util.Log.d("MesProgrammesAdapter", "  - tauxCompletion: ${statistiques.tauxCompletion}%")
                    android.util.Log.d("MesProgrammesAdapter", "  - tauxRepas: ${statistiques.tauxRepas}%")
                    android.util.Log.d("MesProgrammesAdapter", "  - tauxActivites: ${statistiques.tauxActivites}%")
                    android.util.Log.d("MesProgrammesAdapter", "  - jourActuel: ${statistiques.jourActuel}/${statistiques.joursTotal}")
                    android.util.Log.d("MesProgrammesAdapter", "  - totalPlatsConsommes: ${statistiques.totalPlatsConsommes}")
                    android.util.Log.d("MesProgrammesAdapter", "  - totalActivitesRealisees: ${statistiques.totalActivitesRealisees}")
                }
                android.util.Log.d("MesProgrammesAdapter", "========================")
                
                if (progressionBackend != null) {
                    // ✅ Utiliser la progression backend (nouvelle logique simple)
                    android.util.Log.d("MesProgrammesAdapter", "✅ Affichage progression backend: ${progressionBackend}%")
                    binding.progressBar.progress = progressionBackend
                    binding.tvProgression.text = "${progressionBackend}% • Formule: Éléments terminés/attendus"
                    
                    // 🚨 VÉRIFICATION: La progression semble-t-elle correcte ?
                    if (progressionBackend > 100) {
                        android.util.Log.e("MesProgrammesAdapter", "🚨 ERREUR: Progression > 100% ! Vérifier le backend")
                        binding.tvProgression.text = "❌ Erreur calcul: ${progressionBackend}%"
                    } else if (progressionBackend < 0) {
                        android.util.Log.e("MesProgrammesAdapter", "🚨 ERREUR: Progression négative ! Vérifier le backend")
                        binding.tvProgression.text = "❌ Erreur calcul: ${progressionBackend}%"
                    }
                } else {
                    // ⚠️ Backend non disponible - Affichage minimal
                    android.util.Log.w("MesProgrammesAdapter", "⚠️ Backend non disponible - Progression indisponible")
                    binding.progressBar.progress = 0
                    binding.tvProgression.text = "📋 $nbPlats plats • 💪 $nbActivites activités • Progression: En attente..."
                }
                
                // 💖 GESTION DES FAVORIS
                setupFavoriteButton(userProgramme)
                
                // 🗑️ GESTION DE LA SUPPRESSION
                setupDeleteButton(userProgramme)
                
                try {
                    userProgramme.poidsDebut?.let { debut ->
                        userProgramme.poidsObjectif?.let { objectif ->
                            userProgramme.poidsActuel?.let { actuel ->
                                val evolution = debut - actuel
                                val signe = if (evolution > 0) "-" else "+"
                                binding.tvPoids.text = "Poids: ${actuel}kg (${signe}${kotlin.math.abs(evolution)}kg)"
                            } ?: run {
                                binding.tvPoids.text = "Poids: ${debut}kg (début)"
                            }
                        } ?: run {
                            binding.tvPoids.text = ""
                        }
                    } ?: run {
                        binding.tvPoids.text = ""
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MesProgrammesAdapter", "Erreur calcul poids", e)
                    binding.tvPoids.text = ""
                }
                
                // Click listener pour ouvrir les détails
                binding.root.setOnClickListener {
                    try {
                        if (nbPlats == 0 && nbActivites == 0) {
                            android.widget.Toast.makeText(context, "⚠️ Programme sans contenu - Contactez le support", android.widget.Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        
                        val intent = android.content.Intent(context, com.example.projetintegration.ui.activities.MonProgrammeDetailActivity::class.java)
                        intent.putExtra("USER_PROGRAMME_ID", userProgramme.id)
                        android.util.Log.d("MesProgrammesAdapter", "🔄 Ouverture programme ID: ${userProgramme.id}")
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.util.Log.e("MesProgrammesAdapter", "Erreur ouverture programme", e)
                        android.widget.Toast.makeText(context, "Erreur lors de l'ouverture du programme", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                
            } catch (e: Exception) {
                android.util.Log.e("MesProgrammesAdapter", "💥 Erreur dans bind() pour programme ${userProgramme.id}", e)
                
                // Fallback sécurisé
                binding.tvNom.text = "Erreur de chargement"
                binding.tvProgression.text = "⚠️ Erreur"
                binding.tvStatut.text = "Erreur"
                binding.tvDateDebut.text = ""
                binding.tvDateFin.text = ""
                binding.tvPoids.text = ""
                binding.progressBar.progress = 0
            }
        }
        
        private fun setupFavoriteButton(userProgramme: UserProgramme) {
            android.util.Log.d("MesProgrammesAdapter", "🔧 Setup favori pour programme: ${userProgramme.programme.nom} (ID: ${userProgramme.programme.id})")
            
            // Vérifier le statut favori et mettre à jour l'icône
            lifecycleOwner.lifecycleScope.launch {
                try {
                    android.util.Log.d("MesProgrammesAdapter", "🔍 Vérification statut favori programme ID: ${userProgramme.programme.id}")
                    val isFavorite = favoriViewModel.isProgrammeFavorite(userProgramme.programme.id.toLong())
                    android.util.Log.d("MesProgrammesAdapter", "✅ Statut favori reçu: $isFavorite")
                    updateFavoriteIcon(isFavorite)
                } catch (e: Exception) {
                    android.util.Log.e("MesProgrammesAdapter", "❌ Erreur vérification statut favori", e)
                    updateFavoriteIcon(false)
                }
            }
            
            // Observer les changements de favoris pour ce programme
            favoriViewModel.favorisProgrammes.observe(lifecycleOwner) { favoris ->
                val isFavorite = favoris.any { it.programmeId == userProgramme.programme.id.toLong() }
                updateFavoriteIcon(isFavorite)
            }
            
            // Click listener pour toggle favori
            binding.btnFavorite.setOnClickListener {
                android.util.Log.d("MesProgrammesAdapter", "💖 CLICK Toggle favori programme: ${userProgramme.programme.nom}")
                android.util.Log.d("MesProgrammesAdapter", "   - Programme ID: ${userProgramme.programme.id}")
                android.util.Log.d("MesProgrammesAdapter", "   - Appel ViewModel.toggleFavoriProgramme(${userProgramme.programme.id.toLong()})")
                
                try {
                    favoriViewModel.toggleFavoriProgramme(userProgramme.programme.id.toLong())
                    android.util.Log.d("MesProgrammesAdapter", "✅ Appel ViewModel réussi")
                } catch (e: Exception) {
                    android.util.Log.e("MesProgrammesAdapter", "❌ Erreur lors de l'appel ViewModel", e)
                }
            }
        }
        
        private fun setupDeleteButton(userProgramme: UserProgramme) {
            binding.btnDelete.setOnClickListener {
                // Afficher une boîte de dialogue de confirmation
                AlertDialog.Builder(context)
                    .setTitle("Supprimer le programme")
                    .setMessage("Êtes-vous sûr de vouloir supprimer le programme \"${userProgramme.programme.nom}\" ?\n\nCette action est irréversible.")
                    .setPositiveButton("Supprimer") { _, _ ->
                        android.util.Log.d("MesProgrammesAdapter", "🗑️ Suppression confirmée pour: ${userProgramme.programme.nom}")
                        onDeleteProgramme(userProgramme)
                    }
                    .setNegativeButton("Annuler", null)
                    .setIcon(R.drawable.ic_delete)
                    .show()
            }
        }
        
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            android.util.Log.d("MesProgrammesAdapter", "🎨 Mise à jour icône favori: $isFavorite")
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
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
