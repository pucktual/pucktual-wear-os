package de.florianostertag.coffeehelper.ui

import android.app.RemoteInput
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    val saveAndProceed: (String) -> Unit = {
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

    UrlSetupScreenForm(openUrlInput = { openInput("https://", saveAndProceed) }, errorMessage)
}
private const val TEXT_INPUT_KEY = "wear_text_input"

@Composable
fun wearTextInputHandler(): (initialText: String, onTextEntered: (String) -> Unit) -> Unit {

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
    return { initialText: String, onTextEntered: (String) -> Unit ->

        // Speichere den spezifischen Update-Callback für diesen Aufruf
        onTextEnteredCallback = onTextEntered

        // Erstelle den RemoteInput
        val remoteInput = RemoteInput.Builder(TEXT_INPUT_KEY)
            .setLabel("Eingabe")
            .setAllowFreeFormInput(true)
            .build()

        // Erstelle das Intent (um die Standard-Texteingabe-Activity zu starten)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_PROMPT, "URL eingeben")
            //putExtra(RemoteInput.EXTRA_REMOTE_INPUTS, arrayOf(remoteInput))
            // Füge diesen Flag hinzu, wenn Sie die QWERTY-Tastatur auf der Uhr erzwingen möchten
            // addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        launcher.launch(intent)
    }
}