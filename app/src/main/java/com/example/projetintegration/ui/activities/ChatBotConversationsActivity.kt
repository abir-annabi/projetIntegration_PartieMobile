package com.example.projetintegration.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.databinding.ActivityChatbotConversationsBinding
import com.example.projetintegration.ui.adapters.ConversationsAdapter
import com.example.projetintegration.ui.viewmodel.ChatBotViewModel

class ChatBotConversationsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatbotConversationsBinding
    private val viewModel: ChatBotViewModel by viewModels()
    private lateinit var conversationsAdapter: ConversationsAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotConversationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupObservers()
        loadConversations()
    }
    
    private fun setupUI() {
        // Configuration de la toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "💬 Mes Conversations"
        
        // Configuration du RecyclerView
        conversationsAdapter = ConversationsAdapter { conversation ->
            // Ouvrir la conversation
            val intent = Intent(this, ChatBotActivity::class.java)
            intent.putExtra("conversationId", conversation.id)
            intent.putExtra("conversationTitle", conversation.titre)
            startActivity(intent)
        }
        
        binding.recyclerViewConversations.apply {
            layoutManager = LinearLayoutManager(this@ChatBotConversationsActivity)
            adapter = conversationsAdapter
        }
        
        // Bouton nouvelle conversation
        binding.fabNewConversation.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }
        
        // Pull to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadConversations()
        }
    }
    
    private fun setupObservers() {
        viewModel.conversations.observe(this) { conversations ->
            conversationsAdapter.submitList(conversations)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
    
    private fun loadConversations() {
        viewModel.loadConversations()
    }
    
    override fun onResume() {
        super.onResume()
        loadConversations() // Recharger quand on revient à l'activité
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}