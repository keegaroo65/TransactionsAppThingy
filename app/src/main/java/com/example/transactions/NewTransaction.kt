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
import androidx.compose.material.icons.filled.AccountBox
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.DecimalFormat

@Composable
fun NewTransaction(
    navController: NavController
) {
    // These 4 are for turning the text fields red if opened then closed without inputting valid value
    var purchaseTouched = remember { mutableStateOf(false) }
    var purchaseOpened = remember { mutableStateOf(false) }
    var amountTouched = remember { mutableStateOf(false) }
    var amountOpened = remember { mutableStateOf(false) }

    // These 2 are for showing an error field
    var validPurchase = remember { mutableStateOf(false) }
    var validAmount = remember { mutableStateOf(false) }

    // These 2 are for forcing capture on invalid TextFields
    var purchaseFocus = remember { FocusRequester() }
    var amountFocus = remember { FocusRequester() }

    // This tracks the cursor in the amount field to smoothly type currency values
    var amountCursor = remember { mutableStateOf(0) }
    var amountField = remember { mutableStateOf(TextFieldValue("")) }

    //var capturePurchase = remember { mutableStateOf(false) }
    //var captureAmount = remember { mutableStateOf(false) }

    /*
    val purchaseInteractionSource = remember { MutableInteractionSource() }
    val purchaseIsFocused by purchaseInteractionSource.collectIsFocusedAsState()

    val amountInteractionSource = remember { MutableInteractionSource() }
    val amountIsFocused by amountInteractionSource.collectIsFocusedAsState()
     */

    // These 3 are for tracking the 3 inputs when logging a new transaction
    var toSavings by remember { mutableStateOf(false) }
    var purchase by remember { mutableStateOf("")}
    var amountText by remember { mutableStateOf("") }

    var purchaseModifier = Modifier
        .onFocusChanged {
        if (it.isFocused)
            purchaseOpened.value = true
        else if (purchaseOpened.value)
            purchaseTouched.value = true
        }
        .focusRequester(purchaseFocus)

    var amountModifier = Modifier
        .onFocusChanged {
            if (it.isFocused)
                amountOpened.value = true
            else if (amountOpened.value)
                amountTouched.value = true
        }
        .focusRequester(amountFocus)

    /*if (capturePurchase.value) {
        purchaseModifier = purchaseModifier
            .captureFocus()
    }*/

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 0.dp, 0.dp, 20.dp),
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
                    modifier = purchaseModifier,
                    value = purchase,
                    label = { Text("Purchase") },
                    placeholder = { Text("Purchase") },
                    isError = purchaseTouched.value && !validPurchase.value,
                    onValueChange = {
                        purchase = it
                        validPurchase.value = purchase.isNotEmpty()
                    },
                    //interactionSource = purchaseInteractionSource
                )
                OutlinedTextField(
                    modifier = amountModifier,
                    //value = amountText,
                    value = amountText,
                    /*if (amount != 0.0)
                        amount.toString()//String.format("$%.2f", amount)//DecimalFormat("$ .00").format(amount) TODO: investigate lol
                    else
                        "",*/
                    //value = amountField,
                    label = { Text("Amount") },
                    placeholder = { Text("Amount") },
                    isError = amountTouched.value && !validAmount.value,
                    onValueChange = {
                        var newAmountText = it.filter { it.isDigit() }//|| it.equals('.') }

                        //Log.i("amountnum",newAmountText)

                        if (newAmountText.isNotEmpty()) {
                            amountText = newAmountText

                            var num = amountText.toDouble()

                            if (num > 0)
                                validAmount.value = true
                            else {
                                amountText = ""
                                validAmount.value = false
                            }
                        }
                        else {
                            amountText = ""
                            validAmount.value = false
                        }
                    },
                    visualTransformation = MoneyTransformation(),
                    /*cursorPosition = amountCursor,
                    onCursorPositionChange = {
                        TODO: cry about this stupid cursor shit i dont wanna have to make a TextFieldValue :(
                        enough spaghetti already </3. i think all it needs to do is set cursor to end when first digit typed !!
                    },*/
                    //interactionSource = amountInteractionSource,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            FloatingActionButton( // TODO: make this appear and work lol
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(25.dp),
                onClick = {
                    if (!validPurchase.value) {
                        purchaseTouched.value = true
                        purchaseFocus.requestFocus()
                    }
                    else if (!validAmount.value) {
                        amountTouched.value = true
                        amountFocus.requestFocus()
                    }
                    else {
                        Budget.NewTransaction(
                            type = if (toSavings) 3 else 2,
                            amount = amountText.toDouble(),
                            reason = purchase
                        )

                        navController.navigate("home")
                    }
                }
            ) {
                Icon(
                    Icons.Filled.Check, ""
                )
            }
        }
    }
}

@Preview
@Composable
fun NewTransactionPreview() {
    NewTransaction(rememberNavController())
}