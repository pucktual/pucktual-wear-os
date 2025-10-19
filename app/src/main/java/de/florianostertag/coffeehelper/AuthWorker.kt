package de.florianostertag.coffeehelper

import de.florianostertag.coffeehelper.data.LoginRequest

class AuthWorker(
    private val appContainer: AppContainer,
) {
    /**
     * Führt den automatischen Login durch, falls Anmeldedaten gespeichert sind.
     * @return true, wenn ein gültiges Token verfügbar ist, sonst false.
     */
    suspend fun performAutoLogin(): Boolean {

        // 1. Zuerst prüfen, ob bereits ein Token vorhanden ist
        if (appContainer.authManager.getAuthToken() != null) {
            // Token ist vorhanden (von letzter Session), Interceptor aktualisieren und als bereit markieren
            appContainer.retrofitClient.authInterceptor.setToken(appContainer.authManager.getAuthToken())
            return true
        }

        // 2. Anmeldedaten asynchron aus DataStore abrufen
        val username = appContainer.authManager.getUsername()
        val password = appContainer.authManager.getPassword()

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            // Keine optionalen Anmeldedaten gespeichert
            return false
        }

        // 3. API-Aufruf (Login)
        return try {
            val loginRequest = LoginRequest(username, password)
            val response = appContainer.coffeeApiService.login(loginRequest)

            val token = response.accessToken

            // 4. Bei Erfolg: Token speichern und Interceptor aktualisieren
            appContainer.authManager.saveAuthToken(token)
            appContainer.retrofitClient.authInterceptor.setToken(token)
            true

        } catch (e: Exception) {
            // Login fehlgeschlagen (z.B. falsche Daten, Server nicht erreichbar)
            // Protokollierung hier einfügen
            false
        }
    }
}