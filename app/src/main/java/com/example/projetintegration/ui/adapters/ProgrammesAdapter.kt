package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.databinding.ItemProgrammeBinding
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import kotlinx.coroutines.launch

class ProgrammesAdapter(
    private val favoriViewModel: FavoriViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onProgrammeClick: (Programme) -> Unit
) : ListAdapter<Programme, ProgrammesAdapter.ProgrammeViewHolder>(ProgrammeDiffCallback()) {
    
    // Images des programmes dans l'ordre
    private val programmeImages = arrayOf(
        R.drawable.programme1,
        R.drawable.programme2,
        R.drawable.programme3,
        R.drawable.programme4,
        R.drawable.programme5,
        R.drawable.programme6,
        R.drawable.programme7
    )
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgrammeViewHolder {
        val binding = ItemProgrammeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProgrammeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
    
    inner class ProgrammeViewHolder(
        private val binding: ItemProgrammeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(programme: Programme, position: Int) {
            // Image du programme (cyclique si plus de 7 programmes)
            val imageIndex = position % programmeImages.size
            binding.ivProgrammeImage.setImageResource(programmeImages[imageIndex])
            
            binding.tvNom.text = programme.nom
            binding.tvDescription.text = programme.description
            binding.tvDuree.text = "📅 ${programme.dureeJours} jours"
            binding.tvObjectif.text = formatObjectif(programme.objectif)
            
            // 🔧 PROTECTION CONTRE NULL - Plats et Activités
            val platsCount = programme.plats?.size ?: 0
            val activitesCount = programme.activites?.size ?: 0
            
            // Affichage moderne des compteurs
            binding.tvPlatsCount.text = platsCount.toString()
            binding.tvActivitesCount.text = activitesCount.toString()
            
            // 💖 GESTION DES FAVORIS
            setupFavoriteButton(programme)
            
            // Click listeners
            binding.root.setOnClickListener {
                onProgrammeClick(programme)
            }
            
            binding.btnCommencer.setOnClickListener {
                onProgrammeClick(programme)
            }
        }
        
        private fun setupFavoriteButton(programme: Programme) {
            android.util.Log.d("ProgrammesAdapter", "🔧 Setup favori pour programme: ${programme.nom} (ID: ${programme.id})")
            
            // Vérifier le statut favori et mettre à jour l'icône
            lifecycleOwner.lifecycleScope.launch {
                try {
                    android.util.Log.d("ProgrammesAdapter", "🔍 Vérification statut favori programme ID: ${programme.id}")
                    val isFavorite = favoriViewModel.isProgrammeFavorite(programme.id.toLong())
                    android.util.Log.d("ProgrammesAdapter", "✅ Statut favori reçu: $isFavorite")
                    updateFavoriteIcon(isFavorite)
                } catch (e: Exception) {
                    android.util.Log.e("ProgrammesAdapter", "❌ Erreur vérification statut favori", e)
                    updateFavoriteIcon(false)
                }
            }
            
            // Observer les changements de favoris pour ce programme
            favoriViewModel.favorisProgrammes.observe(lifecycleOwner) { favoris ->
                val isFavorite = favoris.any { it.programmeId == programme.id.toLong() }
                updateFavoriteIcon(isFavorite)
            }
            
            // Click listener pour toggle favori
            binding.btnFavorite.setOnClickListener {
                android.util.Log.d("ProgrammesAdapter", "💖 CLICK Toggle favori programme: ${programme.nom}")
                android.util.Log.d("ProgrammesAdapter", "   - Programme ID: ${programme.id}")
                android.util.Log.d("ProgrammesAdapter", "   - Appel ViewModel.toggleFavoriProgramme(${programme.id.toLong()})")
                
                try {
                    favoriViewModel.toggleFavoriProgramme(programme.id.toLong())
                    android.util.Log.d("ProgrammesAdapter", "✅ Appel ViewModel réussi")
                } catch (e: Exception) {
                    android.util.Log.e("ProgrammesAdapter", "❌ Erreur lors de l'appel ViewModel", e)
                }
            }
        }
        
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            android.util.Log.d("ProgrammesAdapter", "🎨 Mise à jour icône favori: $isFavorite")
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
        }
        
        private fun formatObjectif(objectif: String): String {
            return when (objectif) {
                "perte-poids" -> "🎯 Perte de poids"
                "prise-masse" -> "💪 Prise de masse"
                "maintien" -> "⚖️ Maintien"
                "endurance" -> "🏃 Endurance"
                "force" -> "💪 Force"
                "cardio" -> "❤️ Cardio"
                "hiit" -> "⚡ HIIT"
                else -> "🎯 $objectif"
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
