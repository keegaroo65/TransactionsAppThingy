package com.example.transactions

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class HistoryTracker {
    companion object {
        val STATS_PATH = "stats.txt"
        val HISTORY_PATH = "history.txt"

        var path: File? = null

        fun Load(
            context: Context
        ) {
            // Initialize the path variable for future use
            path = context.filesDir
            Log.i("HistoryTracker", path.toString())

            // Initialize the file if it doesn't exist
            val statsFile = File(path, STATS_PATH)
            if (!statsFile.exists()) {
                Save()
            }

            // Load the file to a usable string from internal storage
            val statsText = statsFile.readText()

            // Separate the data into usable parts
            val lines = statsText.split("\n")

            // Populate the file with valid data if it hasn't been created properly yet
            if (lines.size != 3) {
                Save()
            }

            // Convert strings into numeric information
            val timestamp = lines[0].toLong() // TODO: use this add budget routinely :D
            val balance = lines[1].toDouble()
            val savings = lines[2].toDouble()

            Budget.Load(balance, savings)
        }

        /*  Types:

            1 - Add balance
            2 - Spend balance
            3 - Move to savings
            4 - Spend savings
         */
        fun LogTransaction(
            type: Int,
            amount: Double,
            reason: String
        ) {
            val historyFile = File(path, HISTORY_PATH)

            historyFile.writeBytes(
                "${Utility.time()};$type;$amount;$reason\n".toByteArray() +
                        if (historyFile.exists()) historyFile.readBytes() else ByteArray(0)
            )
        }

        fun Save() {
            var statsFile = File(path, STATS_PATH)

            // Overwrite the stats file with the latest stats
            statsFile.writeText(
                "${Utility.time()}\n${Budget.balance.value}\n${Budget.savings.value}"
            )
        }
    }
}