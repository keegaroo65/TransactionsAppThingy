package com.example.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.transactions.data.AppContainer
import com.example.transactions.data.DataStoreManager
import com.example.transactions.data.HistoryRepository
import com.example.transactions.data.Recurring
import com.example.transactions.data.RecurringRepository
import com.example.transactions.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val TAG = "MainViewModel"

class MainViewModel(
    val historyRepository: HistoryRepository,
    val recurringRepository: RecurringRepository,
    val dataStore: DataStoreManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainUiState()
    )
    val uiState = _uiState.asStateFlow()

    fun showDeleteButton() {
        _uiState.update {
            it.copy(
                deleteButtonShown = true
            )
        }
    }

    fun hideDeleteButton() {
        _uiState.update {
            it.copy(
                deleteButtonShown = false
            )
        }
    }

    fun getTransactionAsync(transactionId: Int) {
        viewModelScope.launch {
            val transaction = historyRepository.getTransaction(transactionId)

            _uiState.update { currentState ->
                currentState.copy(
                    editTransaction = transaction
                )
            }
        }
    }

    fun getRecurringAsync(recurringId: Int) {
        viewModelScope.launch {
            val recurring = recurringRepository.getRecurring(recurringId)

            _uiState.update { currentState ->
                currentState.copy(
                    viewRecurring = recurring
                )
            }
        }
    }

    // TODO: make all calls to this function instead of independent on new nav composable,
    //  connected to the edit transaction page being left somehow?
    fun clearEditTransaction() {
        _uiState.update {
            it.copy(
                editTransaction = null,
                viewRecurring = null
            )
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            historyRepository.deleteAllTransactions()
        }
    }

    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                val container: AppContainer = (application as TransactionsApplication).container

                return MainViewModel(
                    container.historyRepository,
                    container.recurringRepository,
                    container.dataStore,
                    savedStateHandle
                ) as T
            }
        }
    }
}

data class MainUiState(
    val deleteButtonShown: Boolean = false,
    val editTransaction: Transaction? = null,
    val viewRecurring: Recurring? = null
)