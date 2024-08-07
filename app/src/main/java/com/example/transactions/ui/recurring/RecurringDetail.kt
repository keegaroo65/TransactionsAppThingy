package com.example.transactions.ui.recurring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transactions.Utility
import com.example.transactions.data.Recurring
import kotlinx.coroutines.launch

@Composable
fun RecurringDetail(
    recurring: Recurring,
    testRunSubscription: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column {
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
        """.trimIndent()
        )

        ExtendedFloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    testRunSubscription()
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "Test run since Jan 1st 2024"
            )
        }
    }
}