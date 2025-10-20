package de.pucktual

import android.content.Context
import de.pucktual.api.ApiClient
import de.pucktual.api.AuthInterceptor
import de.pucktual.api.AuthManager
import de.pucktual.api.CoffeeApiService
import de.pucktual.config.UrlManager

open class AppContainer(context: Context) {

    open val urlManager = UrlManager(context)
    open val authManager = AuthManager(context)

    // RetrofitClient benötigt Manager und Interceptor
    open val authInterceptor = AuthInterceptor()

    open val retrofitClient = ApiClient(urlManager, authInterceptor)

    // Lazy-Zugriff auf den Service
    val coffeeApiService: CoffeeApiService
        get() = retrofitClient.createApiService()
}