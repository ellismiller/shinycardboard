package com.swucollector.app.ui.setcompletion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swucollector.app.data.db.model.SetCompletionSummary
import com.swucollector.app.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SetCompletionViewModel @Inject constructor(
    cardRepository: CardRepository
) : ViewModel() {

    val sets: StateFlow<List<SetCompletionSummary>> = cardRepository.getSetCompletion()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
