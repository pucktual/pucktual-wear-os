package de.pucktual.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Grain
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import de.pucktual.api.CoffeeApiService
import de.pucktual.data.Bean
import de.pucktual.data.Extraction
import de.pucktual.data.LoginRequest
import de.pucktual.data.LoginResponse
import de.pucktual.data.getMockBeans
import de.pucktual.presentation.theme.CoffeeHelperTheme

@Composable
fun ExtractionDetailScreen(
    viewModel: ExtractionDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        timeText = { TimeText() }
    ) {
        when (val state = uiState) {
            is ExtractionDetailViewModel.UiState.Loading -> {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is ExtractionDetailViewModel.UiState.Error -> {
                Text(state.message, style = MaterialTheme.typography.caption1)
            }
            ExtractionDetailViewModel.UiState.Empty -> {
                Text("Keine Extraktionen für diese Bohne gespeichert.")
            }
            is ExtractionDetailViewModel.UiState.Success -> {
                val latestExtraction = state.extractions.first()

                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = 32.dp,
                        bottom = 32.dp
                    )
                ) {
                    item { ListHeader { Text("Aktuelles Rezept") } }

                    // --- Haupt-Rezept Card ---
                    item {
                        RecipeCard(extraction = latestExtraction)
                    }

                    // --- Hinweis ---
                    latestExtraction.nextExtractionHint?.takeIf { it.isNotBlank() }?.let { hint ->
                        item {
                            Text("Tipp:", style = MaterialTheme.typography.caption1, modifier = Modifier.padding(top = 8.dp))
                        }
                        item {
                            Text(hint, style = MaterialTheme.typography.body2)
                        }
                    }

                    // --- Historie ---
                    if (state.extractions.size > 1) {
                        item { ListHeader { Text("Verlauf") } }
                        items(state.extractions.drop(1)) { extraction ->
                            Chip(
                                onClick = { /* Zeige volles Detail in einem Dialog? */ },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Verhältnis ${String.format("%.1f", extraction.outputGrams / extraction.inputGrams)}:1") },
                                secondaryLabel = { Text("${extraction.inputGrams}g IN | ${extraction.time}s | ID ${extraction.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper Composable, um das Hauptrezept visuell hervorzuheben
@Composable
fun RecipeCard(extraction: Extraction, modifier: Modifier = Modifier) {
    Card(
        onClick = { /* Keine Aktion */ },
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        backgroundPainter = CardDefaults.cardBackgroundPainter(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Obere Reihe: Mahlgrad & IN
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                // Neu: Arrangement.Center für horizontale Zentrierung der Row-Inhalte
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically // Vertikale Zentrierung innerhalb der Reihe
            ) {
                // Oben Links: Mahlgrad
                RecipeDataItem(
                    icon = Icons.Filled.Grain,
                    label = "Grind",
                    value = extraction.grind?.toString() ?: "-",
                    modifier = Modifier.weight(1f) // Nimmt die Hälfte der Breite
                )

                // Trennlinie vertikal
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(0.7f) // Nimmt 70% der vertikalen Höhe der Reihe
                        .padding(horizontal = 4.dp)
                )

                // Oben Rechts: IN
                RecipeDataItem(
                    icon = Icons.Filled.CallReceived,
                    label = "In",
                    value = "${String.format("%.1f", extraction.inputGrams)}g",
                    modifier = Modifier.weight(1f) // Nimmt die Hälfte der Breite
                )
            }

            // Trennlinie horizontal zwischen den Reihen
            Divider(
                modifier = Modifier.fillMaxWidth(0.8f) // Nimmt 80% der Breite
                    .align(Alignment.CenterHorizontally) // Zentriert die Linie horizontal
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f) // Leichtere Farbe
            )

            // Untere Reihe: Dauer & OUT
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Unten Links: Dauer
                RecipeDataItem(
                    icon = Icons.Filled.AvTimer,
                    label = "Time",
                    value = "${extraction.time}s",
                    modifier = Modifier.weight(1f)
                )

                // Trennlinie vertikal
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(0.7f)
                        .padding(horizontal = 4.dp)
                )

                // Unten Rechts: OUT
                RecipeDataItem(
                    icon = Icons.Filled.CallMade,
                    label = "Out",
                    value = "${String.format("%.1f", extraction.outputGrams)}g",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RecipeDataItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Icons und Werte zentrieren
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colors.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.title3,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .width(thickness)
            .background(color)
    )
}

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
    thickness: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .height(thickness)
            .background(color)
    )
}

class PreviewExtractionDetailViewModel(val previewState: UiState) : ExtractionDetailViewModel(
    beanId = 1L,
    apiService = MockCoffeeApiService(),
) {
    init { _uiState.value = previewState }
    override fun loadExtractions() { /* Do nothing */ }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionDetailScreenPreviewRound() {
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = PreviewExtractionDetailViewModel(ExtractionDetailViewModel.UiState.Empty)
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionDetailScreenPreviewSquare() {
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = PreviewExtractionDetailViewModel(ExtractionDetailViewModel.UiState.Loading)
        )
    }
}

// Optional: Preview für den Ladezustand
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, name = "Loading State")
@Composable
fun ExtractionDetailScreenLoadingPreview() {
    val loadingViewModel = object : ExtractionDetailViewModel(beanId = 1L, apiService = MockCoffeeApiService()) {
        init { _uiState.value = UiState.Loading }
        override fun loadExtractions() { /* Do nothing */ }
    }
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = loadingViewModel
        )
    }
}