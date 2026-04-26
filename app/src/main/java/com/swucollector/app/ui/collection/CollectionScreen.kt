package com.swucollector.app.ui.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.data.db.model.PrintingWithCollection

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel = hiltViewModel(),
    onSync: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        SetFilterRow(
            sets = state.availableSets,
            selected = state.selectedSet,
            onSelect = viewModel::selectSet
        )
        if (state.printingsBySet.isEmpty()) {
            EmptyState(onSync = onSync)
        } else {
            PrintingList(
                printingsBySet = state.printingsBySet,
                onNormalQtyChange = viewModel::setNormalQty,
                onFoilQtyChange = viewModel::setFoilQty
            )
        }
    }
}

@Composable
private fun SetFilterRow(
    sets: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("All") }
            )
        }
        items(sets) { set ->
            FilterChip(
                selected = selected == set,
                onClick = { onSelect(set) },
                label = { Text(set) }
            )
        }
    }
}

@Composable
private fun PrintingList(
    printingsBySet: Map<String, List<PrintingWithCollection>>,
    onNormalQtyChange: (String, Int) -> Unit,
    onFoilQtyChange: (String, Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        printingsBySet.forEach { (setCode, printings) ->
            item(key = "header_$setCode") {
                Text(
                    text = setCode,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(printings, key = { it.printingId }) { printing ->
                PrintingCard(
                    printing = printing,
                    onNormalQtyChange = { onNormalQtyChange(printing.printingId, it) },
                    onFoilQtyChange = { onFoilQtyChange(printing.printingId, it) }
                )
            }
        }
    }
}

@Composable
private fun PrintingCard(
    printing: PrintingWithCollection,
    onNormalQtyChange: (Int) -> Unit,
    onFoilQtyChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = printing.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (printing.subtitle != null) {
                        Text(
                            text = printing.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${printing.setCode}-${printing.number}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RarityBadge(printing.rarity)
                if (printing.unique) {
                    RarityBadge("Unique")
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            QtyRow(label = "Normal", value = printing.normalQty, onValueChange = onNormalQtyChange)
            Spacer(Modifier.height(4.dp))
            QtyRow(label = "Foil", value = printing.foilQty, onValueChange = onFoilQtyChange)
        }
    }
}

@Composable
private fun RarityBadge(text: String) {
    val color = when (text.lowercase()) {
        "legendary" -> MaterialTheme.colorScheme.primary
        "rare" -> MaterialTheme.colorScheme.secondary
        "special" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color
    )
}

@Composable
private fun QtyRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(48.dp)
        )
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = value > 0
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease $label")
        }
        Text(
            text = "$value",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 32.dp)
        )
        IconButton(onClick = { onValueChange(value + 1) }) {
            Icon(Icons.Default.Add, contentDescription = "Increase $label")
        }
    }
}

@Composable
private fun EmptyState(onSync: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No cards yet", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Fetch card data to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSync) {
            Text("Sync Cards")
        }
    }
}
