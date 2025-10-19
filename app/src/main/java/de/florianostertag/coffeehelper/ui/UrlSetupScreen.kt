package de.florianostertag.coffeehelper.ui

import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import de.florianostertag.coffeehelper.AppContainer

@Composable
fun UrlSetupScreen(
    appContainer: AppContainer,
    onSetupComplete: () -> Unit
) {
    val urlManager = appContainer.urlManager

    var currentUrl by remember {
        mutableStateOf(urlManager.getBaseUrl().takeIf { it.isNotBlank() } ?: "https://")
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val openInput = wearTextInputHandler()

    val saveAndProceed = {
        errorMessage = null

        val url = currentUrl.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errorMessage = "URL muss mit http:// oder https:// beginnen."
        } else {
            urlManager.saveBaseUrl(url)
        }

        if (errorMessage == null) {
            onSetupComplete()
        }
    }

    Scaffold(timeText = { Text("Konfiguration") }) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
        ) {

            item { ListHeader { Text("Service Konfiguration") } }

            item {
                Text("Service URL (Obligatorisch):", style = MaterialTheme.typography.caption1)
                Button(onClick = {
                    // RUFE openInput auf und gib die korrekte Update-Lambda mit!
                    openInput(currentUrl, false) { newUrl ->
                        currentUrl = newUrl
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(currentUrl.takeIf { it.length < 25 } ?: "${currentUrl.take(20)}...")
                }
            }

            errorMessage?.let { msg ->
                item { Text(msg, color = MaterialTheme.colors.error) }
            }

            item {
                Button(onClick = saveAndProceed,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentUrl.isNotBlank()) {
                    Text("Speichern & Starten")
                }
            }
        }
    }

}
private const val TEXT_INPUT_KEY = "wear_text_input"

@Composable
fun wearTextInputHandler(): (initialText: String, isPassword: Boolean, onTextEntered: (String) -> Unit) -> Unit {

    // Wir nutzen hier nur einen einzelnen Launcher für alle Eingaben
    var onTextEnteredCallback by remember { mutableStateOf<(String) -> Unit>({}) }

    // 1. Definiere den Launcher für das Ergebnis der Texteingabe-Activity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Rufe das Ergebnis ab
        val remoteInput = RemoteInput.getResultsFromIntent(result.data)
        val enteredText = remoteInput?.getString(TEXT_INPUT_KEY) ?: ""

        // Führe den spezifischen State-Update-Callback aus
        onTextEnteredCallback(enteredText)
    }

    // 2. Liefere die Funktion zurück, die die Activity startet
    // Diese Funktion hat 3 Argumente
    return { initialText: String, isPassword: Boolean, onTextEntered: (String) -> Unit ->

        // Speichere den spezifischen Update-Callback für diesen Aufruf
        onTextEnteredCallback = onTextEntered

        // Erstelle den RemoteInput
        val remoteInput = RemoteInput.Builder(TEXT_INPUT_KEY)
            .setLabel(if (isPassword) "Passwort" else "Eingabe")
            .setAllowFreeFormInput(true)
            .build()

        // Erstelle das Intent (um die Standard-Texteingabe-Activity zu starten)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_PROMPT, if (isPassword) "Passwort" else "URL/Benutzername")
            //putExtra(RemoteInput.EXTRA_REMOTE_INPUTS, arrayOf(remoteInput))
            // Füge diesen Flag hinzu, wenn Sie die QWERTY-Tastatur auf der Uhr erzwingen möchten
            // addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        launcher.launch(intent)
    }
}