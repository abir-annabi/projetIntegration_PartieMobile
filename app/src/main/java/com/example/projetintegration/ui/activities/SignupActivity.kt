package com.example.projetintegration.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projetintegration.R
import com.example.projetintegration.data.preferences.PreferencesManager
import com.example.projetintegration.databinding.ActivitySignupBinding
import com.example.projetintegration.ui.viewmodel.SignupViewModel
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel
    private lateinit var preferencesManager: PreferencesManager
    private var selectedDate: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.nomError.observe(this) { error ->
            binding.tvNomError.text = error
            binding.tvNomError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.prenomError.observe(this) { error ->
            binding.tvPrenomError.text = error
            binding.tvPrenomError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.phoneError.observe(this) { error ->
            binding.tvPhoneError.text = error
            binding.tvPhoneError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.emailError.observe(this) { error ->
            binding.tvEmailError.text = error
            binding.tvEmailError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.passwordError.observe(this) { error ->
            binding.tvPasswordError.text = error
            binding.tvPasswordError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.dateError.observe(this) { error ->
            binding.tvDateError.text = error
            binding.tvDateError.visibility = if (error != null) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSignup.isEnabled = !isLoading
            binding.btnSignup.text = if (isLoading) "" else getString(R.string.signup_button)
        }
        
        viewModel.signupResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
            result.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.btnSignup.setOnClickListener {
            val nom = binding.etNom.text.toString()
            val prenom = binding.etPrenom.text.toString()
            val phone = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            
            viewModel.signup(nom, prenom, phone, email, password, selectedDate)
        }
        
        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18) // Par défaut, 18 ans en arrière
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = sdf.format(cal.time)
                
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etDate.setText(displayFormat.format(cal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Limiter la date maximale à aujourd'hui
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
