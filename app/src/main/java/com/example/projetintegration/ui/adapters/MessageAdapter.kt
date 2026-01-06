package com.example.projetintegration.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projetintegration.R
import com.example.projetintegration.data.models.CommunityMessageResponse
import com.example.projetintegration.databinding.ItemMessageBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MessageItem(
    val message: CommunityMessageResponse,
    val indentLevel: Int = 0,
    val isReply: Boolean = false
)

class MessageAdapter(
    private val onLikeClick: (CommunityMessageResponse) -> Unit,
    private val onReplyClick: (CommunityMessageResponse) -> Unit,
    private val onMenuClick: (CommunityMessageResponse) -> Unit,
    private val onViewRepliesClick: (CommunityMessageResponse) -> Unit
) : ListAdapter<MessageItem, MessageAdapter.MessageViewHolder>(MessageItemDiffCallback()) {
    
    private val expandedMessages = mutableSetOf<Long>()
    
    fun submitMessages(messages: List<CommunityMessageResponse>) {
        val flattenedItems = mutableListOf<MessageItem>()
        
        messages.forEach { message ->
            // Ajouter le message principal
            flattenedItems.add(MessageItem(message, 0, false))
            
            // Ajouter les réponses si le message est étendu
            if (expandedMessages.contains(message.id) && !message.replies.isNullOrEmpty()) {
                addRepliesRecursively(message.replies, flattenedItems, 1)
            }
        }
        
        submitList(flattenedItems)
    }
    
    private fun addRepliesRecursively(
        replies: List<CommunityMessageResponse>,
        items: MutableList<MessageItem>,
        indentLevel: Int
    ) {
        replies.forEach { reply ->
            items.add(MessageItem(reply, indentLevel, true))
            
            // Ajouter les réponses imbriquées si étendues
            if (expandedMessages.contains(reply.id) && !reply.replies.isNullOrEmpty()) {
                addRepliesRecursively(reply.replies, items, indentLevel + 1)
            }
        }
    }
    
    fun toggleRepliesExpansion(messageId: Long, messages: List<CommunityMessageResponse>) {
        if (expandedMessages.contains(messageId)) {
            expandedMessages.remove(messageId)
        } else {
            expandedMessages.add(messageId)
        }
        submitMessages(messages)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(messageItem: MessageItem) {
            val message = messageItem.message
            
            try {
                // Appliquer l'indentation pour les réponses
                val indentationPx = (messageItem.indentLevel * 32 * binding.root.context.resources.displayMetrics.density).toInt()
                binding.root.setPadding(
                    16 + indentationPx, 
                    binding.root.paddingTop,
                    binding.root.paddingRight,
                    binding.root.paddingBottom
                )
                
                // Style différent pour les réponses
                if (messageItem.isReply) {
                    binding.messageContainer.setBackgroundResource(R.drawable.bg_reply_message)
                    binding.textAuteur.setTextColor(binding.root.context.getColor(R.color.organic_primary))
                } else {
                    binding.messageContainer.setBackgroundResource(android.R.color.transparent)
                    binding.textAuteur.setTextColor(binding.root.context.getColor(R.color.text_secondary))
                }
                
                // Nom de l'auteur avec style moderne
                binding.textAuteur.text = if (message.anonyme) {
                    "👤 ${message.nomAnonyme ?: "Anonyme"}"
                } else {
                    val fullName = "${message.prenomAuteur ?: ""} ${message.nomAuteur ?: ""}".trim()
                    if (fullName.isNotEmpty()) fullName else "Utilisateur"
                }
                
                // Contenu du message
                binding.textContenu.text = if (message.isDeleted) {
                    "[Message supprimé]"
                } else {
                    message.contenu
                }
                
                // Date au format chat (ex: 9:25 AM)
                binding.textDate.text = formatDateChat(message.dateCreation)
                
                // Statistiques
                binding.textLikes.text = if (message.likesCount > 0) message.likesCount.toString() else ""
                binding.textReplies.text = if (message.repliesCount > 0) message.repliesCount.toString() else ""
                
                // État du like
                binding.buttonLike.setImageResource(
                    if (message.likedByCurrentUser) {
                        R.drawable.ic_heart_filled
                    } else {
                        R.drawable.ic_heart_outline
                    }
                )
                
                // Indicateur de modification
                binding.textModified.visibility = if (message.dateModification != null) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // Menu (visible seulement pour ses propres messages)
                binding.buttonMenu.visibility = if (message.isOwner && !message.isDeleted) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // Bouton voir réponses (visible si il y a des réponses et ce n'est pas déjà une réponse)
                if (!messageItem.isReply && message.repliesCount > 0) {
                    binding.buttonViewReplies.visibility = View.VISIBLE
                    val isExpanded = expandedMessages.contains(message.id)
                    binding.buttonViewReplies.text = if (isExpanded) {
                        "Masquer réponses (${message.repliesCount})"
                    } else {
                        "Voir réponses (${message.repliesCount})"
                    }
                } else {
                    binding.buttonViewReplies.visibility = View.GONE
                }
                
                // Masquer les compteurs si zéro
                binding.textLikes.visibility = if (message.likesCount > 0) View.VISIBLE else View.GONE
                binding.textReplies.visibility = if (message.repliesCount > 0 && !messageItem.isReply) View.VISIBLE else View.GONE
                
                // Désactiver les interactions si le message est supprimé
                val isInteractionEnabled = !message.isDeleted
                binding.buttonLike.isEnabled = isInteractionEnabled
                binding.buttonReply.isEnabled = isInteractionEnabled
                
                // Listeners
                binding.buttonLike.setOnClickListener { 
                    if (isInteractionEnabled) onLikeClick(message) 
                }
                binding.buttonReply.setOnClickListener { 
                    if (isInteractionEnabled) onReplyClick(message) 
                }
                binding.buttonMenu.setOnClickListener { onMenuClick(message) }
                binding.buttonViewReplies.setOnClickListener { onViewRepliesClick(message) }
                
                // Style différent pour les messages supprimés
                binding.root.alpha = if (message.isDeleted) 0.6f else 1.0f
                
            } catch (e: Exception) {
                android.util.Log.e("MessageAdapter", "Erreur lors du bind du message ${message.id}", e)
                
                // Fallback sécurisé
                binding.textAuteur.text = "Erreur"
                binding.textContenu.text = "Erreur lors du chargement"
                binding.textDate.text = ""
                binding.textLikes.text = ""
                binding.textReplies.text = ""
                binding.buttonMenu.visibility = View.GONE
                binding.textModified.visibility = View.GONE
                binding.buttonViewReplies.visibility = View.GONE
            }
        }
        
        private fun formatDateChat(dateString: String): String {
            return try {
                val date = LocalDateTime.parse(dateString)
                val now = LocalDateTime.now()
                
                // Si c'est aujourd'hui, afficher seulement l'heure
                if (date.toLocalDate() == now.toLocalDate()) {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    date.format(formatter)
                } else {
                    // Sinon afficher la date courte
                    val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.getDefault())
                    date.format(formatter)
                }
            } catch (e: Exception) {
                android.util.Log.w("MessageAdapter", "Erreur formatage date: $dateString", e)
                // Fallback simple
                try {
                    dateString.substring(11, 16) // Extraire HH:mm
                } catch (ex: Exception) {
                    "00:00"
                }
            }
        }
    }
    
    class MessageItemDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem.message.id == newItem.message.id && oldItem.indentLevel == newItem.indentLevel
        }
        
        override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }
    }
}