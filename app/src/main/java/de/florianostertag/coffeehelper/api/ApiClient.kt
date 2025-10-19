package de.florianostertag.coffeehelper.api

import de.florianostertag.coffeehelper.config.UrlManager
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ApiClient(private val urlManager: UrlManager, val authInterceptor: AuthInterceptor) {
    private val moshi = com.squareup.moshi.Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) // Für Debugging
        .addInterceptor(authInterceptor)
        .build()

    fun createApiService(): CoffeeApiService {
        val baseUrl = urlManager.getBaseUrl()

        if (baseUrl.isBlank()) {
            // WICHTIG: Werfe eine Ausnahme, wenn die URL fehlt.
            throw IllegalStateException("API Base URL not configured!")
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CoffeeApiService::class.java)
    }
}