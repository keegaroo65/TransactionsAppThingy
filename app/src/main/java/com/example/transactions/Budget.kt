package com.example.transactions

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf

class Budget {
    companion object {
        val TransactionCategories = arrayOf(
            "Emergency",
            "Games/Toys",
            "Giving",
            "Groceries",
            "Housing",
            "Housing Bills",
            "Insurance",
            "Investing",
            "Maintenance",
            "Online",
            "Phone & WiFi",
            "Quick Food",
            "Recreation",
            "Saving",
            "Snacks",
            "Transportation"
        )

        var balance = mutableDoubleStateOf(0.0)
            private set

        var savings = mutableDoubleStateOf(0.0)
            private set

        fun Load(
            _balance: Double,
            _savings: Double
        ) {
            balance.value = _balance
            savings.value = _savings
        }

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

        private fun MoveToSavings(
            amount: Double
        ) {
            balance.value -= amount
            savings.value += amount
            Save()
        }

        private fun SpendSavings(
            amount: Double
        ) {
            savings.value -= amount
            Save()
        }

        fun NewTransaction(
            type: Int,
            category: Int,
            amount: Double,
            reason: String
        ) {
            when (type) {
                1 -> AddBalance(amount)
                2 -> RemoveBalance(amount)
                3 -> MoveToSavings(amount)
                4 -> SpendSavings(amount)
                else -> {
                    Log.e("Budget.kt","Invalid transaction type")
                }
            }

            HistoryTracker.LogTransaction(
                type, category, amount, reason
            )
        }

        private fun Save() {
            HistoryTracker.Save()
        }
    }
}