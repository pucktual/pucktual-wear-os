/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package de.florianostertag.coffeehelper.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import de.florianostertag.coffeehelper.AppContainer
import de.florianostertag.coffeehelper.AuthWorker
import de.florianostertag.coffeehelper.CoffeeHelperApp
import de.florianostertag.coffeehelper.R
import de.florianostertag.coffeehelper.api.AuthManager
import de.florianostertag.coffeehelper.presentation.theme.CoffeeHelperTheme
import de.florianostertag.coffeehelper.ui.BeanListScreen
import de.florianostertag.coffeehelper.ui.BeanListViewModel
import de.florianostertag.coffeehelper.ui.ExtractionDetailScreen
import de.florianostertag.coffeehelper.ui.ExtractionDetailViewModel
import de.florianostertag.coffeehelper.ui.UrlSetupScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val appContainer: AppContainer
        get() = (application as CoffeeHelperApp).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            AuthWorker(appContainer).performAutoLogin()
        }

        setContent {
            CoffeeHelperTheme {
                WearApp(appContainer)
            }
        }
    }
}

@Composable
fun WearApp(
    appContainer: AppContainer,
    navController: NavHostController = rememberSwipeDismissableNavController(),
) {
    var startDestination = "beanList"
    if (!appContainer.urlManager.isUrlConfigured()) {
        startDestination = "setupUrl"
    }
    // Ruft einen Scope ab, um Coroutinen innerhalb des Composable zu starten
    val scope = rememberCoroutineScope()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {

        // ------------------------------------------
        // 1. ROUTE: SETUP / URL-KONFIGURATION
        // ------------------------------------------
        composable("setupUrl") {
            UrlSetupScreen(
                appContainer = appContainer,
                onSetupComplete = {
                    // Nach Speichern der URL und optionaler Login-Daten:

                    // Starte den Auto-Login-Vorgang im CoroutineScope
                    scope.launch {
                        AuthWorker(appContainer).performAutoLogin()

                        // Nach abgeschlossenem Login-Versuch zur Hauptliste navigieren
                        navController.navigate("beanList") {
                            // Entfernt den Setup-Screen aus dem Back-Stack
                            popUpTo("setupUrl") { inclusive = true }
                        }
                    }
                }
            )
        }

        // ------------------------------------------
        // 2. ROUTE: HAUPTLISTE DER KAFFEESORTEN
        // ------------------------------------------
        composable("beanList") {
            BeanListScreen(
                // Injiziere den AppContainer über einen Factory-Ansatz für ViewModels
                viewModel = viewModel(factory = BeanListViewModel.Factory(appContainer.coffeeApiService)),
                onBeanSelected = { beanId ->
                    navController.navigate("extractionDetail/$beanId")
                }
            )
        }

        // ------------------------------------------
        // 3. ROUTE: EXTRAKTIONS-DETAILS
        // ------------------------------------------
        composable("extractionDetail/{beanId}") { backStackEntry ->
            val beanId = backStackEntry.arguments?.getString("beanId")?.toLongOrNull()

            if (beanId != null) {
                ExtractionDetailScreen(
                    // Injiziere den BeanId und den Service über einen Factory-Ansatz
                    viewModel = viewModel(
                        factory = ExtractionDetailViewModel.Factory(
                            beanId = beanId,
                            apiService = appContainer.coffeeApiService
                        )
                    )
                )
            } else {
                Text("Fehler: Bohne nicht gefunden.")
            }
        }
    }
}