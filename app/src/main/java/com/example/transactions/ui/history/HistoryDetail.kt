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

    TransactionEditScreen(
        false,
        viewModel,
        uiState,
        navigateHistory
    )
}