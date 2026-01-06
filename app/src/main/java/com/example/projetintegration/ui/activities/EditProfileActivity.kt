package com.example.projetintegration.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.R
import com.example.projetintegration.data.models.UpdateProfileRequest
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivityEditProfileBinding
import com.example.projetintegration.ui.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var preferencesManager: PreferencesManager
    private var selectedDate: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        preferencesManager = PreferencesManager(this)
        
        setupSpinners()
        setupObservers()
        setupClickListeners()
        loadProfile()
    }
    
    private fun setupSpinners() {
        // Sexe
        val sexeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sexe_options,
            android.R.layout.simple_spinner_item
        )
        sexeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSexe.adapter = sexeAdapter
        
        // Objectif
        val objectifAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.objectif_options,
            android.R.layout.simple_spinner_item
        )
        objectifAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerObjectif.adapter = objectifAdapter
        
        // Niveau d'activité
        val niveauAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.niveau_activite_options,
            android.R.layout.simple_spinner_item
        )
        niveauAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNiveauActivite.adapter = niveauAdapter
    }
    
    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            binding.etNom.setText(user.nom)
            binding.etPrenom.setText(user.prenom)
            binding.etEmail.setText(user.adresseEmail)
            binding.etPhone.setText(user.numTel)
            binding.etDateNaissance.setText(user.dateNaissance)
            selectedDate = user.dateNaissance
            
            user.taille?.let { binding.etTaille.setText(it.toString()) }
            user.poids?.let { binding.etPoids.setText(it.toString()) }
            
            // Set spinner selections
            user.sexe?.let { setSpinnerSelection(binding.spinnerSexe, it) }
            user.objectif?.let { setSpinnerSelection(binding.spinnerObjectif, it) }
            user.niveauActivite?.let { setSpinnerSelection(binding.spinnerNiveauActivite, it) }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !isLoading
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.etDateNaissance.setOnClickListener {
            showDatePicker()
        }
        
        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }
    
    private fun loadProfile() {
        // Le backend identifie l'utilisateur via le token JWT
        viewModel.loadProfile()
    }
    
    private fun saveProfile() {
        val nom = binding.etNom.text.toString().trim()
        val prenom = binding.etPrenom.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val tailleStr = binding.etTaille.text.toString().trim()
        val poidsStr = binding.etPoids.text.toString().trim()
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        
        val taille = if (tailleStr.isNotEmpty()) tailleStr.toDoubleOrNull() else null
        val poids = if (poidsStr.isNotEmpty()) poidsStr.toDoubleOrNull() else null
        
        val sexe = when (binding.spinnerSexe.selectedItemPosition) {
            1 -> "HOMME"
            2 -> "FEMME"
            else -> null
        }
        
        val objectif = when (binding.spinnerObjectif.selectedItemPosition) {
            1 -> "PERTE_POIDS"
            2 -> "PRISE_MASSE"
            3 -> "MAINTIEN"
            else -> null
        }
        
        val niveauActivite = when (binding.spinnerNiveauActivite.selectedItemPosition) {
            1 -> "SEDENTAIRE"
            2 -> "LEGER"
            3 -> "MODERE"
            4 -> "INTENSE"
            5 -> "TRES_INTENSE"
            else -> null
        }
        
        val request = UpdateProfileRequest(
            nom = nom,
            prenom = prenom,
            numTel = phone,
            adresseEmail = email,
            dateNaissance = selectedDate,
            taille = taille,
            poids = poids,
            sexe = sexe,
            objectif = objectif,
            niveauActivite = niveauActivite
        )
        
        // Le backend identifie l'utilisateur via le token JWT
        viewModel.updateProfile(request)
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        // Parse current date if exists
        if (selectedDate.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                calendar.time = sdf.parse(selectedDate) ?: Date()
            } catch (e: Exception) {
                // Use current date
            }
        }
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = sdf.format(cal.time)
                
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDateNaissance.setText(displayFormat.format(cal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    
    private fun setSpinnerSelection(spinner: android.widget.Spinner, value: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            val item = adapter.getItem(i).toString()
            if (item.contains(value, ignoreCase = true)) {
                spinner.setSelection(i)
                break
            }
        }
    }
}
