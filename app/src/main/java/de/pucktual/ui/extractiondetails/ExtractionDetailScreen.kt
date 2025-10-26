package de.pucktual.ui.extractiondetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.PagerScaffoldDefaults
import androidx.wear.tooling.preview.devices.WearDevices
import de.pucktual.data.Extraction
import de.pucktual.data.getMockExtractions
import de.pucktual.presentation.theme.CoffeeHelperTheme
import de.pucktual.ui.beanlist.MockCoffeeApiService

@Composable
fun ExtractionDetailScreen(
    viewModel: ExtractionDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })


    when (val state = uiState) {
        is ExtractionDetailViewModel.UiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        is ExtractionDetailViewModel.UiState.Error -> {
            Text(state.message, style = MaterialTheme.typography.titleLarge)
        }

        ExtractionDetailViewModel.UiState.Empty -> {
            Text("Keine Extraktionen für diese Bohne gespeichert.")
        }

        is ExtractionDetailViewModel.UiState.Success -> {
            val latestExtraction = state.extractions.first()
            CustomRingScaffold(
                flowColor = Color(0xFF4CAF50),
                profileColor = Color(0xFF03A9F4),
                isFlowPerfect = true,
                isProfilePerfect = true
            ) {
                HorizontalPagerScaffold(
                    pagerState = pagerState,
                ) {
                    HorizontalPager(
                        state = pagerState,
                        flingBehavior = PagerScaffoldDefaults.snapWithSpringFlingBehavior(state = pagerState),
                        modifier = Modifier.fillMaxSize().offset(y = (-10).dp)
                    ) { page ->
                        when (page) {
                            0 -> {
                                AnimatedPage(pageIndex = 0, pagerState = pagerState) {
                                    PreparationPage(latestExtraction)
                                }
                            }
                            1 -> {
                                AnimatedPage(pageIndex = 1, pagerState = pagerState) {
                                    ExtractionPage(latestExtraction)
                                }
                            }
                            2 -> {
                                AnimatedPage(pageIndex = 2, pagerState = pagerState) {
                                    HintPage(latestExtraction)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreparationPage(extraction: Extraction) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FirstExtractionMetric(
            value = String.format("%.1f", extraction.inputGrams),
            label = "In",
            icon = Icons.Filled.Balance,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        SecondExtractionMetric(
            value = extraction.grind?.toString() ?: "N/A",
            label = "Mahlgrad",
            icon = Icons.Filled.Settings,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
fun ExtractionPage(extraction: Extraction) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        FirstExtractionMetric(
            value = extraction.time.toString(),
            label = "Zeit",
            icon = Icons.Filled.Watch,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        SecondExtractionMetric(
            value = extraction.outputGrams.toString(),
            label = "Out",
            icon = Icons.Filled.LocalCafe,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
fun HintPage(extraction: Extraction) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Info, contentDescription = "Hinweis Icon")
        Text(
            text = "Für die nächste Extraktion:",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = extraction.nextExtractionHint?.takeIf { it.isNotBlank() } ?: "Perfektes Ergebnis! Beibehalten.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FirstExtractionMetric(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    val secondaryBackground = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .padding(all = 10.dp)
            .rotate(-20.0f)
            .offset(x = (5).dp, y = 30.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, contentDescription = label,
                modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = secondaryBackground,
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }.padding(10.dp, bottom = 5.dp, top = 5.dp, end = 35.dp),
                text = value,
                style = MaterialTheme.typography.numeralMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun SecondExtractionMetric(value: String, label: String, icon: ImageVector, modifier: Modifier = Modifier){
    val primaryBackground = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .padding(all = 10.dp)
            .rotate(-20.0f)
            .offset(y = (-10).dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .drawBehind{
                        drawRoundRect(
                            color = primaryBackground,
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }.padding(30.dp, bottom = 5.dp, top = 5.dp, end=10.dp)
                ,
                text = value,
                style = MaterialTheme.typography.numeralMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(3.dp))
            Icon(
                icon, contentDescription = label,
                modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary
            )
        }

    }
}


@Composable
fun CustomRingScaffold(
    flowColor: Color,
    profileColor: Color,
    isFlowPerfect: Boolean,
    isProfilePerfect: Boolean,
    ringThickness: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    val thicknessPx = with(LocalDensity.current) { ringThickness.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val center = Offset(size.width / 2f, size.height / 2f)
                val strokeStyle = Stroke(width = thicknessPx)

                val flowBrush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.0f to flowColor,      // 0° (12 Uhr)
                        0.2f to flowColor,     // 54° (Fokus nach 12 Uhr)
                        0.5f to Color.Transparent,     // 180° (6 Uhr): Bleibt transparent
                        0.8f to flowColor,     // 306° (Fokus vor 12 Uhr)
                        1.0f to flowColor       // 360° (Zurück zu 12 Uhr)
                    ),
                    center = center
                )

                val profileBrush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.2f to Color.Transparent,    // 90° (3 Uhr): Fadet zu transparent
                        0.3f to profileColor,      // 126° (Farbe beginnt)
                        0.5f to profileColor,       // 180° (6 Uhr): Reine Farbe
                        0.7f to profileColor,      // 234° (Farbe endet)
                        0.8f to Color.Transparent,    // 270° (9 Uhr): Fadet zu transparent
                        1.0f to Color.Transparent
                    ),
                    center = center
                )

                if (!isFlowPerfect) {
                    drawArc(
                        brush = flowBrush,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        size = size,
                        style = strokeStyle
                    )
                }

                if (!isProfilePerfect) {
                    drawArc(
                        brush = profileBrush,
                        startAngle = 0f,    // Starte bei 12 Uhr
                        sweepAngle = 360f,  // Zeichne den gesamten Kreis
                        useCenter = false,
                        size = size,
                        style = strokeStyle
                    )
                }
            }
    ) {
        content()
    }
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
fun PreparationPagePreview() {
    CoffeeHelperTheme {
        PreparationPage(extraction = getMockExtractions().first())
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionPagePreview() {
    CoffeeHelperTheme {
        ExtractionPage(extraction = getMockExtractions().first())
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun HintPagePreview() {
    CoffeeHelperTheme {
        HintPage(extraction = getMockExtractions().first())
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionDetailScreenPreview() {
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = PreviewExtractionDetailViewModel(ExtractionDetailViewModel.UiState.Success(getMockExtractions()))
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionDetailScreenPreviewEmpty() {
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = PreviewExtractionDetailViewModel(ExtractionDetailViewModel.UiState.Empty)
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ExtractionDetailScreenPreviewLoading() {
    CoffeeHelperTheme {
        ExtractionDetailScreen(
            viewModel = PreviewExtractionDetailViewModel(ExtractionDetailViewModel.UiState.Loading)
        )
    }
}