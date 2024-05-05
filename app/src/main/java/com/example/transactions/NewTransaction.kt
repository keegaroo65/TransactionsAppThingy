package com.example.transactions

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
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCartCheckout
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransaction(
    navController: NavController
) {
    val typeOptions = listOf(
        Icons.Outlined.Redeem,
        Icons.Outlined.AttachMoney,
        Icons.Outlined.Savings,
        Icons.Outlined.ShoppingCartCheckout
    )

    // These 4 are for turning the text fields red if opened then closed without inputting valid value
    val purchaseTouched = rememberSaveable { mutableStateOf(false) }
    val purchaseOpened = rememberSaveable { mutableStateOf(false) }
    val amountTouched = rememberSaveable { mutableStateOf(false) }
    val amountOpened = rememberSaveable { mutableStateOf(false) }

    // These 2 are for showing an error field
    val validPurchase = rememberSaveable { mutableStateOf(false) }
    val validAmount = rememberSaveable { mutableStateOf(false) }

    // These 2 are for forcing capture on invalid TextFields
    val purchaseFocus = rememberSaveable { FocusRequester() }
    val amountFocus = rememberSaveable { FocusRequester() }

    // These 3 are for tracking the 3 inputs when using this entire NewTransaction menu
    var tranType by rememberSaveable { mutableStateOf(1) }
    var categoryChosen by rememberSaveable { mutableStateOf(11) } // defaults to Quick Food (11)
    var purchase by rememberSaveable { mutableStateOf("")}
    var amountText by rememberSaveable { mutableStateOf("") }

    // This tracks the cursor in the amount field to smoothly type currency values
    var amountCursor = rememberSaveable { mutableStateOf(0) }
    var amountField = rememberSaveable { mutableStateOf(TextFieldValue("")) }

    // These track the expanded state of the category picker menu
    val categoryExpanded = rememberSaveable { mutableStateOf(false) }

    val purchaseModifier = Modifier
        .onFocusChanged {
        if (it.isFocused)
            purchaseOpened.value = true
        else if (purchaseOpened.value)
            purchaseTouched.value = true
        }
        .focusRequester(purchaseFocus)

    val amountModifier = Modifier
        .onFocusChanged {
            if (it.isFocused)
                amountOpened.value = true
            else if (amountOpened.value)
                amountTouched.value = true
        }
        .focusRequester(amountFocus)

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
                // Title text
                Text(
                    text = "New Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = TextDecoration.Underline
                )
                // Segmented button to pick between 4 transaction types
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SingleChoiceSegmentedButtonRow {
                        typeOptions.forEachIndexed { index, icon ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                                onClick = { tranType = index },
                                /*icon = {
                                    SegmentedButtonDefaults.Icon(active = index == tranType) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "",
                                            tint = Color.Cyan,
                                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                        )
                                    }
                                },*/
                                selected = index == tranType
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "",
                                    //tint = Color.Cyan,
                                    modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier
                            .padding(5.dp),
                        textAlign = TextAlign.Center,
                        text = when (tranType) {
                            0 -> "Add balance"
                            1 -> "Spend balance"
                            2 -> "Move to savings"
                            else -> "Spend savings"
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp),
                        text = "Category"
                    )
                    DropdownMenu(
                        expanded = categoryExpanded.value,
                        onDismissRequest = {
                            categoryExpanded.value = false
                        }
                    ) {
                        Budget.TransactionCategories.forEachIndexed { index, category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    categoryChosen = index
                                    categoryExpanded.value = false
                                }
                            )
                        }
                    }
                    ElevatedButton(
                        onClick = {
                            categoryExpanded.value = true
                        }
                    ) {
                        Icon(Icons.Outlined.ExpandMore, "dropdown")
                        Text(Budget.TransactionCategories[categoryChosen])
                    }
                }
                /*Row(
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
                }*/
                // Purchase text input field
                OutlinedTextField(
                    modifier = purchaseModifier,
                    value = purchase,
                    label = { Text("Purchase") },
                    placeholder = { Text("Purchase") },
                    isError = purchaseTouched.value && !validPurchase.value,
                    onValueChange = {
                        purchase = it.replace(";", "")
                        validPurchase.value = purchase.isNotEmpty()
                    },
                )
                // Amount text input field
                OutlinedTextField(
                    modifier = amountModifier,
                    value = amountText,
                    label = { Text("Amount") },
                    placeholder = { Text("Amount") },
                    isError = amountTouched.value && !validAmount.value,
                    onValueChange = { it ->
                        val newAmountText = it.filter { it.isDigit() }//|| it.equals('.') }

                        //Log.i("amountnum",newAmountText)

                        if (newAmountText.isNotEmpty()) {
                            amountText = newAmountText

                            val num = amountText.toDouble()

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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            FloatingActionButton(
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
                            type = tranType + 1,
                            category = categoryChosen,
                            amount = amountText.toDouble() / 100,
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