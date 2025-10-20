package de.pucktual.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
@Composable
fun CoffeeHelperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}