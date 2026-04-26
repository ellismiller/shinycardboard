package com.swucollector.app.ui.setcompletion

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.data.db.model.SetCompletionSummary
import com.swucollector.app.ui.theme.SWUGreen
import kotlin.math.roundToInt

@Composable
fun SetCompletionScreen(viewModel: SetCompletionViewModel = hiltViewModel()) {
    val sets by viewModel.sets.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(sets, key = { it.setCode }) { set ->
            SetCard(set)
        }
    }
}

@Composable
private fun SetCard(set: SetCompletionSummary) {
    val progress = if (set.totalPrintings > 0)
        set.ownedPrintings.toFloat() / set.totalPrintings
    else 0f
    val pct = (progress * 100).roundToInt()
    val complete = set.ownedPrintings >= set.totalPrintings && set.totalPrintings > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = set.setCode,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${set.ownedPrintings} / ${set.totalPrintings}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (complete) SWUGreen else MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$pct% collected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = if (complete) SWUGreen else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
