package com.swucollector.app.ui.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swucollector.app.data.db.entity.CardIdentityEntity
import com.swucollector.app.data.db.entity.DeckEntity
import com.swucollector.app.data.db.model.DeckCardWithIdentity
import com.swucollector.app.data.db.model.DeckWithCards
import com.swucollector.app.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val allDecks: StateFlow<List<DeckEntity>> = deckRepository.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Card picker search query
    private val _pickerQuery = MutableStateFlow("")
    val pickerQuery: StateFlow<String> = _pickerQuery

    @OptIn(ExperimentalCoroutinesApi::class)
    val pickerCards: StateFlow<List<CardIdentityEntity>> = _pickerQuery
        .flatMapLatest { query -> deckRepository.searchCards(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current deck for detail screen — initialised from nav arg when available
    private val _currentDeckId = MutableStateFlow<Long?>(savedStateHandle.get<Long>("deckId"))

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDeckWithCards: StateFlow<DeckWithCards?> = _currentDeckId
        .flatMapLatest { id ->
            if (id != null) deckRepository.getDeckWithCards(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDeckCards: StateFlow<List<DeckCardWithIdentity>> = _currentDeckId
        .flatMapLatest { id ->
            if (id != null) deckRepository.getDeckCardsWithIdentity(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openDeck(deckId: Long) {
        _currentDeckId.value = deckId
    }

    fun createDeck(name: String, leaderCardKey: String, baseCardKey: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = deckRepository.createDeck(name, leaderCardKey, baseCardKey)
            onCreated(id)
        }
    }

    fun deleteDeck(deck: DeckEntity) {
        viewModelScope.launch { deckRepository.deleteDeck(deck) }
    }

    fun renameDeck(deckId: Long, newName: String) {
        viewModelScope.launch { deckRepository.renameDeck(deckId, newName) }
    }

    fun updateLeader(deckId: Long, cardKey: String) {
        viewModelScope.launch { deckRepository.updateLeader(deckId, cardKey) }
    }

    fun updateBase(deckId: Long, cardKey: String) {
        viewModelScope.launch { deckRepository.updateBase(deckId, cardKey) }
    }

    fun setCardQty(deckId: Long, cardKey: String, qty: Int) {
        viewModelScope.launch { deckRepository.setCardQuantity(deckId, cardKey, qty) }
    }

    fun setPickerQuery(query: String) {
        _pickerQuery.value = query
    }

    suspend fun getLeaders(): List<CardIdentityEntity> = deckRepository.getLeaders()
    suspend fun getBases(): List<CardIdentityEntity> = deckRepository.getBases()
}
