package de.pucktual.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import de.pucktual.data.Bean
import de.pucktual.data.getMockBeans
import de.pucktual.presentation.theme.CoffeeHelperTheme

@Composable
fun BeanListEntry(
    bean: Bean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            if(bean.decaf) {
                StrikethroughCoffeeIcon(
                    tint = CardDefaults.cardColors().contentColor
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Coffee,
                    contentDescription = "triggers meditation action",
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column {
                Text(
                    text = bean.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = bean.manufacturer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview
@Composable
fun BeanListEntryPreview() {
    CoffeeHelperTheme {
        BeanListEntry(bean = getMockBeans().first(), onClick = {}, Modifier)
    }
}

@Preview
@Composable
fun BeanListEntryDecafPreview() {
    CoffeeHelperTheme {
        BeanListEntry(bean = getMockBeans().get(2), onClick = {}, Modifier)
    }
}
