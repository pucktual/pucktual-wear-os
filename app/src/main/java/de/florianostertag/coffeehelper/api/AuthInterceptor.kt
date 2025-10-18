package de.florianostertag.coffeehelper.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        AuthManager.authToken?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}