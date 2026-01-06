package com.example.projetintegration.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorHandler {
    
    fun getErrorMessage(throwable: Throwable, context: Context? = null): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Requête invalide"
                    401 -> "Non autorisé - Veuillez vous reconnecter"
                    403 -> "Accès refusé - Vérifiez vos permissions"
                    404 -> "Ressource non trouvée"
                    408 -> "Délai d'attente dépassé"
                    500 -> "Erreur serveur interne"
                    502 -> "Serveur indisponible"
                    503 -> "Service temporairement indisponible"
                    else -> "Erreur HTTP ${throwable.code()}"
                }
            }
            is SocketTimeoutException -> "Délai d'attente dépassé - Vérifiez votre connexion"
            is UnknownHostException -> "Impossible de joindre le serveur - Vérifiez votre connexion internet"
            is IOException -> "Erreur de connexion réseau"
            else -> throwable.message ?: "Erreur inconnue"
        }
    }
    
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    
    fun shouldRetry(throwable: Throwable): Boolean {
        return when (throwable) {
            is SocketTimeoutException -> true
            is IOException -> true
            is HttpException -> throwable.code() in 500..599 // Erreurs serveur
            else -> false
        }
    }
}