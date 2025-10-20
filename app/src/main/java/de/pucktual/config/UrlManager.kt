package de.pucktual.config

import android.content.Context
import android.util.Log
import androidx.core.content.edit

open class UrlManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
    private val BASE_URL_KEY = "base_api_url"
    private val DEFAULT_URL = ""

    open fun resetBaseUrl() {
        Log.d("UrlManager", "Resetting URL")
        prefs.edit { remove(BASE_URL_KEY) }
    }

    open fun saveBaseUrl(url: String) {
        val formattedUrl = if (url.endsWith("/")) url else "$url/"
        prefs.edit { putString(BASE_URL_KEY, formattedUrl) }
    }

    open fun getBaseUrl(): String {
        return prefs.getString(BASE_URL_KEY, DEFAULT_URL) ?: DEFAULT_URL
    }

    open fun isUrlConfigured(): Boolean {
        Log.d("UrlManager", "isUrlConfigured: ${getBaseUrl().isNotBlank()}, ${getBaseUrl()}")
        return getBaseUrl().isNotBlank()
    }
}