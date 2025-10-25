package de.pucktual.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon

@Composable
fun StrikethroughCoffeeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.LightGray,
) {
    Box(modifier = modifier.size(size)) {
        Icon(
            imageVector = Icons.Rounded.Coffee,
            contentDescription = "Kaffee nicht optimal",
            modifier = Modifier.fillMaxSize(),
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.toPx() / 10f

            drawLine(
                color = tint,
                start = Offset(0f, 0f),
                end = Offset(size.toPx(), size.toPx()),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun StrikethroughCoffeeIconPreview() {
    StrikethroughCoffeeIcon()
}