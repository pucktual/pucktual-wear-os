package de.pucktual.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.Dialog
import de.pucktual.AuthWorker
import de.pucktual.PucktualApp
import kotlinx.coroutines.launch

@Composable
fun LoginDialog(
    onLoginSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    // 1. Abhängigkeitszugriff
    val context = LocalContext.current
    // Hole den AppContainer aus der globalen Application Instanz
    val appContainer = remember { (context.applicationContext as PucktualApp).container }

    // 2. Lokaler Zustand
    val scope = rememberCoroutineScope()
    val authWorker = remember { AuthWorker(appContainer) }

    var usernameState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Platzhalter für die native Texteingabe
    val openInput = { initialText: String, isPassword: Boolean, onTextEntered: (String) -> Unit ->
        // In einer realen App: Starte hier die RemoteInput-Activity.
        // Für das Prototyping: Nutze simulierte Eingabe.
        if (initialText.isBlank()) {
            onTextEntered(if (isPassword) "pass_sim" else "user_sim")
        } else {
            onTextEntered(initialText) // Nutze den aktuellen Wert (für Previews)
        }
    }

    // 3. Login-Logik
    val attemptLogin = {
        if (usernameState.isBlank() || passwordState.isBlank()) {
            loginError = "Bitte Benutzername und Passwort eingeben."
        } else {
            isLoading = true
            loginError = null

            scope.launch {
                val success = authWorker.manualLogin(usernameState, passwordState)

                if (success) {
                    onLoginSuccess()
                } else {
                    loginError = "Login fehlgeschlagen. Ungültige Anmeldedaten."
                }
                isLoading = false
            }
        }
    }

    // 4. Modales Layout
    Dialog(
        showDialog = true,
        onDismissRequest = onCancel,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ladekreis, wenn versucht wird, sich anzumelden
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { ListHeader { Text("Anmeldung erforderlich") } }

                        // Benutzername Eingabe
                        item {
                            Button(onClick = { openInput(usernameState, false) { usernameState = it } }) {
                                Text(usernameState.takeIf { it.isNotBlank() } ?: "Benutzername")
                            }
                        }

                        // Passwort Eingabe
                        item {
                            Button(onClick = { openInput(passwordState, true) { passwordState = it } }) {
                                Text(if (passwordState.isNotBlank()) "••••••••" else "Passwort")
                            }
                        }

                        // Login Button
                        item {
                            Button(
                                onClick = attemptLogin as () -> Unit,
                                modifier = Modifier.padding(top = 8.dp),
                                enabled = usernameState.isNotBlank() && passwordState.isNotBlank()
                            ) {
                                Text("Anmelden")
                            }
                        }

                        // Fehlermeldung
                        loginError?.let { msg ->
                            item {
                                Text(msg, color = MaterialTheme.colors.error)
                            }
                        }
                    }
                }
            }
        }
    )
}