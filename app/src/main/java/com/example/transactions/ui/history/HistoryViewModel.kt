package com.example.transactions.ui.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactions.data.HistoryRepository
import com.example.transactions.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HistoryViewModel"

class HistoryViewModel (
    historyRepository: HistoryRepository
) : ViewModel() {
    val _uiState = MutableStateFlow(
        HistoryUiState()
    )
    val uiState = _uiState.asStateFlow()

    // Get transaction history update stream from the Room repository
    private val _transactionList = historyRepository.getAllTransactionsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )
        // Update _uiState when the transaction history is modified or initialized
        .also { stateFlow ->
            viewModelScope.launch {
                stateFlow.collect { transactionList ->
                    _uiState.update { currentState ->
                        Log.d(TAG, "stupidness !!!!!! ${currentState.transactionList.count()} -> ${transactionList.count()}")
                        currentState.copy(
                            transactionList = transactionList,
                            selectedTransactions = List(transactionList.count()) { false },
                            anySelected = false
                        )
                    }
                }
            }
        }

    fun selectTransaction(tranId: Int): Boolean {
        var newAnySelected = false
        _uiState.update {
            val newSelected = it.selectedTransactions.toMutableList()
            newSelected[tranId] = !newSelected[tranId]

            // Set anySelected to true if the current transaction is now selected, or if any other is
            newAnySelected = newSelected[tranId] or newSelected.contains(true)

            Log.d(TAG, "newAnySelected $newAnySelected")

            it.copy(
                selectedTransactions = newSelected.toList(),
                anySelected = newAnySelected
            )
        }

        Log.d(TAG, "returning newAnySelected $newAnySelected")

        return newAnySelected
    }

    fun deselectAll() {
        _uiState.update {
            Log.d(TAG, "newAnySelected false*")

            it.copy(
                selectedTransactions = List(it.transactionList.count()) { false },
                anySelected = false
            )
        }
    }

    fun updateDeletePrompt(promptDeleteSelected: Boolean) {
        _uiState.update {
            it.copy(
                promptDeleteSelected = promptDeleteSelected
            )
        }
    }

    fun transactionsToDelete(): List<Transaction> {
        val state = uiState.value

        val toDelete = mutableListOf<Transaction>()

        state.selectedTransactions.forEachIndexed { index, value ->
            if (value) {
                toDelete.add(state.transactionList[index])
            }
        }

        return toDelete
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        // Define ViewModel factory in a companion object

//        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel> create(
//                modelClass: Class<T>,
//                extras: CreationExtras
//            ): T {
//                // Get the Application object from extras
//                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
//                // Create a SavedStateHandle for this ViewModel from extras
//                val savedStateHandle = extras.createSavedStateHandle()
//
//                val container: AppContainer = (application as TransactionsApplication).container
//
//                return HistoryViewModel(
//                    container.historyRepository
//                ) as T
//            }
//        }
    }
}

data class HistoryUiState(
    val transactionList: List<Transaction> = listOf(),
    val selectedTransactions: List<Boolean> = listOf(),
    val anySelected: Boolean = false,
    val promptDeleteSelected: Boolean = false
)