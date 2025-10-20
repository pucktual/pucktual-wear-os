package de.pucktual.api

import okhttp3.Interceptor
import okhttp3.Response

open class AuthInterceptor() : Interceptor {

    @Volatile
    private var currentToken: String? = null

    fun setToken(token: String?) {
        this.currentToken = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        currentToken?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}