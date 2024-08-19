package com.example.transactions.ui.newTransaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.transactions.TransactionsApplication
import com.example.transactions.Utility
import com.example.transactions.data.AppContainer
import com.example.transactions.data.DataStoreManager
import com.example.transactions.data.HistoryRepository
import com.example.transactions.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private val TAG = "NewTransactionVM"

class NewTransactionViewModel(
    val historyRepository: HistoryRepository,
    val dataStore: DataStoreManager,
    val transaction: Transaction?
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        if (transaction == null)
            NewTransactionUiState(
                LocalDateTime.MIN,
                1,
                11,
                "",
                "",
                false,
                false,
                false,
                null
            )
        else
            NewTransactionUiState(
                LocalDateTime.MIN,
                transaction.type,
                transaction.category,
                transaction.reason,
                (transaction.amount * 10).toString().filter { it.isDigit() },
                true,
                true,
                false,
                transaction
            )
    )
    val uiState = _uiState.asStateFlow()

    fun openTimestampDialogue() {
        _uiState.update { currentState ->
            currentState.copy(
                modTimestamp = LocalDateTime.of(
                    2024,
                    2,
                    10,
                    16,
                    50
                )
            )
        }
    }

    fun changeTranType(tranType: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                tranType = tranType
            )
        }
    }

    fun changeCategory(category: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                category = category,
                categoryExpanded = false
            )
        }
    }

    fun changePurchaseText(purchaseText: String) {
        _uiState.update { currentState ->
            currentState.copy( // TODO: remove start + end whitespace, possibly double spaces
                purchaseText = purchaseText,
                validPurchase = purchaseText.isNotEmpty()
            )
        }
    }

    fun changeAmountText(amountText: String) {
        val newAmountText = amountText.filter { it.isDigit() }//|| it.equals('.') }

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

    fun changeCategoryExpanded(categoryExpanded: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                categoryExpanded = categoryExpanded
            )
        }
    }

    fun saveTransaction() {
        val state: NewTransactionUiState = uiState.value
        val amount: Double = state.amountText.toDouble() / 100
        val timestamp = Utility.time()

        viewModelScope.launch {
            if (state.existingTransaction == null) {
                // Log transaction in database
                historyRepository.insertTransaction(
                    Transaction(
                        type = state.tranType,
                        category = state.category,
                        amount = amount,
                        reason = state.purchaseText,
                        timestamp = timestamp
                    )
                )

                // Modify current balance
                when(state.tranType) {
                    0 -> dataStore.addBalance(amount)
                    1 -> dataStore.removeBalance(amount)
                    2 -> "Move to savings"
                    else -> "Spend savings"
                }
            }
            else {
                // Edit transaction in database
                historyRepository.updateTransaction(
                    Transaction(
                        id = state.existingTransaction.id,
                        type = state.tranType,
                        category = state.category,
                        amount = amount,
                        reason = state.purchaseText,
                        timestamp = state.existingTransaction.timestamp
                    )
                )

                // Modify current balance
                val oldType = state.existingTransaction.type
                val oldAmount = state.existingTransaction.amount

                when(state.tranType) {
                    0 -> {
                        when(oldType) {
                            0 -> dataStore.addBalance(amount - oldAmount) // Adding -> Adding, only add difference
                            1 -> dataStore.addBalance(oldAmount + amount) // Removing -> Adding, un-remove old, then add new on-top
                        }
                    }
                    1 -> {
                        when(oldType) {
                            0 -> dataStore.removeBalance(oldAmount + amount) // Adding -> Removing, remove old, then remove new on-top
                            1 -> dataStore.removeBalance(amount - oldAmount)// Removing -> Removing, only remove difference
                        }
                    }
                    2 -> "Move to savings" // TODO: savings
                    else -> "Spend savings"
                }
            }
        }

        _uiState.update {
            NewTransactionUiState(
                LocalDateTime.MIN,
                1,
                11,
                "",
                "",
                false,
                false,
                false,
                null
            )
        }
    }

//    fun openToEdit(transaction: Transaction) {
//        _uiState.update {
//            NewTransactionUiState(
//                LocalDateTime.MIN,
//                transaction.type,
//                transaction.category,
//                transaction.reason,
//                "",
//                true,
//                true,
//                false,
//                transaction
//            )
//        }
//
//        changeAmountText((transaction.amount*10).toString())
//    }

    companion object {
        class NewTransactionViewModelFactory(private val transaction: Transaction?) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                val container: AppContainer = (application as TransactionsApplication).container

                Log.d(TAG, "NTVM made with ${transaction?.reason}")

                return NewTransactionViewModel(
                    container.historyRepository,
                    container.dataStore,
                    transaction
                ) as T
            }
        }
    }
}



data class NewTransactionUiState(
    // These 5 are for tracking the 5 inputs when using this entire NewTransaction menu
    val modTimestamp: LocalDateTime,
    val tranType: Int,
    val category: Int,
    val purchaseText: String,
    val amountText: String,

    // These 2 are for showing an error field
    val validPurchase: Boolean,
    val validAmount: Boolean,

    // This tracks the expanded state of the category picker menu
    val categoryExpanded: Boolean,

    // This is for editing an existing transaction
    val existingTransaction: Transaction?
)