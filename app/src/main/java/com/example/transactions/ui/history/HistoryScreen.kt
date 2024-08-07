package com.example.transactions.ui.history

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transactions.Utility
import com.example.transactions.data.Transaction

private const val TAG = "HistoryScreen"

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    newTransaction: () -> Unit,
    editTransaction: (Transaction) -> Unit,
    showDeleteButton: () -> Unit,
    hideDeleteButton: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    Log.d(TAG, "Recomposing HistoryScreen ${uiState.anySelected}")

    val transactions = uiState.transactionList

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            for (i in 0..<transactions.count()) {
                val transaction = transactions[i]

                HistoryCard(
                    viewModel,
                    transaction,
                    i,
                    uiState,
                    editTransaction,
                    showDeleteButton,
                    hideDeleteButton
                )
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(25.dp),
            onClick = {
                newTransaction()
            }
        ) {
            Icon(
                Icons.Filled.AddCircleOutline, ""
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryCard(
    viewModel: HistoryViewModel,
    transaction: Transaction,
    tranId: Int,
    uiState: HistoryUiState,
    editTransaction: (Transaction) -> Unit,
    showDeleteButton: () -> Unit,
    hideDeleteButton: () -> Unit,
) {
    val type = transaction.type
    val amount = transaction.amount
    val reason = transaction.reason
    val timestamp = transaction.timestamp

    val date = Utility.readablePastDate(timestamp)

    ListItem(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    if (uiState.anySelected) {
                        if (viewModel.selectTransaction(tranId)) {
                            showDeleteButton()
                        }
                        else {
                            hideDeleteButton()
                        }
                    }
                    else {
                        editTransaction(transaction)
                    }
                },
                onLongClick = {
                    Log.d(TAG,"Long click!")
                    if (uiState.anySelected) {
                        viewModel.deselectAll()
                        hideDeleteButton()
                    }
                    else {
                        viewModel.selectTransaction(tranId)
                        showDeleteButton()
                    }
                }
            ) ,
        headlineContent = { Text(reason) },
        supportingContent = { Text(date) },
        trailingContent = { Text(Utility.formatMoney(amount)) },
        leadingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (uiState.anySelected) {
                    Icon(
                        when(uiState.selectedTransactions[tranId]) {
                            false -> Icons.Outlined.Circle
                            true -> Icons.Outlined.CheckCircle
                        },
                        contentDescription = "Select"
                    )
                }
                Icon(
                    when(type) {
                        (0) -> Icons.Outlined.Redeem
                        (1) -> Icons.Outlined.AttachMoney
                        (2) -> Icons.Outlined.Savings
                        (3) -> Icons.Outlined.ShoppingCartCheckout
                        else -> {
                            Icons.Outlined.Error
                        }
                    },
                    contentDescription = "",
                )
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarHistoryDropdown(
    viewModel: HistoryViewModel,
    deleteTransactions: (List<Transaction>) -> Unit,
    dismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState().value

    DropdownMenu(
        expanded = true,
        onDismissRequest = {
            dismissDropdown()
        }
    ) {
        DropdownMenuItem(
            onClick = {
                viewModel.updateDeletePrompt(true)
            },
            text = { Text("Delete") }
        )
    }

    if (uiState.promptDeleteSelected) {
        HistoryDeleteDialog(
            viewModel,
            {
                deleteTransactions(viewModel.transactionsToDelete())
                dismissDropdown()
            }
        )
    }
}

@Composable
fun HistoryDeleteDialog(
    viewModel: HistoryViewModel,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit = {}
) {
    val deleteCount = viewModel.transactionsToDelete().count()

    AlertDialog(
        onDismissRequest = {
            viewModel.updateDeletePrompt(false)
            onDeleteCancel()
        },
        title = {
            Text("Confirm")
        },
        text = {
            Text("Are you sure you want to delete $deleteCount transaction${if (deleteCount > 1) "s" else ""}?")
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.updateDeletePrompt(false)
                onDeleteCancel()
            }) {
                Text("No")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateDeletePrompt(false)
                    onDeleteConfirm()
                }
            ) {
                Text("Yes")
            }
        }
    )
}