package com.example.transactions.ui.newRecurring

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.transactions.TransactionsApplication
import com.example.transactions.Utility
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

class NewRecurringViewModel(
    val recurringRepository: RecurringRepository,
    val historyRepository: HistoryRepository,
    val dataStore: DataStoreManager,
    recurring: Recurring?
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        NewRecurringUiState(
            "",
            null,
            true,
            0,
            Utility.currentDayOfMonth().toString(),
            "",
            0,
            0L,
            0L,
            false,
            true,
            false
        )
    )
    val uiState = _uiState.asStateFlow()

    fun changeTitleText(title: String) {
        _uiState.update { currentState ->
            currentState.copy( // TODO: remove start + end whitespace, possibly double spaces
                titleText = title,
                validTitle = title.isNotEmpty()
            )
        }
    }

    fun changeDetailsText(details: String) {
        _uiState.update { currentState ->
            currentState.copy( // TODO: remove start + end whitespace, possibly double spaces
                detailsText = details
            )
        }
    }

    fun changeType(type: Int) {
        _uiState.update {
            it.copy(
                type = type
            )
        }
    }

    fun changeActive(active: Boolean) {
        _uiState.update {
            it.copy(
                active = active
            )
        }
    }

    fun changePeriodText(period: String) {
        val periodDigits = period.filter { it.isDigit() }

        var newPeriod = periodDigits.toIntOrNull()

        if (newPeriod == null) {
            _uiState.update { currentState ->
                currentState.copy( // TODO: number only, restrictions based on type
                    periodText = "",
                    validPeriod = false
                )
            }
        }
        else {
            when (_uiState.value.type) {
                0 -> newPeriod = newPeriod.coerceIn(1, 28)
                1 -> newPeriod = newPeriod.coerceIn(1, 365)
                // TODO: add 3rd type for X months
            }

            _uiState.update { currentState ->
                currentState.copy( // TODO: number only, restrictions based on type
                    periodText = newPeriod.toString(),
                    validPeriod = true
                )
            }
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

    fun changeChargeType(chargeType: Int) {
        _uiState.update {
            it.copy(
                chargeType = chargeType
            )
        }
    }

    fun saveRecurring() {
        val state = _uiState.value
        val amount = state.amountText.toInt()
        val now = Utility.time()

        val recurring = Recurring(
            amount = amount,
            title = state.titleText,
            details = state.detailsText,
            active = state.active,
            type = state.type,
            period = state.periodText.toInt(),
            firstCharge = now,
            lastCharge = now,
            nextCharge = 0L
        )

        viewModelScope.launch {
            recurringRepository.insertRecurring(
                recurring.copy(
                    nextCharge = Utility.nextCharge(recurring)
                )
            )

            if (state.chargeType == 0) {
                historyRepository.insertTransaction(
                    Transaction(
                        type = 1,
                        category = 13,
                        amount = amount,
                        reason = state.titleText,
                        timestamp = Utility.time()
                    )
                )
                dataStore.removeBalance(
                    amount
                )
            }
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
                    container.historyRepository,
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
    val detailsText: String?,
    val active: Boolean,
    val type: Int,
    val periodText: String,
    val amountText: String,
    val chargeType: Int,
    val firstCharge: Long,
    val lastCharge: Long,

    // These 2 are for showing an error field
    val validTitle: Boolean,
    val validPeriod: Boolean,
    val validAmount: Boolean,

    // This tracks the expanded state of the category picker menu
//    val categoryExpanded: Boolean,

    // This is for editing an existing recurring transaction
//    val existingTransaction: Transaction?
)

@Preview
@Composable
fun NRVMPreview() {
    NewRecurringPreview()
}