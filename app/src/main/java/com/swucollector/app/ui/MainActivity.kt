package com.swucollector.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swucollector.app.ui.sync.SyncViewModel
import com.swucollector.app.ui.theme.ShinyCardboardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val syncViewModel: SyncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShinyCardboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isSyncing by syncViewModel.isSyncing.collectAsStateWithLifecycle()
                    val syncError by syncViewModel.syncError.collectAsStateWithLifecycle()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(syncError) {
                        if (syncError != null) {
                            snackbarHostState.showSnackbar("Sync failed: $syncError")
                            syncViewModel.clearError()
                        }
                    }

                    Column(Modifier.fillMaxSize()) {
                        if (isSyncing) {
                            LinearProgressIndicator(Modifier.fillMaxWidth())
                        }
                        AppNavigation()
                    }

                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}
