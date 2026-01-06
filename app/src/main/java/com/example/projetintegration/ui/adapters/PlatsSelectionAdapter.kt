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
