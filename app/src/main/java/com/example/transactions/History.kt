package com.example.transactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun History() {
    Text(
        modifier = Modifier
            .fillMaxSize()
            .padding(100.dp),
        textAlign = TextAlign.Center,
        text = "You spend too much money on sweet treats :("
    )
}

@Preview
@Composable
fun HistoryPreview() {
    History()
}