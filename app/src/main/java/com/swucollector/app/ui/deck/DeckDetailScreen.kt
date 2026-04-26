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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.data.db.model.DeckCardWithIdentity
import com.swucollector.app.data.db.model.DeckWithCards

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deckId: Long,
    onBack: () -> Unit,
    viewModel: DeckViewModel = hiltViewModel()
) {
    LaunchedEffect(deckId) { viewModel.openDeck(deckId) }

    val deckWithCards by viewModel.currentDeckWithCards.collectAsStateWithLifecycle()
    val cards by viewModel.currentDeckCards.collectAsStateWithLifecycle()
    val totalCards = cards.sumOf { it.quantity }
    var showCardPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deckWithCards?.deck?.name ?: "Deck") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCardPicker = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add cards")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            deckWithCards?.let { dwc -> DeckHeader(dwc, totalCards) }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            if (cards.isEmpty()) {
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No cards added yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap + to add cards",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(cards, key = { it.cardKey }) { card ->
                        DeckCardRow(
                            card = card,
                            totalCards = totalCards,
                            onQtyChange = { qty -> viewModel.setCardQty(deckId, card.cardKey, qty) }
                        )
                    }
                }
            }
        }
    }

    if (showCardPicker) {
        DeckCardPickerDialog(
            deckId = deckId,
            currentCards = cards,
            totalCards = totalCards,
            viewModel = viewModel,
            onDismiss = { showCardPicker = false }
        )
    }
}

@Composable
private fun DeckHeader(dwc: DeckWithCards, totalCards: Int) {
    Column(Modifier.padding(vertical = 8.dp)) {
        LabelValue("Leader", dwc.deck.leaderCardKey)
        Spacer(Modifier.height(4.dp))
        LabelValue("Base", dwc.deck.baseCardKey)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Cards",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "$totalCards / 50",
                style = MaterialTheme.typography.bodyMedium,
                color = if (totalCards > 50) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DeckCardRow(
    card: DeckCardWithIdentity,
    totalCards: Int,
    onQtyChange: (Int) -> Unit
) {
    val maxQty = if (card.unique) 1 else 3
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    card.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (card.subtitle != null) {
                    Text(
                        card.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = { onQtyChange(card.quantity - 1) },
                enabled = card.quantity > 0
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Remove")
            }
            Text(
                "${card.quantity}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 28.dp)
            )
            IconButton(
                onClick = { onQtyChange(card.quantity + 1) },
                enabled = card.quantity < maxQty && totalCards < 50
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
private fun DeckCardPickerDialog(
    deckId: Long,
    currentCards: List<DeckCardWithIdentity>,
    totalCards: Int,
    viewModel: DeckViewModel,
    onDismiss: () -> Unit
) {
    val pickerQuery by viewModel.pickerQuery.collectAsStateWithLifecycle()
    val allCards by viewModel.pickerCards.collectAsStateWithLifecycle()
    val currentByKey = currentCards.associateBy { it.cardKey }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Cards ($totalCards/50)") },
        text = {
            Column {
                OutlinedTextField(
                    value = pickerQuery,
                    onValueChange = viewModel::setPickerQuery,
                    label = { Text("Search cards") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.height(360.dp)) {
                    items(allCards, key = { it.cardKey }) { card ->
                        val currentQty = currentByKey[card.cardKey]?.quantity ?: 0
                        val maxQty = if (card.unique) 1 else 3
                        val canAdd = currentQty < maxQty && totalCards < 50
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    card.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (card.subtitle != null) {
                                    Text(
                                        card.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (currentQty > 0)
                                        viewModel.setCardQty(deckId, card.cardKey, currentQty - 1)
                                },
                                enabled = currentQty > 0
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Remove")
                            }
                            Text(
                                "$currentQty",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(min = 28.dp)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.setCardQty(deckId, card.cardKey, currentQty + 1)
                                },
                                enabled = canAdd
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
