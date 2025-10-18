package de.florianostertag.coffeehelper.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.tooling.preview.devices.WearDevices
import de.florianostertag.coffeehelper.data.getMockBeans
import de.florianostertag.coffeehelper.presentation.theme.CoffeeHelperTheme

@Composable
fun BeanListScreen(
    viewModel: BeanListViewModel = viewModel(),
    onBeanSelected: (Long) -> Unit // Callback, wenn eine Bohne ausgewählt wird
) {
    val uiState by viewModel.uiState.collectAsState()

    // Scaffolding für die Wear OS Ansicht (Vignette, Steuerung des Scroll-Effekts)
    val listState = rememberScalingLazyListState()

    Scaffold(
        // Die Ränder für runde Uhren
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        // Anzeige der aktuellen Zeit/Status
        timeText = { TimeText() }
    ) {
        when (val state = uiState) {
            is BeanListViewModel.UiState.Loading -> {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is BeanListViewModel.UiState.Error -> {
                Text(state.message, style = MaterialTheme.typography.caption1)
            }
            is BeanListViewModel.UiState.Success -> {
                // ScalingLazyColumn ist die empfohlene Komponente für scrollbare Listen
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = 32.dp, // Platz für TimeText
                        bottom = 32.dp
                    )
                ) {
                    item { ListHeader { Text("Meine Bohnen") } }

                    items(state.beans) { bean ->
                        Chip(
                            onClick = { onBeanSelected(bean.id) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(bean.name) },
                            secondaryLabel = { Text(bean.manufacturer) },
                            //icon = { Icon(Icons.Filled.Coffee, contentDescription = "Bohne") } // Beispiel-Icon
                        )
                    }
                }
            }
        }
    }
}

class PreviewBeanListViewModel : BeanListViewModel() {
    init {
        _uiState.value = UiState.Success(getMockBeans())
    }
    override fun loadBeans() { /* Do nothing */ }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun BeanListScreenPreview() {
    CoffeeHelperTheme {
        BeanListScreen(
            viewModel = PreviewBeanListViewModel(),
            onBeanSelected = { id -> println("Bean selected: $id") }
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun BeanListScreenLoadingPreview() {
    val loadingViewModel = object : BeanListViewModel() {
        init { _uiState.value = UiState.Loading }
        override fun loadBeans() { /* Do nothing */ }
    }
    CoffeeHelperTheme {
        BeanListScreen(
            viewModel = loadingViewModel,
            onBeanSelected = { /* ... */ }
        )
    }
}