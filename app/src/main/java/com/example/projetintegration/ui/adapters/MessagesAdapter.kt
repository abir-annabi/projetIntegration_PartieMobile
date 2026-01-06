package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.data.model.Message
import com.example.projetintegration.databinding.ItemMessageUserBinding
import com.example.projetintegration.databinding.ItemMessageAssistantBinding

class MessagesAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_ASSISTANT = 2
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).role) {
            "user" -> VIEW_TYPE_USER
            "assistant" -> VIEW_TYPE_ASSISTANT
            else -> VIEW_TYPE_USER
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemMessageUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserMessageViewHolder(binding)
            }
            VIEW_TYPE_ASSISTANT -> {
                val binding = ItemMessageAssistantBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AssistantMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AssistantMessageViewHolder -> holder.bind(message)
        }
    }
    
    class UserMessageViewHolder(
        private val binding: ItemMessageUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: Message) {
            binding.textViewMessage.text = message.contenu
            binding.textViewTime.text = formatTime(message.timestamp)
        }
        
        private fun formatTime(timestamp: String): String {
            // TODO: Formater le timestamp selon vos besoins
            return timestamp.substringAfter("T").substringBefore(".")
        }
    }
    
    class AssistantMessageViewHolder(
        private val binding: ItemMessageAssistantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(message: Message) {
            binding.textViewMessage.text = message.contenu
            binding.textViewTime.text = formatTime(message.timestamp)
        }
        
        private fun formatTime(timestamp: String): String {
            // TODO: Formater le timestamp selon vos besoins
            return timestamp.substringAfter("T").substringBefore(".")
        }
    }
    
    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}