package com.example.projetintegration.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetintegration.R
import com.example.projetintegration.data.models.CommunityMessageResponse
import com.example.projetintegration.data.models.MessageRequest
import com.example.projetintegration.databinding.ActivityMessageBinding
import com.example.projetintegration.ui.adapters.MessageAdapter
import com.example.projetintegration.ui.viewmodel.MessageViewModel
import kotlinx.coroutines.launch

class MessageActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMessageBinding
    private val viewModel: MessageViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
        setupFab()
        
        // Charger les messages
        viewModel.loadMessages()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Communauté"
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(
            onLikeClick = { message -> handleLikeClick(message) },
            onReplyClick = { message -> showReplyDialog(message) },
            onMenuClick = { message -> showMessageMenu(message) },
            onViewRepliesClick = { message -> toggleRepliesVisibility(message) }
        )
        
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            adapter = messageAdapter
        }
    }
    
    private fun setupObservers() {
        // Messages
        viewModel.messages.observe(this) { messages ->
            messageAdapter.submitMessages(messages)
            binding.emptyView.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && messageAdapter.itemCount == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        
        // Error messages
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Success messages
        viewModel.createSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Message publié avec succès", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateSuccess()
            }
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadMessages(refresh = true)
        }
    }
    
    private fun setupFab() {
        binding.fabNewMessage.setOnClickListener {
            showNewMessageDialog()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_messages, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_search -> {
                showSearchDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun handleLikeClick(message: CommunityMessageResponse) {
        lifecycleScope.launch {
            viewModel.toggleLike(message.id)
        }
    }
    
    private fun showNewMessageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_message, null)
        val editTextContent = dialogView.findViewById<EditText>(R.id.editTextContent)
        val switchAnonymous = dialogView.findViewById<Switch>(R.id.switchAnonymous)
        
        AlertDialog.Builder(this)
            .setTitle("Nouveau message")
            .setView(dialogView)
            .setPositiveButton("Publier") { _, _ ->
                val content = editTextContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.creerMessage(content, switchAnonymous.isChecked)
                } else {
                    Toast.makeText(this, "Le contenu ne peut pas être vide", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun showReplyDialog(parentMessage: CommunityMessageResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reply_message, null)
        val editTextContent = dialogView.findViewById<EditText>(R.id.editTextContent)
        val switchAnonymous = dialogView.findViewById<Switch>(R.id.switchAnonymous)
        
        AlertDialog.Builder(this)
            .setTitle("Répondre à ${if (parentMessage.anonyme) parentMessage.nomAnonyme else "${parentMessage.prenomAuteur} ${parentMessage.nomAuteur}"}")
            .setView(dialogView)
            .setPositiveButton("Répondre") { _, _ ->
                val content = editTextContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.creerMessage(content, switchAnonymous.isChecked, parentMessage.id)
                } else {
                    Toast.makeText(this, "Le contenu ne peut pas être vide", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun showMessageMenu(message: CommunityMessageResponse) {
        val options = arrayOf("Modifier", "Supprimer")
        
        AlertDialog.Builder(this)
            .setTitle("Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(message)
                    1 -> confirmDeleteMessage(message)
                }
            }
            .show()
    }
    
    private fun showEditDialog(message: CommunityMessageResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_message, null)
        val editTextContent = dialogView.findViewById<EditText>(R.id.editTextContent)
        
        editTextContent.setText(message.contenu)
        
        AlertDialog.Builder(this)
            .setTitle("Modifier le message")
            .setView(dialogView)
            .setPositiveButton("Modifier") { _, _ ->
                val content = editTextContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    viewModel.modifierMessage(message.id, content)
                } else {
                    Toast.makeText(this, "Le contenu ne peut pas être vide", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun confirmDeleteMessage(message: CommunityMessageResponse) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer le message")
            .setMessage("Êtes-vous sûr de vouloir supprimer ce message ?")
            .setPositiveButton("Supprimer") { _, _ ->
                viewModel.supprimerMessage(message.id)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun showSearchDialog() {
        val editText = EditText(this)
        editText.hint = "Rechercher dans les messages..."
        
        AlertDialog.Builder(this)
            .setTitle("Rechercher")
            .setView(editText)
            .setPositiveButton("Rechercher") { _, _ ->
                val query = editText.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchMessages(query)
                } else {
                    viewModel.loadMessages()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    private fun toggleRepliesVisibility(message: CommunityMessageResponse) {
        val currentMessages = viewModel.messages.value ?: return
        messageAdapter.toggleRepliesExpansion(message.id, currentMessages)
    }
    
    private fun viewReplies(message: CommunityMessageResponse) {
        // Cette méthode n'est plus utilisée, remplacée par toggleRepliesVisibility
        toggleRepliesVisibility(message)
    }
}