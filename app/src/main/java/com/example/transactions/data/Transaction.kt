package com.example.transactions.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: Int,

    val category: Int,

    val amount: Double,

    val reason: String,

    val timestamp: Long
)