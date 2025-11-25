package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.UserProgramme
import com.example.projetintegration.databinding.ItemMesProgrammesBinding
import kotlin.math.min

class MesProgrammesAdapter(
    private val onProgressionUpdate: (UserProgramme, Int) -> Unit
) : ListAdapter<UserProgramme, MesProgrammesAdapter.UserProgrammeViewHolder>(UserProgrammeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProgrammeViewHolder {
        val binding = ItemMesProgrammesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserProgrammeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: UserProgrammeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class UserProgrammeViewHolder(
        private val binding: ItemMesProgrammesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(userProgramme: UserProgramme) {
            binding.tvNom.text = userProgramme.programme.nom
            binding.tvDateDebut.text = "Début: ${userProgramme.dateDebut}"
            userProgramme.dateFin?.let {
                binding.tvDateFin.text = "Fin: $it"
            }
            
            binding.tvStatut.text = formatStatut(userProgramme.statut)
            binding.tvStatut.setBackgroundColor(getStatutColor(userProgramme.statut))
            
            binding.progressBar.progress = userProgramme.progression
            binding.tvProgression.text = "${userProgramme.progression}%"
            
            // Boutons de progression
            if (userProgramme.statut == "en-cours") {
                binding.btnPlus10.setOnClickListener {
                    val newProgression = min(userProgramme.progression + 10, 100)
                    onProgressionUpdate(userProgramme, newProgression)
                }
                
                binding.btnPlus25.setOnClickListener {
                    val newProgression = min(userProgramme.progression + 25, 100)
                    onProgressionUpdate(userProgramme, newProgression)
                }
            } else {
                binding.btnPlus10.isEnabled = false
                binding.btnPlus25.isEnabled = false
            }
        }
        
        private fun formatStatut(statut: String): String {
            return when (statut) {
                "en-cours" -> "En cours"
                "termine" -> "Terminé"
                "abandonne" -> "Abandonné"
                else -> statut
            }
        }
        
        private fun getStatutColor(statut: String): Int {
            return when (statut) {
                "en-cours" -> 0xFF2196F3.toInt()
                "termine" -> 0xFF4CAF50.toInt()
                "abandonne" -> 0xFFF44336.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }
    }
    
    class UserProgrammeDiffCallback : DiffUtil.ItemCallback<UserProgramme>() {
        override fun areItemsTheSame(oldItem: UserProgramme, newItem: UserProgramme): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: UserProgramme, newItem: UserProgramme): Boolean {
            return oldItem == newItem
        }
    }
}
