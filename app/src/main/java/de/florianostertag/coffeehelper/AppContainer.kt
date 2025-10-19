package de.florianostertag.coffeehelper

import android.content.Context
import de.florianostertag.coffeehelper.api.ApiClient
import de.florianostertag.coffeehelper.api.AuthInterceptor
import de.florianostertag.coffeehelper.api.AuthManager
import de.florianostertag.coffeehelper.api.CoffeeApiService
import de.florianostertag.coffeehelper.config.UrlManager

open class AppContainer(context: Context) {

    open val urlManager = UrlManager(context)
    open val authManager = AuthManager(context)

    // RetrofitClient benötigt Manager und Interceptor
    private val authInterceptor = AuthInterceptor(authManager)

    open val retrofitClient = ApiClient(urlManager, authInterceptor)

    // Lazy-Zugriff auf den Service
    val coffeeApiService: CoffeeApiService
        get() = retrofitClient.createApiService()
}