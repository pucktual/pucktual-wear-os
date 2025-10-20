package de.pucktual.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import de.pucktual.data.Bean
import de.pucktual.data.getMockBeans
import de.pucktual.presentation.theme.AppCardDefaults
import de.pucktual.presentation.theme.CoffeeHelperTheme

@Composable
fun BeanListEntry(
    bean: Bean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Chip(
        modifier = modifier,
        onClick = onClick,
        colors = AppCardDefaults.chipColors(),
        icon = {
            Icon(
                imageVector = Icons.Rounded.Coffee,
                contentDescription = "triggers meditation action",
            )
        },
        label = {
            Text(
                text = bean.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryLabel = {
            Text(
                text = bean.manufacturer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
}

@Preview
@Composable
fun BeanListEntryPreview() {
    CoffeeHelperTheme {
        BeanListEntry(bean = getMockBeans().first(), onClick = {}, Modifier)
    }
}
