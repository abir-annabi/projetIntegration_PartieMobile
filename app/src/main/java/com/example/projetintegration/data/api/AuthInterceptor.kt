package com.example.projetintegration.data.api

import android.content.Context
import android.util.Log
import com.example.projetintegration.data.preferences.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferencesManager = PreferencesManager(context)
        val token = preferencesManager.getToken()
        
        val requestBuilder = chain.request().newBuilder()
        
        // Ajouter le token si disponible
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Token ajouté pour ${chain.request().url}")
            Log.d("AuthInterceptor", "Token: ${token.take(20)}...")
        } else {
            Log.w("AuthInterceptor", "Aucun token disponible pour ${chain.request().url}")
        }
        
        // Ajouter des headers supplémentaires
        requestBuilder.addHeader("Accept", "application/json")
        
        val request = requestBuilder.build()
        Log.d("AuthInterceptor", "Requête: ${request.method} ${request.url}")
        
        val response = try {
            chain.proceed(request)
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthInterceptor", "⏰ TIMEOUT DE CONNEXION")
            Log.e("AuthInterceptor", "🎯 Cible: ${request.url.host}:${request.url.port}")
            Log.e("AuthInterceptor", "⏱️ Durée: ${e.message}")
            Log.e("AuthInterceptor", "")
            Log.e("AuthInterceptor", "🔧 SOLUTIONS À ESSAYER:")
            Log.e("AuthInterceptor", "   1. ✅ Vérifier que le backend Spring Boot est DÉMARRÉ")
            Log.e("AuthInterceptor", "   2. ✅ Confirmer le port 8100 dans application.properties")
            Log.e("AuthInterceptor", "   3. ✅ Tester: curl http://localhost:8100/api/auth/test")
            Log.e("AuthInterceptor", "   4. ✅ Vérifier les logs du backend pour erreurs")
            Log.e("AuthInterceptor", "   5. ✅ Redémarrer le backend si nécessaire")
            Log.e("AuthInterceptor", "")
            throw e
        } catch (e: java.net.ConnectException) {
            Log.e("AuthInterceptor", "🔌 CONNEXION REFUSÉE")
            Log.e("AuthInterceptor", "🎯 Cible: ${request.url.host}:${request.url.port}")
            Log.e("AuthInterceptor", "📝 Erreur: ${e.message}")
            Log.e("AuthInterceptor", "")
            Log.e("AuthInterceptor", "🔧 DIAGNOSTIC RÉSEAU:")
            Log.e("AuthInterceptor", "   1. ✅ Backend Spring Boot est-il démarré?")
            Log.e("AuthInterceptor", "   2. ✅ Port 8100 est-il libre? (netstat -an | grep 8100)")
            Log.e("AuthInterceptor", "   3. ✅ Firewall bloque-t-il le port?")
            Log.e("AuthInterceptor", "   4. ✅ IP correcte? (émulateur: 10.0.2.2, appareil: IP locale)")
            Log.e("AuthInterceptor", "")
            Log.e("AuthInterceptor", "🧪 TESTS À EFFECTUER:")
            Log.e("AuthInterceptor", "   • curl http://10.0.2.2:8100/api/auth/test")
            Log.e("AuthInterceptor", "   • telnet 10.0.2.2 8100")
            Log.e("AuthInterceptor", "")
            throw e
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthInterceptor", "🌐 HÔTE INCONNU: ${request.url.host}")
            Log.e("AuthInterceptor", "💡 Vérifiez la configuration DNS/réseau")
            throw e
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "❌ Erreur réseau: ${e.message}")
            Log.e("AuthInterceptor", "Erreur réseau: failed to connect to /10.0.2.2 (port 8100) from /10.0.2.16 (port 51386) after 59999ms", e)
            throw e
        }
        
        Log.d("AuthInterceptor", "Réponse: ${response.code} pour ${request.url}")
        
        // Logger les erreurs d'authentification avec plus de détails
        if (response.code == 403 || response.code == 401) {
            Log.e("AuthInterceptor", "Erreur d'authentification (${response.code}) pour ${chain.request().url}")
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            Log.e("AuthInterceptor", "Corps de l'erreur: $errorBody")
            
            if (response.code == 401) {
                Log.w("AuthInterceptor", "Token expiré ou invalide, suppression du token")
                preferencesManager.clearAuthData()
            }
        }
        
        return response
    }
}
