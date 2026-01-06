package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.databinding.ItemIngredientModernBinding

class IngredientsModernAdapter : ListAdapter<String, IngredientsModernAdapter.IngredientViewHolder>(IngredientDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientModernBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class IngredientViewHolder(
        private val binding: ItemIngredientModernBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(ingredient: String) {
            binding.tvIngredientName.text = ingredient
            binding.tvIngredientIcon.text = getIngredientIcon(ingredient)
        }
        
        private fun getIngredientIcon(ingredient: String): String {
            return when {
                ingredient.lowercase().contains("quinoa") -> "🌾"
                ingredient.lowercase().contains("avocat") -> "🥑"
                ingredient.lowercase().contains("pois") -> "🟢"
                ingredient.lowercase().contains("citron") -> "🍋"
                ingredient.lowercase().contains("concombre") -> "🥒"
                ingredient.lowercase().contains("carotte") -> "🥕"
                ingredient.lowercase().contains("chou") -> "🥬"
                ingredient.lowercase().contains("sauce") -> "🥄"
                ingredient.lowercase().contains("tortilla") -> "🌯"
                ingredient.lowercase().contains("thon") -> "🐟"
                ingredient.lowercase().contains("maïs") -> "🌽"
                ingredient.lowercase().contains("salade") -> "🥗"
                ingredient.lowercase().contains("banane") -> "🍌"
                ingredient.lowercase().contains("fraise") -> "🍓"
                ingredient.lowercase().contains("granola") -> "🥣"
                ingredient.lowercase().contains("chia") -> "🌰"
                ingredient.lowercase().contains("poulet") -> "🍗"
                ingredient.lowercase().contains("brocoli") -> "🥦"
                ingredient.lowercase().contains("ail") -> "🧄"
                ingredient.lowercase().contains("paprika") -> "🌶️"
                ingredient.lowercase().contains("épinard") -> "🥬"
                ingredient.lowercase().contains("gingembre") -> "🫚"
                ingredient.lowercase().contains("céleri") -> "🥬"
                ingredient.lowercase().contains("courgette") -> "🥒"
                ingredient.lowercase().contains("saumon") -> "🐟"
                ingredient.lowercase().contains("haricot") -> "🫘"
                ingredient.lowercase().contains("œuf") -> "🥚"
                ingredient.lowercase().contains("tomate") -> "🍅"
                ingredient.lowercase().contains("poivre") -> "🌶️"
                ingredient.lowercase().contains("lait") -> "🥛"
                ingredient.lowercase().contains("avoine") -> "🌾"
                ingredient.lowercase().contains("riz") -> "🍚"
                ingredient.lowercase().contains("tofu") -> "🧈"
                ingredient.lowercase().contains("poivron") -> "🫑"
                ingredient.lowercase().contains("soja") -> "🫘"
                else -> "🥬" // Default vegetable icon
            }
        }
    }
    
    class IngredientDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}