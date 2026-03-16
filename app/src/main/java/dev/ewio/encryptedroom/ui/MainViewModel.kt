package dev.ewio.encryptedroom.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.ewio.encryptedroom.data.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Date

class MainViewModel(
    private val repository: DatabaseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        verifyOnOpen()
    }

    fun addBatch() {
        if (_uiState.value.isBusy) return

        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, statusMessage = "Adding 10,000 rows…") }

            runCatching {
                Log.d("INSERT", "starting insert")
                repository.addBatch()
            }
                .onSuccess { insertResult ->
                    _uiState.update {
                        it.copy(
                            isBusy = false,
                            totalRowsLabel = formatNumber(insertResult.totalRows),
                            lastBatchSizeLabel = formatNumber(insertResult.inserted),
                            statusMessage = "Inserted ${formatNumber(insertResult.inserted)} rows."
                        )
                    }
                    Log.d("INSERT", "Inserted ${formatNumber(insertResult.inserted)} rows.")
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isBusy = false,
                            statusMessage = throwable.message ?: "Insert failed"
                        )
                    }
                }
        }
    }

    fun verifyNow() {
        if (_uiState.value.isBusy) return

        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, statusMessage = "Verifying database…") }
            runCatching { repository.verifyAll() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isBusy = false,
                            totalRowsLabel = formatNumber(result.rowCount),
                            lastVerifiedAtLabel = Date().toString(),
                            verificationLabel = if (result.ok) {
                                "OK (${formatNumber(result.rowCount)} rows checked)"
                            } else {
                                "FAILED (${formatNumber(result.mismatchCount)} mismatches)"
                            },
                            statusMessage = if (result.ok) {
                                "Verification succeeded"
                            } else {
                                "Verification failed"
                            }
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isBusy = false,
                            statusMessage = throwable.message ?: "Verification failed"
                        )
                    }
                }
        }
    }

    private fun verifyOnOpen() {
        verifyNow()
    }

    private fun formatNumber(value: Int): String = NumberFormat.getIntegerInstance().format(value)
}

class MainViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = DatabaseRepository.create(context.applicationContext)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository) as T
    }
}

data class MainUiState(
    val isBusy: Boolean = false,
    val totalRowsLabel: String = "0",
    val lastBatchSizeLabel: String = "10,000",
    val verificationLabel: String = "Not verified yet",
    val lastVerifiedAtLabel: String = "Never",
    val statusMessage: String = "Ready",
)
