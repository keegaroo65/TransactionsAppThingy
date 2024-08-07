package com.example.transactions.ui.newRecurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.transactions.TransactionsApplication
import com.example.transactions.data.AppContainer
import com.example.transactions.data.DataStoreManager
import com.example.transactions.data.Recurring
import com.example.transactions.data.RecurringRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewRecurringViewModel(
    val recurringRepository: RecurringRepository,
    dataStore: DataStoreManager,
    recurring: Recurring?
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        NewRecurringUiState(
            "",
            "",
            null,
            true,
            0,
            0, // TODO: current day of month
            0L,
            0L,
            false,
            false
        )
    )
    val uiState = _uiState.asStateFlow()

    fun changeType(type: Int) {
        _uiState.update {
            it.copy(
                type = type
            )
        }
    }

    fun changeTitleText(title: String) {
        _uiState.update { currentState ->
            currentState.copy( // TODO: remove start + end whitespace, possibly double spaces
                titleText = title.replace(";", ""),
                validPurchase = title.isNotEmpty()
            )
        }
    }

    fun changeAmountText(amount: String) {
        val newAmountText = amount.filter { it.isDigit() }//|| it.equals('.') }

        if (newAmountText.isNotEmpty()) {
            val num = newAmountText.toDouble()

            if (num > 0) {
                _uiState.update { currentState ->
                    currentState.copy(
                        amountText = newAmountText,
                        validAmount = true
                    )
                }
            }
            else {
                _uiState.update { currentState ->
                    currentState.copy(
                        amountText = "",
                        validAmount = false
                    )
                }
            }
        }
        else {
            _uiState.update { currentState ->
                currentState.copy(
                    amountText = "",
                    validAmount = false
                )
            }
        }
    }

    fun saveRecurring() {
        val state = _uiState.value
        val amount: Double = state.amountText.toDouble() / 100

        viewModelScope.launch {
            recurringRepository.insertRecurring(
                Recurring(
                    amount = amount,
                    title = state.titleText,
                    details = state.detailsText,
                    active = state.active,
                    type = state.type,
                    period = state.period, // TODO: limit to 28 for type 0 and 365 for type 1
                    firstCharge = state.firstCharge,
                    lastCharge = state.lastCharge,
                    nextCharge = 0L
                )
            )
        }
    }

    companion object {
        class NewRecurringViewModelFactory(private val recurring: Recurring?) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                val container: AppContainer = (application as TransactionsApplication).container

//                Log.d(TAG, "NTVM made with ${transaction?.reason}")

                return NewRecurringViewModel(
                    container.recurringRepository,
                    container.dataStore,
                    recurring
                ) as T
            }
        }
    }
}

data class NewRecurringUiState(
    // These 5 are for tracking the 5 inputs when using this entire NewRecurring menu
    val titleText: String,
    val amountText: String,
    val detailsText: String?,
    val active: Boolean,
    val type: Int,
    val period: Int,
    val firstCharge: Long,
    val lastCharge: Long,

    // These 2 are for showing an error field
    val validPurchase: Boolean,
    val validAmount: Boolean,

    // This tracks the expanded state of the category picker menu
//    val categoryExpanded: Boolean,

    // This is for editing an existing recurring transaction
//    val existingTransaction: Transaction?
)