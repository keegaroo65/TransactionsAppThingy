package com.example.transactions.ui.recurring

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transactions.Utility
import com.example.transactions.data.Recurring
import kotlinx.coroutines.launch

@Composable
fun RecurringDetail(
    recurring: Recurring,
    deleteRecurring: () -> Unit,
    editRecurring: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        Text("""
            id: ${recurring.id}
            amount: ${recurring.amount}
            title: ${recurring.title}
            details: ${recurring.details}
            active: ${recurring.active}
            type: ${recurring.type}
            period: ${recurring.period}
            first: ${Utility.readableDate(recurring.firstCharge)}
            lastCharge: ${Utility.readableDate(recurring.lastCharge)}
            nextCharge: ${Utility.readableDate(recurring.nextCharge)}
        """.trimIndent(),
            modifier = Modifier
                .align(Alignment.Center)
        )

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    editRecurring()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                Icons.Filled.Edit, "edit"
            )
        }

        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    deleteRecurring()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) { // TODO: confirmation popup before deletion
            Icon(
                Icons.Filled.Delete, "delete"
            )
        }
    }
}