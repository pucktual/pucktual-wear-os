package de.florianostertag.coffeehelper.config

import android.content.Context
import androidx.core.content.edit

open class UrlManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
    private val BASE_URL_KEY = "base_api_url"
    private val DEFAULT_URL = ""

    open fun saveBaseUrl(url: String) {
        val formattedUrl = if (url.endsWith("/")) url else "$url/"
        prefs.edit { putString(BASE_URL_KEY, formattedUrl) }
    }

    open fun getBaseUrl(): String {
        return prefs.getString(BASE_URL_KEY, DEFAULT_URL) ?: DEFAULT_URL
    }

    open fun isUrlConfigured(): Boolean {
        return getBaseUrl().isNotBlank()
    }
}