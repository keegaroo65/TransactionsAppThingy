package com.example.transactions

import androidx.compose.runtime.mutableDoubleStateOf

class Budget {
    companion object {
        var balance = mutableDoubleStateOf(0.0)
            private set

        fun AddBalance(
            amount: Double
        ) {
            balance.value += amount
            Save()
        }

        fun RemoveBalance(
            amount: Double
        ) {
            balance.value -= amount
            Save()
        }

        fun NewTransaction(
            type: Int,
            amount: Double,
            reason: String
        ) {

        }

        fun Save() {

        }
    }
}