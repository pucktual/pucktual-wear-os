package de.florianostertag.coffeehelper.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material3.CardColors
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme

object AppCardDefaults {
    @Composable
    fun cardColors(): CardColors =
        CardDefaults.cardColors(
            titleColor = MaterialTheme.colorScheme.primary
        )

    @Composable
    fun chipColors(): ChipColors =
        ChipDefaults.chipColors(
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
}