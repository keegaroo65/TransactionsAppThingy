package com.example.transactions

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NewTransaction() {
    var purchaseTouched = remember { mutableStateOf(false) }
    var amountTouched = remember { mutableStateOf(false) }

    var invalidAmount = remember { mutableStateOf(false) }

    val purchaseInteractionSource = remember { MutableInteractionSource() }
    val purchaseIsFocused by purchaseInteractionSource.collectIsFocusedAsState()

    val amountInteractionSource = remember { MutableInteractionSource() }
    val amountIsFocused by amountInteractionSource.collectIsFocusedAsState()

    var toSavings by remember { mutableStateOf(false) }
    var purchase by remember { mutableStateOf("")}
    var amount by remember { mutableStateOf(0.0)}
    // TODO: Error check empty purchase & <= 0 amounts

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "New Transaction",
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(5.dp),
                    text = "Move to savings"
                )
                Switch(
                    checked = toSavings,
                    onCheckedChange = {
                        toSavings = it
                    },
                    thumbContent = if (toSavings) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )
            }
            OutlinedTextField(
                value = purchase,
                label = { Text("Purchase") },
                placeholder = { Text("Purchase") },
                onValueChange = {
                    purchase = it
                },
                interactionSource = purchaseInteractionSource
            )
            OutlinedTextField(
                value =
                    if (amount != 0.0)
                        String.format("$%.2f",amount)
                    else
                        "",
                label = { Text("Amount") },
                placeholder = { Text("Amount") },
                isError = amountTouched.value && !amountIsFocused && amount <= 0.0,
                onValueChange = {
                    amountTouched.value = true
                    /*var newAmount = it.toDoubleOrNull()

                    if (newAmount != null) {
                        invalidAmount.value = false
                        amount = newAmount
                    }
                    else
                        invalidAmount.value = true*/
                    var amountNumber = it.filter { it.isDigit() }
                    Log.i("DIGITONLY", amountNumber)

                    if (amountNumber.isNotEmpty()) {
                        amount = amountNumber.toDouble()
                    }
                },
                interactionSource = amountInteractionSource,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        FloatingActionButton( // TODO: make this appear and work lol
            onClick = {

            }
        ) {
            Icon(
                Icons.Filled.Check, ""
            )
        }
    }
}

@Preview
@Composable
fun NewTransactionPreview() {
    NewTransaction()
}