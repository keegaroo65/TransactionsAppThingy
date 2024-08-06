package com.example.transactions.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class, Recurring::class], version = 2, exportSchema = false)
abstract class TransactionsDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun recurringDao(): RecurringDao

    companion object {
        @Volatile
        private var Instance: TransactionsDatabase? = null

        fun getDatabase(context: Context): TransactionsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TransactionsDatabase::class.java, "transactions_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it}
            }
        }
    }
}