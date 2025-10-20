package de.florianostertag.coffeehelper.api

import android.util.Log
import de.florianostertag.coffeehelper.config.UrlManager
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

open class ApiClient(
    private val urlManager: UrlManager,
    val authInterceptor: AuthInterceptor
) {
    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) // Für Debugging
        .addInterceptor(authInterceptor)
        .build()

    open fun createApiService(): CoffeeApiService {
        val baseUrl = urlManager.getBaseUrl()

        if (baseUrl.isBlank()) {
            // WICHTIG: Werfe eine Ausnahme, wenn die URL fehlt.
            throw IllegalStateException("API Base URL not configured!")
        }

        try {
            val apiService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(CoffeeApiService::class.java)

            return apiService

        } catch (e: IllegalArgumentException) {
            handleFatalError(e)
            throw IllegalStateException("Die Basis-URL ist ungültig: ${e.message}")
        }
    }

    private fun handleFatalError(e: Exception) {
        Log.e("ApiClient","Resetting URL due to ${e.javaClass.simpleName}")
        urlManager.resetBaseUrl()
        authInterceptor.setToken(null)
    }
}