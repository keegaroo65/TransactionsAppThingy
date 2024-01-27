package com.example.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.abs

@Composable
fun Home(
    navController: NavController
) {
    var balance = Budget.balance
    var negBudget = balance.doubleValue < 0.0

    // Format the double as currency text (exactly 2 decimal places)
    var budget = String.format("$%.2f",abs(balance.doubleValue))

    // Add the - BEFORE the $ if the balance is negative
    if (negBudget) {
        budget = "-" + budget
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly

    ) {
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (negBudget) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Budget:\n$budget",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayLarge,
                    color =
                        if (negBudget) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                    //fontSize = 50.sp
                )
            }
        }
        ExtendedFloatingActionButton(

            onClick = {
                navController.navigate("home/newTransaction")
            }
        ) {
            Icon(
                Icons.Outlined.AddCircle, ""
            )
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "New Transaction"
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick  = {
                    Budget.AddBalance(2.5)
                }
            ) {
                Icon(Icons.Outlined.AddCircleOutline, "")
            }
            FloatingActionButton(
                onClick = {
                    Budget.RemoveBalance(2.5)
                }
            ) {
                Icon(Icons.Outlined.Paid, "")
            }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    Home(rememberNavController())
}