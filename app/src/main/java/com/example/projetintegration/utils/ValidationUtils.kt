package com.example.projetintegration.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPhone(phone: String): Boolean {
        // Format simplifié: exactement 8 chiffres
        val phonePattern = "^[0-9]{8}$".toRegex()
        return phone.matches(phonePattern)
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            val parsedDate = sdf.parse(date)
            parsedDate != null && parsedDate.before(Date())
        } catch (e: Exception) {
            false
        }
    }
    
    fun isNotEmpty(text: String): Boolean {
        return text.trim().isNotEmpty()
    }
}
