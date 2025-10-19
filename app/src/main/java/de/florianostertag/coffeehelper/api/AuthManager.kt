package de.florianostertag.coffeehelper.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_settings")

open class AuthManager(private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USERNAME_KEY = stringPreferencesKey("saved_username")
    private val PASSWORD_KEY = stringPreferencesKey("saved_password")


    /** Speichern des Tokens nach erfolgreichem Login. */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { settings ->
            settings[TOKEN_KEY] = token
        }
    }

    /** Asynchrones Abrufen des Tokens. Gibt den Token als String oder null zurück. */
    suspend fun getAuthToken(): String? {
        // .first() blockiert, aber nur die Coroutine, nicht den UI-Thread
        return context.dataStore.data.map { settings ->
            settings[TOKEN_KEY]
        }.first()
    }

    // ----------------- CREDENTIALS-VERWALTUNG -----------------

    /** Speichert die optionalen Anmeldedaten. */
    open suspend fun saveCredentials(username: String, password: String) {
        context.dataStore.edit { settings ->
            settings[USERNAME_KEY] = username
            settings[PASSWORD_KEY] = password
        }
    }

    /** Gibt den gespeicherten Benutzernamen zurück. */
    fun getUsernameFlow() = context.dataStore.data.map { settings ->
        settings[USERNAME_KEY]
    }

    /** Gibt das gespeicherte Passwort zurück. */
    fun getPasswordFlow() = context.dataStore.data.map { settings ->
        settings[PASSWORD_KEY]
    }

    // Einfache Getter, die die Coroutine blockieren, um den Wert sofort zu liefern (für Login-Prozess)
    open suspend fun getUsername(): String? = getUsernameFlow().first()
    open suspend fun getPassword(): String? = getPasswordFlow().first()

    /** Löscht Anmeldedaten und Token. */
    suspend fun clearAll() {
        context.dataStore.edit { settings ->
            settings.remove(TOKEN_KEY)
            settings.remove(USERNAME_KEY)
            settings.remove(PASSWORD_KEY)
        }
    }
}