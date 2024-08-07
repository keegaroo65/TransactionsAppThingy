package com.example.transactions.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.transactions.ui.newTransaction.NewTransactionViewModel
import com.example.transactions.ui.newTransaction.TransactionEditScreen

@Composable
fun HistoryDetail(
    viewModel: NewTransactionViewModel,
    navigateHistory: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    // TODO: separate editing & viewing screens for a more pleasurable straight-forward experience
    // TODO: add delete button from the transaction details OR edit screen
    TransactionEditScreen(
        false,
        viewModel,
        uiState,
        navigateHistory
    )
}