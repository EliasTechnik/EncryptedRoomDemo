package dev.ewio.encryptedroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.ewio.encryptedroom.ui.MainScreen
import dev.ewio.encryptedroom.ui.MainViewModel
import dev.ewio.encryptedroom.ui.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        System.loadLibrary("sqlcipher")

        setContent {
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        uiState = uiState,
                        onAddBatch = viewModel::addBatch,
                        onVerify = viewModel::verifyNow
                    )
                }
            }
        }
    }
}
