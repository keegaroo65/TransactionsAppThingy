package com.example.transactions.ui.recurring

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactions.Utility
import com.example.transactions.data.Recurring
import com.example.transactions.data.RecurringRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "RecurringViewModel"

class RecurringViewModel(
    val recurringRepository: RecurringRepository
) : ViewModel() {
    val _uiState = MutableStateFlow(
        RecurringUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _recurringList = recurringRepository.getAllRecurringsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(RecurringViewModel.TIMEOUT_MILLIS),
            initialValue = listOf()
        )
        // Update _uiState when the transaction history is modified or initialized
        .also { stateFlow ->
            viewModelScope.launch {
                stateFlow.collect { recurringList ->
                    _uiState.update { currentState ->
                        var updated = false

                        if (!currentState.allUpdated && recurringList.isNotEmpty()) {
                            updateAllNextCharges()

                            updated = true
                        }
//                        else {
//                            Log.d(TAG, "attempted but didn't updateAllNextCharges ${currentState.allUpdated} ${recurringList.count()}")
//                        }

                        currentState.copy(
                            recurringList = recurringList,
                            allUpdated = if (currentState.allUpdated) true else updated
//                            selectedTransactions = List(transactionList.count()) { false },
//                            anySelected = false
                        )
                    }
                }
            }
        }

    fun updateNextCharge(recurring: Recurring) {
        viewModelScope.launch {
            recurringRepository.updateRecurring(
                recurring.copy(
                    nextCharge = Utility.nextCharge(recurring)
                )
            )
        }
    }

    fun updateAllNextCharges() {
        Log.d(TAG, "updateAllNextCharges ${_recurringList.value.count()}")
        for (recurring in _recurringList.value) {
            updateNextCharge(recurring)
        }
    }

//    fun resetTimestamps() {
//        viewModelScope.launch {
//            for (recurring in _recurringList.value) {
//                recurringRepository.updateRecurring(
//                    recurring.copy(
//                        firstCharge = 1704088800000,
//                        lastCharge = 1704088800000
//                    )
//                )
//            }
//        }
//    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RecurringUiState(
    val recurringList: List<Recurring> = listOf(),//Collections.nCopies(1000, Recurring(0,15.0,"title",null,true,0,5,0L,0L,0L)),
    val allUpdated: Boolean = false
)