package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.model.Conversation
import com.example.projetintegration.databinding.ItemConversationBinding

class ConversationsAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationsAdapter.ConversationViewHolder>(ConversationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(conversation: Conversation) {
            binding.textViewTitle.text = conversation.titre
            binding.textViewLastActivity.text = conversation.derniereActivite
            
            // Icône selon le type de conversation
            binding.imageViewIcon.text = when {
                conversation.titre.contains("nutrition", ignoreCase = true) -> "🥗"
                conversation.titre.contains("exercice", ignoreCase = true) -> "💪"
                conversation.titre.contains("programme", ignoreCase = true) -> "🏃"
                else -> "💬"
            }
            
            binding.root.setOnClickListener {
                onConversationClick(conversation)
            }
        }
    }
    
    private class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}