package de.pucktual.ui.common

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun ErrorScreen(message: String, onClick: (() -> Unit)? = null, onClickLabel: String? = null)
{
    val listState = rememberTransformingLazyColumnState()

    if(onClick == null) {
        ScreenScaffold(scrollState = listState) {
            contentPadding -> ErrorScreenContent(contentPadding, listState, message)
        }
    } else {
        ScreenScaffold(
            scrollState = listState,
            edgeButtonSpacing = 15.dp,
            edgeButton = {
                EdgeButton(
                    onClick = onClick,
                    modifier =
                        Modifier.scrollable(
                            listState,
                            orientation = Orientation.Vertical,
                            reverseDirection = true,
                            overscrollEffect = rememberOverscrollEffect(),
                        ),
                ) {
                    Text(text = onClickLabel ?: "Ok")
                }
            }
        ) {
            contentPadding -> ErrorScreenContent(contentPadding, listState, message)
        }
    }

}

@Composable
fun ErrorScreenContent(
    contentPadding: PaddingValues,
    listState: TransformingLazyColumnState,
    message: String
) {
    TransformingLazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item { ListHeader { Text("Konfiguration") } }
        item {
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview(device = WearDevices.SMALL_ROUND)
@Composable
fun ErrorScreenPreview() {
    ErrorScreen("Bohnen konnten nicht gelanden werden.")
}

@Preview(device = WearDevices.SMALL_ROUND)
@Composable
fun ErrorScreenPreviewWithButton() {
    ErrorScreen("Bohnen konnten nicht gelanden werden.", onClick = { }, onClickLabel = "Erneut laden")
}
