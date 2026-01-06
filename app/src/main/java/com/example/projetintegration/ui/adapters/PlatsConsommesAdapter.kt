package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ItemPlatConsommeBinding

class PlatsConsommesAdapter : ListAdapter<Plat, PlatsConsommesAdapter.PlatViewHolder>(PlatDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatViewHolder {
        val binding = ItemPlatConsommeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PlatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PlatViewHolder(
        private val binding: ItemPlatConsommeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(plat: Plat) {
            binding.tvNom.text = plat.nom
            binding.tvCalories.text = "${plat.calories} kcal"
            binding.tvCategorie.text = formatCategorie(plat.categorie)
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
