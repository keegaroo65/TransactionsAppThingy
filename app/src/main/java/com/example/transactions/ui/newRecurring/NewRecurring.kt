package com.example.transactions.ui.newRecurring

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
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.transactions.MoneyTransformation
import com.example.transactions.R
import com.example.transactions.data.AppDataContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecurring(
    viewModel: NewRecurringViewModel,
    navigateBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    val typeOptions = listOf(
        Icons.Outlined.Event,
        Icons.Outlined.DateRange
    )

    val startTypeOptions = listOf(
        Icons.Outlined.AttachMoney,
        ImageVector.vectorResource(R.drawable.event_upcoming)
    )

    // TODO: array for these saveables?

    // These 6 are for turning the text fields red if opened then closed without inputting valid value
    val titleOpened = rememberSaveable { mutableStateOf(false) }
    val titleTouched = rememberSaveable { mutableStateOf(false) }

    val detailsOpened = rememberSaveable { mutableStateOf(false) }
    val detailsTouched = rememberSaveable { mutableStateOf(false) }

    val amountOpened = rememberSaveable { mutableStateOf(false) }
    val amountTouched = rememberSaveable { mutableStateOf(false) }

    // These 3 are for forcing capture on invalid TextFields
    val titleFocus = remember { FocusRequester() }
    val detailsFocus = remember { FocusRequester() }
    val periodFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }

    val titleModifier = Modifier
        .onFocusChanged {
            if (it.isFocused)
                titleOpened.value = true
            else if (titleOpened.value)
                titleTouched.value = true
        }
        .focusRequester(titleFocus)

    val detailsModifier = Modifier
        .onFocusChanged {
            if (it.isFocused)
                detailsOpened.value = true
            else if (detailsOpened.value)
                detailsTouched.value = true
        }
        .focusRequester(detailsFocus)

    val periodModifier = Modifier
        .focusRequester(periodFocus)

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
            .padding(15.dp)
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
                    text = "New Recurring Transaction",//if (isNew) "New Recurring" else "Edit Transaction",
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = TextDecoration.Underline
                )

                // Title text input field
                OutlinedTextField(
                    modifier = titleModifier,
                    value = uiState.titleText,
                    label = { Text("Title") },
                    placeholder = { Text("Title") },
                    isError = titleTouched.value && !uiState.validTitle,
                    onValueChange = { viewModel.changeTitleText(it) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    )
                )

                // Details text input field
                OutlinedTextField(
                    modifier = detailsModifier,
                    value = uiState.detailsText ?: "",
                    label = { Text("Details") },
                    placeholder = { Text("Details (optional)") },
                    onValueChange = { viewModel.changeDetailsText(it) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Active",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = uiState.active,
                        onCheckedChange = { viewModel.changeActive(!uiState.active) },
                        thumbContent = if (uiState.active) {
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

                // Segmented button to pick between 2 recurring types
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SingleChoiceSegmentedButtonRow {
                        typeOptions.forEachIndexed { index, icon ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                                onClick = { viewModel.changeType(index) },
                                selected = index == uiState.type
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
                        text = when (uiState.type) {
                            0 -> "(max of 28) Calendar day each month:"
                            else -> "(max of 365) Period of days:"
                        }
                    )
                }

                // Period text input field
                OutlinedTextField(
                    modifier = periodModifier,
                    value = uiState.periodText,
                    label = { Text("Period") },
                    placeholder = { Text("Period") },
                    isError = !uiState.validPeriod,
                    onValueChange = { viewModel.changePeriodText(it) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    )
                )

                // Amount text input field
                OutlinedTextField(
                    modifier = amountModifier,
                    value = uiState.amountText,
                    label = { Text("Amount") },
                    placeholder = { Text("Amount") },
                    isError = amountTouched.value && !uiState.validAmount,
                    onValueChange = { viewModel.changeAmountText(it) },
                    visualTransformation = MoneyTransformation(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                )

                // Segmented button to pick between 2 recurring types
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SingleChoiceSegmentedButtonRow {
                        startTypeOptions.forEachIndexed { index, icon ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = typeOptions.size),
                                onClick = { viewModel.changeChargeType(index) },
                                selected = index == uiState.chargeType
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
                        text = when (uiState.chargeType) {
                            0 -> "make first charge now"
                            else -> "wait until next charge"
                        }
                    )
                }

                /*
                    TODO:
                     - Add firstCharge & lastCharge fields
                     - show next projected charge in this menu
                 */


//                Column() {
////                    if (uiState.modTimestamp != LocalDateTime.MIN) {
////                        Text(
////                            uiState.modTimestamp.toString() + "\n" + uiState.modTimestamp.toEpochSecond(
////                                ZoneOffset.UTC)
////                        )
////                    }
//
//                    // Edit timestamp dialogue opener
//                    TextButton(
//                        onClick = {
////                            viewModel.openTimestampDialogue()
//                        }
//                    ) {
//                        Text(
//                            text = "Edit time",
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Icon(
//                            Icons.Outlined.ArrowCircleRight,
//                            "Edit time button"
//                        )
//                    }
//                }


//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        modifier = Modifier
//                            .padding(10.dp),
//                        text = "Category"
//                    )
//                    DropdownMenu(
//                        expanded = uiState.categoryExpanded,
//                        onDismissRequest = { viewModel.changeCategoryExpanded(false) }
//                    ) {
//                        Utility.TransactionCategories.forEachIndexed { index, category ->
//                            DropdownMenuItem(
//                                text = { Text(category) },
//                                onClick = { viewModel.changeCategory(index) }
//                            )
//                        }
//                    }
//                    ElevatedButton(
//                        onClick = { viewModel.changeCategoryExpanded(true) }
//                    ) {
//                        Icon(Icons.Outlined.ExpandMore, "dropdown")
//                        Text(Utility.TransactionCategories[uiState.category])
//                    }
//                }
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
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(25.dp),
                onClick = {
                    if (!uiState.validTitle) {
                        titleTouched.value = true
                        titleFocus.requestFocus()
                    }
                    else if (!uiState.validPeriod) {
                        periodFocus.requestFocus()
                    }
                    else if (!uiState.validAmount) {
                        amountTouched.value = true
                        amountFocus.requestFocus()
                    }
                    else {
                        viewModel.saveRecurring()
                        navigateBack()
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
fun NewRecurringPreview() {
    val container = AppDataContainer(
        LocalContext.current
    )

    val viewModel = NewRecurringViewModel(
        container.recurringRepository,
        container.historyRepository,
        container.dataStore,
        null
    )

    NewRecurring(
        viewModel
    ) {}
}