package com.example.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MarkunreadMailbox
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
        //.verticalScroll(rememberScrollState())
    ) {
        for (i in 1..6) { HistoryCard(
            i,
            i.toDouble()*2.5,
            "Chezburger" + i.toString(),
            50+i.toLong()
        ) }
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
    val date = timestamp.toString()

    ListItem(
        headlineContent = { Text(reason) },
        supportingContent = { Text(date) },
        trailingContent = { Text(amount.toString()) },
        leadingContent = {
            Icon(
                when(type) {
                    (1) -> Icons.Outlined.AccountBalance
                    (2) -> Icons.Outlined.SupervisorAccount
                    (3) -> Icons.Outlined.FavoriteBorder
                    (4) -> Icons.Outlined.MarkunreadMailbox
                    else -> {
                        Icons.Outlined.Error
                    }
                },
                contentDescription = "Localized description",
            )
        }
    )
}

@Preview
@Composable
fun HistoryPreview() {
    History()
}