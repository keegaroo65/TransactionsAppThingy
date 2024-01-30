package com.example.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MarkunreadMailbox
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun History(

){


    Column (
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        val transactions: ArrayList<TransactionLog> = HistoryTracker.GetAllHistory()

        for (i in 0..transactions.count() - 1) {
            val transaction = transactions[i]

            HistoryCard(
                transaction.type,
                transaction.amount,
                transaction.reason,
                transaction.timestamp
            )
        }

        /*for (i in 1..6) { HistoryCard(
            i,
            i.toDouble()*2.5,
            "Chezburger" + i.toString(),
            i.toLong()*5000
        ) }*/

    }

    //HistoryCard()

    /*Text(
        modifier = Modifier
            .fillMaxSize()
            .padding(100.dp),
        textAlign = TextAlign.Center,
        text = "You spend too much money on sweet treats :("
    )*/
}

@Composable
fun HistoryCard(
    type: Int,
    amount: Double,
    reason: String,
    timestamp: Long
) {
    val date = Utility.readableDate(timestamp)

    ListItem(
        modifier = Modifier
            .clickable {

            },
        headlineContent = { Text(reason) },
        supportingContent = { Text(date) },
        trailingContent = { Text(amount.toString()) },
        leadingContent = {
            Icon(
                when(type) {
                    (1) -> Icons.Outlined.Redeem
                    (2) -> Icons.Outlined.AttachMoney
                    (3) -> Icons.Outlined.Savings
                    (4) -> Icons.Outlined.ShoppingCartCheckout
                    else -> {
                        Icons.Outlined.Error
                    }
                },
                contentDescription = "",
            )
        }
    )
}

@Preview
@Composable
fun HistoryPreview() {
    History()
}