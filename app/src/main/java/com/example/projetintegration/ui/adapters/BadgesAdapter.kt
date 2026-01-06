package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.models.Badge
import com.example.projetintegration.databinding.ItemBadgeBinding

class BadgesAdapter : ListAdapter<Badge, BadgesAdapter.BadgeViewHolder>(BadgeDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BadgeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class BadgeViewHolder(
        private val binding: ItemBadgeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(badge: Badge) {
            binding.tvBadgeIcon.text = badge.icone
            binding.tvBadgeTitre.text = badge.titre
            binding.tvBadgeDescription.text = badge.description
        }
    }
    
    class BadgeDiffCallback : DiffUtil.ItemCallback<Badge>() {
        override fun areItemsTheSame(oldItem: Badge, newItem: Badge): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Badge, newItem: Badge): Boolean {
            return oldItem == newItem
        }
    }
}
