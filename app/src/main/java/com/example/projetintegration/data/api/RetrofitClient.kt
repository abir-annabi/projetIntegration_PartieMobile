package com.example.projetintegration.data.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = "http://10.0.2.2:8084/" // Pour émulateur Android
    // Pour appareil physique, utilisez: "http://VOTRE_IP:8081/"
    
    private var retrofit: Retrofit? = null
    
    fun initialize(context: Context) {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val authInterceptor = AuthInterceptor(context.applicationContext)
            
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
    
    private fun getRetrofit(): Retrofit {
        return retrofit ?: throw IllegalStateException("RetrofitClient must be initialized first")
    }
    
    val authApiService: AuthApiService
        get() = getRetrofit().create(AuthApiService::class.java)
    
    val programmeApiService: ProgrammeApiService
        get() = getRetrofit().create(ProgrammeApiService::class.java)
    
    val platApiService: PlatApiService
        get() = getRetrofit().create(PlatApiService::class.java)
}
