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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import de.florianostertag.coffeehelper.R
import de.florianostertag.coffeehelper.api.AuthManager
import de.florianostertag.coffeehelper.presentation.theme.CoffeeHelperTheme
import de.florianostertag.coffeehelper.ui.BeanListScreen
import de.florianostertag.coffeehelper.ui.ExtractionDetailScreen
import de.florianostertag.coffeehelper.ui.ExtractionDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        AuthManager.authToken = "DEIN_FESTER_TOKEN"

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    CoffeeHelperTheme {
        val navController = rememberSwipeDismissableNavController()

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "beanList"
        ) {
            composable("beanList") {
                BeanListScreen(
                    onBeanSelected = { beanId ->
                        navController.navigate("extractionDetail/$beanId")
                    }
                )
            }

            composable("extractionDetail/{beanId}") { backStackEntry ->
                val beanId = backStackEntry.arguments?.getString("beanId")?.toLongOrNull()
                if (beanId != null) {
                    // Hinzufügen der Navigation für den ViewModel-Konstruktor
                    ExtractionDetailScreen(
                        viewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return ExtractionDetailViewModel(SavedStateHandle(mapOf("beanId" to beanId))) as T
                                }
                            }
                        )
                    )
                } else {
                    Text("Fehler: Bohne nicht gefunden.")
                }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}