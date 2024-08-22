package com.example.transactions.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.transactions.Utility
import com.example.transactions.workers.SubscriptionsViewModel
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    subscriptionsViewModel: SubscriptionsViewModel,
    newTransaction: () -> Unit
) {
    val balance by viewModel.balance.collectAsState()//WithLifecycle()

    Log.d(TAG, "_balance is $balance")

//    val balance = 0.0

    val budget = Utility.formatMoney(balance)

    val coroutineScope = rememberCoroutineScope()

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
                    if (balance < 0.0) MaterialTheme.colorScheme.errorContainer
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
                        if (balance < 0.0) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer,
                    //fontSize = 50.sp
                )
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    newTransaction()
                }
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
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            FloatingActionButton(
                onClick  = {
                    viewModel.add25()
                }
            ) {
                Icon(Icons.Outlined.AddCircleOutline, "")
            }
            FloatingActionButton(
                onClick  = {
                    viewModel.resetBalance()
                }
            ) {
                Icon(Icons.Outlined.Cancel, "")
            }
            FloatingActionButton(
                onClick = {
                    viewModel.remove25()
                }
            ) {
                Icon(Icons.Outlined.Paid, "")
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                Log.d(TAG, "workManagerTest started ${Utility.time()}")
                subscriptionsViewModel.test()
            }
        ) {
            Icon(
                Icons.Outlined.AddCircle, ""
            )
            Text(
                modifier = Modifier
                    .padding(10.dp),
                text = "Test Work Task"
            )
        }
    }
}