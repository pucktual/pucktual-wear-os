package de.florianostertag.coffeehelper.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun UrlSetupScreenForm(openUrlInput: () -> Unit,
                       errorMessage: String? = null,
) {
    val listState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = listState,
        edgeButtonSpacing = 15.dp,
        edgeButton = {
            EdgeButton(
                onClick = openUrlInput,
                modifier =
                    Modifier.scrollable(
                        listState,
                        orientation = Orientation.Vertical,
                        reverseDirection = true,
                        overscrollEffect = rememberOverscrollEffect(),
                    ),
            ) {
                val label = if (errorMessage == null) {
                    "Starten"
                } else {
                    "Ändern"
                }
                Text(label)
            }
        },
    ) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            item { ListHeader { Text("Konfiguration") } }
            if(errorMessage == null) {
                item {
                    Text("Gib die URL ein, unter der Coffee-Helper erreichbar ist.",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                item {
                    Text(
                        errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun UrlSetupScreenFormPreview() {
    UrlSetupScreenForm(openUrlInput = {})
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun UrlSetupScreenFormPreviewWithError() {
    UrlSetupScreenForm(openUrlInput = {}, "Die URL muss mit https:// beginnen.")
}