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
            //Log.i("HistoryTracker", path.toString())

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

            Save()
        }

        // TODO: add time-range specific/max number (etc) methods to improve performance
        // TODO: investigate what an ArrayList is vs List
        fun GetAllHistory(): ArrayList<TransactionLog> {
            /*val historyFile = """1706579373459;2;249.99;many cookies!
1706423344619;2;0.02;hi
1705858140000;3;96.75;bus pass mayhaps
1705344789000;1;20.0;for clothing from mom
1695877020000;3;35.0;new years
1653306369000;4;428.19;pc stuff
1653305369000;1;5.29;filler
1653205369000;1;5.29;filler1
1653105369000;1;5.29;filler2
1653005369000;1;5.29;filler3
1652305369000;1;5.29;filler4
1651305369000;1;5.29;filler5
1650305369000;1;5.29;filler6
1643305369000;1;5.29;filler7
1633305369000;1;5.29;filler8
1623305369000;1;5.29;filler9
1613305369000;1;5.29;filler10
1603305369000;1;5.29;filler11
1553305369000;1;5.29;filler12"""*/
            val historyFile = File(path, HISTORY_PATH)

            //File(path, HISTORY_PATH).writeText(historyFile)

            var transactions = ArrayList<TransactionLog>(5)

            //historyFile.split("\n").forEach{
            historyFile.forEachLine{
                if (it.isNotEmpty()) {
                    // Format: timestamp;type;amount;reason - 0;1;2;3
                    val arguments = it.split(";")

                    transactions.add(TransactionLog(
                        arguments[1].toInt(),
                        arguments[2].toInt(),
                        arguments[3].toDouble(),
                        arguments[4],
                        arguments[0].toLong()
                    ))
                }
            }

            Log.i("trans",transactions.toString())

            return transactions
        }

        /*  Types:
            1 - Add balance
            2 - Spend balance
            3 - Move to savings
            4 - Spend savings
         */
        fun LogTransaction(
            type: Int,
            category: Int,
            amount: Double,
            reason: String
        ) {
            val historyFile = File(path, HISTORY_PATH)

            historyFile.writeBytes(
                "${Utility.time()};$type;$category;$amount;$reason\n".toByteArray() +
                        if (historyFile.exists()) historyFile.readBytes() else ByteArray(0)
            )
        }

        fun Save() {
            var statsFile = File(path, STATS_PATH)

            // Overwrite the stats file with the latest stats
            statsFile.writeText(
                "${Utility.time()}\n${Budget.balance.value}\n${Budget.savings.value}"
            )

            var historyFile = File(path, HISTORY_PATH)

            historyFile.writeText("""1714500623483;1;10;6.0;hfg
1707843171638;3;10;80.03;Venmo to me :)
1706735756874;1;10;725.0;sold ur mom
1706735726365;3;10;2.06;hi
1706735489722;2;10;694.2;that ass
1706646384704;2;10;32.75;lil macs
1706635330660;1;10;67.25;yippee
1706588154362;0;10;21.56;chez!!
1706579373459;2;10;249.99;many cookies!
1706423344619;2;10;0.02;hi
1705858140000;3;10;96.75;bus pass mayhaps
1705344789000;1;10;20.0;for clothing from mom
1695877020000;3;10;35.0;new years
1653306369000;4;10;428.19;pc stuff
1653305369000;1;10;5.29;filler
1653205369000;1;10;5.29;filler1
1653105369000;1;10;5.29;filler2
1653005369000;1;10;5.29;filler3
1652305369000;1;10;5.29;filler4
1651305369000;1;10;5.29;filler5
1650305369000;1;10;5.29;filler6
1643305369000;1;10;5.29;filler7
1633305369000;1;10;5.29;filler8
1623305369000;1;10;5.29;filler9
1613305369000;1;10;5.29;filler10
1603305369000;1;10;5.29;filler11
1553305369000;1;10;5.29;filler12""")
        }
    }
}

data class TransactionLog(
    val type: Int,
    val category: Int,
    val amount: Double,
    val reason: String,
    val timestamp: Long
)