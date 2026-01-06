package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.FavoriPlatResponse
import com.example.projetintegration.databinding.ItemFavoriPlatBinding
import com.example.projetintegration.ui.viewmodel.FavoriViewModel
import kotlinx.coroutines.launch

class FavorisPlatsAdapter(
    private val favoriViewModel: FavoriViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onPlatClick: (FavoriPlatResponse) -> Unit
) : ListAdapter<FavoriPlatResponse, FavorisPlatsAdapter.FavoriPlatViewHolder>(FavoriPlatDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriPlatViewHolder {
        val binding = ItemFavoriPlatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriPlatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FavoriPlatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FavoriPlatViewHolder(
        private val binding: ItemFavoriPlatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(favoriPlat: FavoriPlatResponse) {
            binding.tvNom.text = favoriPlat.nom
            binding.tvDescription.text = favoriPlat.description
            binding.tvCalories.text = "${favoriPlat.calories} kcal"
            binding.tvTypePlat.text = formatTypePlat(favoriPlat.typePlat)
            binding.tvDateAjout.text = "Ajouté le ${formatDate(favoriPlat.dateAjout)}"
            
            // Bouton favori (toujours rempli car c'est la liste des favoris)
            binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
            
            // Click listener pour retirer des favoris
            binding.btnFavorite.setOnClickListener {
                android.util.Log.d("FavorisPlatsAdapter", "💖 Retirer des favoris: ${favoriPlat.nom}")
                favoriViewModel.toggleFavoriPlat(favoriPlat.platId)
            }
            
            // Click listener pour ouvrir les détails
            binding.root.setOnClickListener {
                onPlatClick(favoriPlat)
            }
        }
        
        private fun formatTypePlat(typePlat: String): String {
            return when (typePlat.lowercase()) {
                "petit-dejeuner", "petit_dejeuner" -> "🌅 Petit-déjeuner"
                "dejeuner", "déjeuner" -> "☀️ Déjeuner"
                "diner", "dîner" -> "🌙 Dîner"
                "collation" -> "🍎 Collation"
                "dessert" -> "🍰 Dessert"
                "boisson" -> "🥤 Boisson"
                else -> "🍽️ $typePlat"
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
    
    class FavoriPlatDiffCallback : DiffUtil.ItemCallback<FavoriPlatResponse>() {
        override fun areItemsTheSame(oldItem: FavoriPlatResponse, newItem: FavoriPlatResponse): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: FavoriPlatResponse, newItem: FavoriPlatResponse): Boolean {
            return oldItem == newItem
        }
    }
}