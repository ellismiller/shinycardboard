package com.swucollector.app.ui.deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
// rememberCoroutineScope not needed
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.data.db.entity.CardIdentityEntity
import com.swucollector.app.data.db.entity.DeckEntity
import kotlinx.coroutines.launch

@Composable
fun DeckListScreen(
    onDeckClick: (Long) -> Unit,
    viewModel: DeckViewModel = hiltViewModel()
) {
    val decks by viewModel.allDecks.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New deck")
            }
        }
    ) { padding ->
        if (decks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No decks yet", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap + to create your first deck",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 12.dp, end = 12.dp,
                    top = 12.dp + padding.calculateTopPadding(),
                    bottom = 80.dp + padding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(decks, key = { it.deckId }) { deck ->
                    DeckListItem(
                        deck = deck,
                        onClick = { onDeckClick(deck.deckId) },
                        onDelete = { viewModel.deleteDeck(deck) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateDeckDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onCreated = { deckId ->
                showCreateDialog = false
                onDeckClick(deckId)
            }
        )
    }
}

@Composable
private fun DeckListItem(
    deck: DeckEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(deck.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Leader: ${deck.leaderCardKey}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Base: ${deck.baseCardKey}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete deck")
            }
        }
    }
}

@Composable
private fun CreateDeckDialog(
    viewModel: DeckViewModel,
    onDismiss: () -> Unit,
    onCreated: (Long) -> Unit
) {
    var step by remember { mutableStateOf(0) }
    var deckName by remember { mutableStateOf("") }
    var leaders by remember { mutableStateOf<List<CardIdentityEntity>>(emptyList()) }
    var bases by remember { mutableStateOf<List<CardIdentityEntity>>(emptyList()) }
    var selectedLeader by remember { mutableStateOf<CardIdentityEntity?>(null) }

    LaunchedEffect(Unit) {
        leaders = viewModel.getLeaders()
        bases = viewModel.getBases()
    }

    when (step) {
        0 -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("New Deck") },
            text = {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Deck name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = { if (deckName.isNotBlank()) step = 1 },
                    enabled = deckName.isNotBlank()
                ) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )
        1 -> CardPickerDialog(
            title = "Select Leader",
            cards = leaders,
            onDismiss = onDismiss,
            onSelect = { leader ->
                selectedLeader = leader
                step = 2
            }
        )
        2 -> CardPickerDialog(
            title = "Select Base",
            cards = bases,
            onDismiss = onDismiss,
            onSelect = { base ->
                viewModel.createDeck(
                    name = deckName,
                    leaderCardKey = selectedLeader!!.cardKey,
                    baseCardKey = base.cardKey,
                    onCreated = onCreated
                )
            }
        )
    }
}

@Composable
private fun CardPickerDialog(
    title: String,
    cards: List<CardIdentityEntity>,
    onDismiss: () -> Unit,
    onSelect: (CardIdentityEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(Modifier.height(320.dp)) {
                items(cards, key = { it.cardKey }) { card ->
                    TextButton(
                        onClick = { onSelect(card) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(card.name, fontWeight = FontWeight.Bold)
                            if (card.subtitle != null) {
                                Text(
                                    card.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
