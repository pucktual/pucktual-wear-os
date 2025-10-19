package de.florianostertag.coffeehelper.ui

import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.wear.tooling.preview.devices.WearDevices
import de.florianostertag.coffeehelper.AppContainer
import de.florianostertag.coffeehelper.api.ApiClient
import de.florianostertag.coffeehelper.api.AuthInterceptor
import de.florianostertag.coffeehelper.api.AuthManager
import de.florianostertag.coffeehelper.config.UrlManager
import de.florianostertag.coffeehelper.presentation.theme.CoffeeHelperTheme
import kotlinx.coroutines.launch

@Composable
fun UrlSetupScreen(
    appContainer: AppContainer,
    onSetupComplete: () -> Unit
) {
    val urlManager = appContainer.urlManager
    val authManager = appContainer.authManager
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentUrl by remember {
        mutableStateOf(urlManager.getBaseUrl().takeIf { it.isNotBlank() } ?: "https://")
    }
    var storedPassword by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var passwordDisplay by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val PASSWORD_PLACEHOLDER = "••••••••"


    LaunchedEffect(key1 = Unit) {
        val savedUsername = authManager.getUsername()
        val savedPassword = authManager.getPassword()

        if (!savedUsername.isNullOrBlank()) {
            username = savedUsername
        }
        if (!savedPassword.isNullOrBlank()) {
            storedPassword = savedPassword
            passwordDisplay = PASSWORD_PLACEHOLDER
        }
    }

    val openInput = WearTextInputHandler()

    val saveAndProceed = {
        errorMessage = null

        val url = currentUrl.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errorMessage = "URL muss mit http:// oder https:// beginnen."
        } else {
            urlManager.saveBaseUrl(url)

            val isNewPasswordEntered = passwordDisplay.isNotBlank() && passwordDisplay != PASSWORD_PLACEHOLDER

            if (username.isBlank() && passwordDisplay.isBlank()) {
                scope.launch { authManager.clearAll() }
            } else if (username.isNotBlank() && (isNewPasswordEntered || storedPassword != null)) {
                val finalPassword = when {
                    isNewPasswordEntered -> passwordDisplay
                    storedPassword != null -> storedPassword!!
                    else -> {
                        // Dieser Fall sollte nicht eintreten, falls die Logik korrekt ist
                        // Er dient als Fallback für den Fall, dass username gesetzt ist, aber kein Passowrt
                        errorMessage = "Für den optionalen Login werden Benutzername UND Passwort benötigt."
                        null
                    }
                }

                if (finalPassword != null) {
                    scope.launch { authManager.saveCredentials(username.trim(), finalPassword) }
                }

            } else {
                // Fehler: Ungültige Kombination (z.B. nur Benutzername oder nur Passwort)
                // HINWEIS: Wenn der Benutzer nur den Benutzernamen ändert, *muss* er das Passwort neu eingeben,
                // da wir den `storedPassword` nur beibehalten, wenn der Platzhalter da ist.
                // Daher ist eine Fehlermeldung hier korrekt, um den Nutzer zur Neueingabe zu zwingen,
                // wenn er das Passwort ändern/bestätigen will.
                errorMessage = "Für den optionalen Login werden Benutzername UND Passwort benötigt (oder alle Felder leer lassen)."
            }
        }

        if (errorMessage == null) {
            onSetupComplete()
        }
    }

    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp)
        ) {

            item { ListHeader { Text("Service Konfiguration") } }

            // --- 1. URL (Obligatorisch) ---
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

            // --- 2. Username (Optional) ---
            item {
                Text("Login (Optional):", style = MaterialTheme.typography.caption1)
                Button(onClick = {
                    // RUFE openInput auf und gib die korrekte Update-Lambda mit!
                    openInput(username, false) { newUsername ->
                        username = newUsername
                    }
                }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Text(username.takeIf { it.isNotBlank() } ?: "Benutzername eingeben")
                }
            }

            // --- 3. Passwort (Optional) ---
            item {
                Button(onClick = {
                    // RUFE openInput auf und gib die korrekte Update-Lambda mit!
                    openInput(passwordDisplay, true) { newPassword ->
                        passwordDisplay = newPassword
                        // Das gespeicherte Passwort muss ungültig gemacht werden,
                        // da ein neues eingegeben wurde
                        storedPassword = null
                    }
                }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text(if (passwordDisplay.isNotBlank()) PASSWORD_PLACEHOLDER else "Passwort eingeben")
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
fun WearTextInputHandler(): (initialText: String, isPassword: Boolean, onTextEntered: (String) -> Unit) -> Unit {

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

// ----------------------------------------------------------------------------------
// PREVIEW IMPLEMENTIERUNG (MIT MOCK-KLASSEN)
// ----------------------------------------------------------------------------------

// Benötigte Mock-Klassen für die Vorschau (müssen definiert sein)
class MockUrlManager(context: Context) : UrlManager(context) {
    override fun saveBaseUrl(url: String) {}
    override fun getBaseUrl(): String = "https://my.test-server.coffee/"
    override fun isUrlConfigured(): Boolean = true
}

class MockAuthManager(context: Context) : AuthManager(context) {
    override suspend fun saveCredentials(username: String, password: String) {}
    override suspend fun getUsername(): String? = "barista_user"
    override suspend fun getPassword(): String? = "encrypted_mock_pass"
}

class MockAuthInterceptor(authManager: AuthManager) : AuthInterceptor(authManager) {
    // Keine weiteren Anpassungen nötig, solange der Konstruktor aufgerufen wird.
}

class MockAppContainer(context: Context) : AppContainer(context) {
    override val urlManager = MockUrlManager(context)
    override val authManager = MockAuthManager(context)

    override val retrofitClient: ApiClient
        get() = ApiClient(urlManager, mockAuthInterceptor)

    // Wir definieren alle abhängigen Objekte hier, um die Reihenfolge zu steuern:
    private val mockAuthInterceptor by lazy { AuthInterceptor(authManager) }}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun UrlSetupScreenPreview() {
    val context = LocalContext.current
    // Instanziiere den Mock Container mit Context
    val mockContainer = remember { MockAppContainer(context) }

    CoffeeHelperTheme {
        UrlSetupScreen(
            appContainer = mockContainer,
            onSetupComplete = { }
        )
    }
}