package de.pucktual.ui

import android.app.RemoteInput
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.pucktual.AppContainer

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

    val saveAndProceed: (String) -> Unit = { newUrl ->
        errorMessage = null

        val url = newUrl.trim()
        Log.d("UrlSetupScreen", "Benutzer hat folgende URL eingegeben: $url")

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errorMessage = "URL muss mit http:// oder https:// beginnen."
        } else {
            currentUrl = url
            urlManager.saveBaseUrl(url)
        }

        if (errorMessage == null) {
            onSetupComplete()
        }
    }

    UrlSetupScreenForm(openUrlInput = {
        openInput(currentUrl) { enteredUrl ->
            saveAndProceed(enteredUrl)
        }}, errorMessage)
}
private const val TEXT_INPUT_KEY = "wear_text_input"

@Composable
fun wearTextInputHandler(): (initialText: String, onTextEntered: (String) -> Unit) -> Unit {

    var onTextEnteredCallback by remember { mutableStateOf<(String) -> Unit>({}) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data // Das Ergebnis-Intent

        val remoteInput = RemoteInput.getResultsFromIntent(data)
        var enteredText = remoteInput?.getString(TEXT_INPUT_KEY)

        if (enteredText.isNullOrBlank()) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            enteredText = results?.get(0)
        }

        onTextEnteredCallback(enteredText ?: "")
    }

    return { initialText: String, onTextEntered: (String) -> Unit ->

        onTextEnteredCallback = onTextEntered

        val remoteInput = RemoteInput.Builder(TEXT_INPUT_KEY)
            .setLabel("Eingabe")
            .setAllowFreeFormInput(true)
            .build()

        // Erstelle das Intent (um die Standard-Texteingabe-Activity zu starten)
        // Verwenden Sie ACTION_SEND oder ACTION_GET_CONTENT, wenn Sie nur die Tastatur anzeigen möchten,
        // aber RecognizerIntent ist typisch für die initiale Wear OS Eingabe.
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_PROMPT, "URL eingeben")

            // WICHTIG: Füge das RemoteInput-Array hinzu, um die Tastatureingabe zu ermöglichen!
            // Der standardmäßige Android-Eingabemechanismus nutzt dies.
            //val remoteInputs = arrayOf(remoteInput)
            //RemoteInput.addResultsToIntent(remoteInputs, this, null) // Fügt RemoteInput hinzu
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "de.pucktual")
        }

        launcher.launch(intent)
    }
}