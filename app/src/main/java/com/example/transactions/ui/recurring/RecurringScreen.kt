package com.example.transactions.ui.recurring

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.transactions.Utility
import com.example.transactions.data.AppDataContainer
import com.example.transactions.data.Recurring

@Composable
fun RecurringScreen(
    viewModel: RecurringViewModel,
    newRecurring: () -> Unit,
    viewRecurring: (Recurring) -> Unit,
    deleteAllTransactions: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            itemsIndexed(uiState.recurringList) { index, recurring ->
                RecurringCard(
                    recurring,
                    index,
                    Utility.daysUntil(recurring.nextCharge)
                ) {
                    viewModel.updateNextCharge(recurring)

                    viewRecurring(recurring)
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(25.dp),
            onClick = {
                newRecurring()
            }
        ) {
            Icon(
                Icons.Filled.AddCircleOutline, "create a new recurring"
            )
        }
    }


//            FloatingActionButton(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(25.dp),
//                onClick = {
//                    newRecurring()
//                }
//            ) {
//                Icon(
//                    Icons.Filled.AddCircleOutline, ""
//                )
//            }
//        }
//    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecurringCard(
    recurring: Recurring,
    recurringId: Int,
    daysUntil: Int,
    onClick: () -> Unit
) {
    val title = recurring.title
    val amount = recurring.amount
    val details = recurring.details

    // TODO: add "next charge" field to schema to make my life infinitely easier, remember MIGRATION
//    val pretendDays = (recurringId) / 4f * 14
//    val type = transaction.type
//    val amount = transaction.amount
//    val reason = transaction.reason
//    val timestamp = transaction.timestamp

//    val date = Utility.readableDate(timestamp)

    val circleColor = MaterialTheme.colorScheme.onSurface

//    val supportingContent: (() -> Unit)? =
//        if (details == null) {
//            null
//        } else {
//            detailsText(details)
//        }

    ListItem(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    onClick()
//                    if (uiState.anySelected) {
//                        if (viewModel.selectTransaction(tranId)) {
//                            showDeleteButton()
//                        }
//                        else {
//                            hideDeleteButton()
//                        }
//                    }
//                    else {
//                        editTransaction(transaction)
//                    }
                }
//                onLongClick = {
//                    Log.d(com.example.transactions.ui.history.TAG,"Long click!")
//                    if (uiState.anySelected) {
//                        viewModel.deselectAll()
//                        hideDeleteButton()
//                    }
//                    else {
//                        viewModel.selectTransaction(tranId)
//                        showDeleteButton()
//                    }
//                }
            ),
        headlineContent = { Text(title) },
        supportingContent = { Text(details ?: "") },
        trailingContent = { Text(Utility.formatMoney(amount)) },
        leadingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
//                if (uiState.anySelected) {
//                    Icon(
//                        when(uiState.selectedTransactions[tranId]) {
//                            false -> Icons.Outlined.Circle
//                            true -> Icons.Outlined.CheckCircle
//                        },
//                        contentDescription = "Select"
//                    )
//                }
                Text(
                    text = daysUntil.toString(),
                    modifier = Modifier
                        .drawBehind {
                            drawCircle(
                                if (daysUntil > 7 || daysUntil < 0) {
                                    circleColor
                                }
                                else {
                                     Utility.lerp(
                                         Color(245, 239, 66),
                                         Color(245, 66, 66),
                                         1 - (daysUntil / 7f),
                                     )
                                },
                                radius = size.height * 0.6f,
                                style = Stroke(
                                    width = 2f
                                )
                            )
                        },
                    textAlign = TextAlign.Center
                )
//                Icon(
//                    when(type) {
//                        (0) -> Icons.Outlined.Redeem
//                        (1) -> Icons.Outlined.AttachMoney
//                        (2) -> Icons.Outlined.Savings
//                        (3) -> Icons.Outlined.ShoppingCartCheckout
//                        else -> {
//                            Icons.Outlined.Error
//                        }
//                    },
//                    contentDescription = "",
//                )
            }

        }
    )
}

@Composable
private fun detailsText(
    text: String
) {
    Text(text)
}

@Preview
@Composable
fun rcPreview() {
    val container = AppDataContainer(
        LocalContext.current
    )

    val viewModel = RecurringViewModel(
        container.recurringRepository
    )

    RecurringScreen(
        viewModel,
        {}, {}, {}
    )
}