package com.swucollector.app.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swucollector.app.data.db.model.PrintingWithCollection
import com.swucollector.app.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionUiState(
    val printingsBySet: Map<String, List<PrintingWithCollection>> = emptyMap(),
    val selectedSet: String? = null,
    val availableSets: List<String> = emptyList()
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _selectedSet = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CollectionUiState> = combine(
        cardRepository.getPrintingsWithCollection(),
        _selectedSet
    ) { printings, selectedSet ->
        val bySet = printings.groupBy { it.setCode }
        CollectionUiState(
            printingsBySet = if (selectedSet != null) bySet.filterKeys { it == selectedSet } else bySet,
            selectedSet = selectedSet,
            availableSets = bySet.keys.sorted()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CollectionUiState()
    )

    fun selectSet(setCode: String?) {
        _selectedSet.value = setCode
    }

    fun setNormalQty(printingId: String, qty: Int) {
        viewModelScope.launch { cardRepository.setNormalQty(printingId, qty) }
    }

    fun setFoilQty(printingId: String, qty: Int) {
        viewModelScope.launch { cardRepository.setFoilQty(printingId, qty) }
    }
}
