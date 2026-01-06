package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.ActiviteSportive
import com.example.projetintegration.databinding.ItemActiviteSelectionBinding

class ActivitesSelectionAdapter(
    private val onActiviteChecked: (ActiviteSportive, Boolean) -> Unit
) : ListAdapter<ActiviteSportive, ActivitesSelectionAdapter.ActiviteViewHolder>(ActiviteDiffCallback()) {
    
    private val activitesRealisesIds = mutableSetOf<Int>()
    private val selectedActiviteIds = mutableSetOf<Int>()
    
    fun setActivitesRealisees(ids: List<Int>) {
        activitesRealisesIds.clear()
        activitesRealisesIds.addAll(ids)
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(ids)
        notifyDataSetChanged()
    }
    
    // ✅ NOUVELLE MÉTHODE: Récupérer les IDs sélectionnés
    fun getSelectedActiviteIds(): List<Int> {
        return selectedActiviteIds.toList()
    }
    
    // ✅ NOUVELLES MÉTHODES: Sélection rapide
    fun selectAll() {
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(currentList.map { it.id })
        notifyDataSetChanged()
    }
    
    fun deselectAll() {
        selectedActiviteIds.clear()
        notifyDataSetChanged()
    }
    
    fun selectByType(type: String) {
        // Comme il n'y a pas de propriété 'type' dans le modèle, 
        // on peut sélectionner par niveau ou par nom contenant le type
        selectedActiviteIds.clear()
        selectedActiviteIds.addAll(
            currentList.filter { 
                it.nom.contains(type, ignoreCase = true) ||
                it.description.contains(type, ignoreCase = true)
            }.map { it.id }
        )
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiviteViewHolder {
        val binding = ItemActiviteSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActiviteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ActiviteViewHolder, position: Int) {
        holder.bind(getItem(position), activitesRealisesIds.contains(getItem(position).id))
    }
    
    inner class ActiviteViewHolder(
        private val binding: ItemActiviteSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(activite: ActiviteSportive, isRealisee: Boolean) {
            binding.tvNom.text = activite.nom
            binding.tvDuree.text = "⏱️ ${activite.duree} min"
            binding.tvCaloriesBrulees.text = "-${activite.caloriesBrulees} kcal"
            binding.tvNiveau.text = formatNiveau(activite.niveau)
            
            // Désactiver le listener temporairement
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = isRealisee
            
            // ✅ AMÉLIORATION: Feedback visuel selon l'état
            updateVisualState(isRealisee)
            
            // Réactiver le listener
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                // ✅ AMÉLIORATION: Animation lors du changement
                animateStateChange(isChecked)
                
                if (isChecked) {
                    selectedActiviteIds.add(activite.id)
                    onActiviteChecked(activite, true)
                } else {
                    selectedActiviteIds.remove(activite.id)
                    onActiviteChecked(activite, false)
                }
            }
            
            binding.root.setOnClickListener {
                binding.checkbox.isChecked = !binding.checkbox.isChecked
            }
        }
        
        // ✅ NOUVELLE MÉTHODE: Mise à jour visuelle selon l'état
        private fun updateVisualState(isRealisee: Boolean) {
            if (isRealisee) {
                // État "réalisé" - style success
                binding.root.alpha = 1.0f
                binding.root.setCardBackgroundColor(
                    binding.root.context.getColor(android.R.color.holo_blue_light).let { color ->
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
        
        private fun formatNiveau(niveau: String): String {
            return when (niveau.lowercase()) {
                "debutant" -> "🟢 Débutant"
                "intermediaire", "intermédiaire" -> "🟡 Intermédiaire"
                "avance", "avancé" -> "🔴 Avancé"
                else -> niveau
            }
        }
    }
    
    class ActiviteDiffCallback : DiffUtil.ItemCallback<ActiviteSportive>() {
        override fun areItemsTheSame(oldItem: ActiviteSportive, newItem: ActiviteSportive): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ActiviteSportive, newItem: ActiviteSportive): Boolean {
            return oldItem == newItem
        }
    }
}
