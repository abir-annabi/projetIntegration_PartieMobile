package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Plat
import com.example.projetintegration.databinding.ItemPlatBinding

class PlatsAdapter(
    private val onPlatClick: (Plat) -> Unit
) : ListAdapter<Plat, PlatsAdapter.PlatViewHolder>(PlatDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatViewHolder {
        val binding = ItemPlatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PlatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class PlatViewHolder(
        private val binding: ItemPlatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(plat: Plat) {
            binding.tvNom.text = plat.nom
            binding.tvCalories.text = "${plat.calories} cal"
            binding.tvTemps.text = "⏱️ ${plat.tempsPreparation} min"
            
            // Définir l'image selon la catégorie
            val imageRes = getCategorieImage(plat.categorie)
            binding.ivPlatImage.setImageResource(imageRes)
            
            binding.root.setOnClickListener {
                onPlatClick(plat)
            }
        }
        
        private fun getCategorieImage(categorie: String): Int {
            return when (categorie) {
                "petit-dejeuner" -> com.example.projetintegration.R.drawable.omelette
                "dejeuner" -> com.example.projetintegration.R.drawable.plat_meat
                "diner" -> com.example.projetintegration.R.drawable.tacos
                "collation" -> com.example.projetintegration.R.drawable.applesplash
                else -> com.example.projetintegration.R.drawable.vegetables
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
