package com.swucollector.app.ui.playset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.data.db.model.PlaysetSummary
import com.swucollector.app.data.repository.CardRepository
import com.swucollector.app.ui.theme.SWUGreen

@Composable
fun PlaysetScreen(viewModel: PlaysetViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        PlaysetFilterRow(current = state.filter, onSelect = viewModel::setFilter)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.cards, key = { it.cardKey }) { card ->
                PlaysetCard(card)
            }
        }
    }
}

@Composable
private fun PlaysetFilterRow(current: PlaysetFilter, onSelect: (PlaysetFilter) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlaysetFilter.entries.forEach { filter ->
            FilterChip(
                selected = current == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun PlaysetCard(card: PlaysetSummary) {
    val threshold = CardRepository.playsetThreshold(card.type)
    val owned = card.totalOwned.coerceAtMost(threshold)
    val complete = card.totalOwned >= threshold
    val progress = if (threshold > 0) owned.toFloat() / threshold else 1f

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
                        text = card.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (card.subtitle != null) {
                        Text(
                            text = card.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "${card.totalOwned}/$threshold",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (complete) SWUGreen else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (complete) FontWeight.Bold else FontWeight.Normal
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                color = if (complete) SWUGreen else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
