package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.ActiviteSportive
import com.example.projetintegration.databinding.ItemActiviteRealiseeBinding

class ActivitesRealisesAdapter : ListAdapter<ActiviteSportive, ActivitesRealisesAdapter.ActiviteViewHolder>(ActiviteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiviteViewHolder {
        val binding = ItemActiviteRealiseeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActiviteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ActiviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ActiviteViewHolder(
        private val binding: ItemActiviteRealiseeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(activite: ActiviteSportive) {
            binding.tvNom.text = activite.nom
            binding.tvDuree.text = "⏱️ ${activite.duree} min"
            binding.tvCaloriesBrulees.text = "-${activite.caloriesBrulees} kcal"
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
