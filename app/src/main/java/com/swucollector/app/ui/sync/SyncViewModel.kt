package com.swucollector.app.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swucollector.app.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    init {
        viewModelScope.launch {
            if (cardRepository.isDatabaseEmpty()) {
                performSync()
            }
        }
    }

    fun triggerSync() {
        viewModelScope.launch { performSync() }
    }

    fun clearError() {
        _syncError.value = null
    }

    private suspend fun performSync() {
        _isSyncing.value = true
        _syncError.value = null
        runCatching { cardRepository.syncAll() }
            .onFailure { _syncError.value = it.message ?: "Sync failed" }
        _isSyncing.value = false
    }
}
