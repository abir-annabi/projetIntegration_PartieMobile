package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.FavoriProgrammeResponse
import com.example.projetintegration.databinding.ItemFavoriProgrammeBinding
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import kotlinx.coroutines.launch

class FavorisProgrammesAdapter(
    private val favoriViewModel: FavoriViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onProgrammeClick: (FavoriProgrammeResponse) -> Unit
) : ListAdapter<FavoriProgrammeResponse, FavorisProgrammesAdapter.FavoriProgrammeViewHolder>(FavoriProgrammeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriProgrammeViewHolder {
        val binding = ItemFavoriProgrammeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriProgrammeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FavoriProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FavoriProgrammeViewHolder(
        private val binding: ItemFavoriProgrammeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(favoriProgramme: FavoriProgrammeResponse) {
            binding.tvNom.text = favoriProgramme.nom
            binding.tvDescription.text = favoriProgramme.description
            binding.tvDuree.text = "📅 ${favoriProgramme.dureeJours} jours"
            binding.tvObjectif.text = formatObjectif(favoriProgramme.objectif)
            binding.tvDateAjout.text = "Ajouté le ${formatDate(favoriProgramme.dateAjout)}"
            
            // Bouton favori (toujours rempli car c'est la liste des favoris)
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
            
            // Click listener pour retirer des favoris
            binding.btnFavorite.setOnClickListener {
                android.util.Log.d("FavorisProgrammesAdapter", "💖 Retirer des favoris: ${favoriProgramme.nom}")
                favoriViewModel.toggleFavoriProgramme(favoriProgramme.programmeId)
            }
            
            // Click listener pour ouvrir les détails
            binding.root.setOnClickListener {
                onProgrammeClick(favoriProgramme)
            }
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
        
        private fun formatDate(dateString: String): String {
            return try {
                // Supposons que la date est au format ISO (2024-01-15T10:30:00)
                val parts = dateString.split("T")[0].split("-")
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } catch (e: Exception) {
                dateString
            }
        }
    }
    
    class FavoriProgrammeDiffCallback : DiffUtil.ItemCallback<FavoriProgrammeResponse>() {
        override fun areItemsTheSame(oldItem: FavoriProgrammeResponse, newItem: FavoriProgrammeResponse): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: FavoriProgrammeResponse, newItem: FavoriProgrammeResponse): Boolean {
            return oldItem == newItem
        }
    }
}