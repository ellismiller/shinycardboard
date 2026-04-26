package com.swucollector.app.ui.playset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swucollector.app.data.db.model.PlaysetSummary
import com.swucollector.app.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class PlaysetFilter { ALL, COMPLETE, INCOMPLETE }

data class PlaysetUiState(
    val cards: List<PlaysetSummary> = emptyList(),
    val filter: PlaysetFilter = PlaysetFilter.ALL
)

@HiltViewModel
class PlaysetViewModel @Inject constructor(
    cardRepository: CardRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(PlaysetFilter.ALL)

    val uiState: StateFlow<PlaysetUiState> = combine(
        cardRepository.getPlaysetSummary(),
        _filter
    ) { cards, filter ->
        val filtered = when (filter) {
            PlaysetFilter.ALL -> cards
            PlaysetFilter.COMPLETE -> cards.filter { card ->
                card.totalOwned >= CardRepository.playsetThreshold(card.type)
            }
            PlaysetFilter.INCOMPLETE -> cards.filter { card ->
                card.totalOwned < CardRepository.playsetThreshold(card.type)
            }
        }
        PlaysetUiState(cards = filtered, filter = filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaysetUiState()
    )

    fun setFilter(filter: PlaysetFilter) {
        _filter.value = filter
    }
}
