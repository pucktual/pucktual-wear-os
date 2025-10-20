package de.pucktual.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.tooling.preview.devices.WearDevices
import de.pucktual.api.CoffeeApiService
import de.pucktual.data.Bean
import de.pucktual.data.Extraction
import de.pucktual.data.LoginRequest
import de.pucktual.data.LoginResponse
import de.pucktual.data.getMockBeans
import de.pucktual.presentation.theme.CoffeeHelperTheme

@Composable
fun BeanListScreen(
    viewModel: BeanListViewModel = viewModel(),
    onBeanSelected: (Long) -> Unit // Callback, wenn eine Bohne ausgewählt wird
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLoginDialog by remember { mutableStateOf(false) }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()


    ScreenScaffold(
        scrollState = listState
    ) {
        when (val state = uiState) {
            is BeanListViewModel.UiState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is BeanListViewModel.UiState.Unauthorized -> {
                showLoginDialog = true
                // Zeige weiterhin den Ladekreis, bis der Login erfolgreich ist
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is BeanListViewModel.UiState.Error -> {
                Text(state.message, style = MaterialTheme.typography.caption1)
            }
            is BeanListViewModel.UiState.Success -> {
                TransformingLazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(top = 32.dp, bottom = 32.dp, start = 8.dp, end = 8.dp)
                ) {
                    item {
                        ListHeader(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec)
                        ) { Text(text = "Meine Bohnen") }
                    }
                    items(state.beans) { bean ->
                        BeanListEntry(bean, onClick = { onBeanSelected(bean.id) }, Modifier
                            .fillMaxWidth())
                    }
                }
            }
        }

        if (showLoginDialog) {
            LoginDialog(
                // Wird ausgelöst, wenn der Login erfolgreich ist
                onLoginSuccess = {
                    showLoginDialog = false
                    viewModel.loadBeans() // Erneuter Versuch, die Daten abzurufen
                },
                // Wird ausgelöst, wenn der Nutzer den Login abbricht
                onCancel = {
                    showLoginDialog = false
                    // Optional: setze den Zustand auf Error, um den Abbruch anzuzeigen
                }
            )
        }
    }
}

class MockCoffeeApiService : CoffeeApiService {
    override suspend fun getAllBeans(): List<Bean> {
        return getMockBeans()
    }

    override suspend fun getExtractionsForBean(beanId: Long): List<Extraction> = throw NotImplementedError()
    override suspend fun login(request: LoginRequest): LoginResponse = throw NotImplementedError()
}

class PreviewBeanListViewModel : BeanListViewModel(apiService = MockCoffeeApiService()) {
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
    val loadingViewModel = object : BeanListViewModel(apiService = MockCoffeeApiService()) {
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