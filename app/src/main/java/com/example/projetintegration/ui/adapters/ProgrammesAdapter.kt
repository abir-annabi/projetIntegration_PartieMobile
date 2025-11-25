package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Programme
import com.example.projetintegration.databinding.ItemProgrammeBinding

class ProgrammesAdapter(
    private val onProgrammeClick: (Programme) -> Unit
) : ListAdapter<Programme, ProgrammesAdapter.ProgrammeViewHolder>(ProgrammeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgrammeViewHolder {
        val binding = ItemProgrammeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProgrammeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ProgrammeViewHolder(
        private val binding: ItemProgrammeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(programme: Programme) {
            binding.tvNom.text = programme.nom
            binding.tvDescription.text = programme.description
            binding.tvDuree.text = "${programme.dureeJours} jours"
            binding.tvObjectif.text = formatObjectif(programme.objectif)
            binding.tvPlatsCount.text = "${programme.plats.size} plats"
            binding.tvActivitesCount.text = "${programme.activites.size} activités"
            
            binding.root.setOnClickListener {
                onProgrammeClick(programme)
            }
        }
        
        private fun formatObjectif(objectif: String): String {
            return when (objectif) {
                "perte-poids" -> "🎯 Perte de poids"
                "prise-masse" -> "💪 Prise de masse"
                "maintien" -> "⚖️ Maintien"
                "endurance" -> "🏃 Endurance"
                else -> objectif
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
