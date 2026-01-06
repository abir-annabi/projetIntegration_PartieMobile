package com.example.projetintegration.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.databinding.ActivityChatbotBinding
import com.example.projetintegration.ui.adapters.MessagesAdapter
import com.example.projetintegration.ui.viewmodel.ChatBotViewModel

class ChatBotActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatbotBinding
    private val viewModel: ChatBotViewModel by viewModels()
    private lateinit var messagesAdapter: MessagesAdapter
    private var conversationId: Long? = null
    private var conversationTitle: String = "Nouvelle conversation"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Récupérer les données de l'intent
        conversationId = intent.getLongExtra("conversationId", -1L).takeIf { it != -1L }
        conversationTitle = intent.getStringExtra("conversationTitle") ?: "Nouvelle conversation"
        
        setupUI()
        setupObservers()
        
        // Charger la conversation existante si nécessaire
        conversationId?.let { id ->
            viewModel.loadConversation(id)
        }
    }
    
    private fun setupUI() {
        // Configuration de la toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = conversationTitle
        
        // Configuration du RecyclerView
        messagesAdapter = MessagesAdapter()
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatBotActivity)
            adapter = messagesAdapter
        }
        
        // Configuration du bouton d'envoi
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.editTextMessage.text.clear()
            }
        }
        
        // Suggestions de démarrage pour nouvelle conversation
        if (conversationId == null) {
            showStarterSuggestions()
        }
    }
    
    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            messagesAdapter.submitList(messages)
            // Scroll vers le dernier message
            if (messages.isNotEmpty()) {
                binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.buttonSend.isEnabled = !isLoading
            if (isLoading) {
                binding.textViewTyping.visibility = android.view.View.VISIBLE
                binding.textViewTyping.text = "L'assistant écrit..."
            } else {
                binding.textViewTyping.visibility = android.view.View.GONE
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.newConversationId.observe(this) { newId ->
            newId?.let {
                conversationId = it
                // Mettre à jour le titre si c'était une nouvelle conversation
                if (conversationTitle == "Nouvelle conversation") {
                    supportActionBar?.title = "💬 Conversation"
                }
            }
        }
    }
    
    private fun sendMessage(message: String) {
        viewModel.sendMessage(conversationId, message)
    }
    
    private fun showStarterSuggestions() {
        // Afficher des suggestions pour commencer la conversation
        val suggestions = listOf(
            "Bonjour, j'ai besoin de conseils nutritionnels",
            "Peux-tu me créer un programme d'exercices ?",
            "Comment améliorer mon alimentation ?",
            "Quels exercices pour perdre du poids ?"
        )
        
        // TODO: Implémenter l'affichage des suggestions
        // Pour l'instant, on peut les afficher dans un TextView ou des boutons
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}