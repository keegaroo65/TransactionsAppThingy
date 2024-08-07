package com.example.transactions.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurrings")
data class Recurring (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,

    val title: String,

    val details: String?,

    val active: Boolean,

    // 0: Happens same calendar day monthly; 1: Happens on a period of X days
    val type: Int,

    // Either day that it happens every month or period of days it takes to happen again, see `type`
    val period: Int,

    // When this is created and first charged
    val firstCharge: Long,

    // The last time it was charged, so the app knows when to make the next charge
    val lastCharge: Long,

    @ColumnInfo(defaultValue = "0")
    val nextCharge: Long

    /*
        TODO:
         - potential new type: occurs same calendar day every period of X months instead of days
         - preconfigured transaction categories for recurring transactions (default "recurring")
     */

)