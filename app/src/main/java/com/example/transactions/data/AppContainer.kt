package com.example.transactions.data

import android.content.Context

interface AppContainer {
    val historyRepository: HistoryRepository
    val recurringRepository: RecurringRepository
    val dataStore: DataStoreManager
}

class AppDataContainer(private val context: Context): AppContainer {
    override val historyRepository: HistoryRepository by lazy {
        OfflineHistoryRepository(TransactionsDatabase.getDatabase(context).historyDao())
    }

    override val recurringRepository: RecurringRepository by lazy {
        OfflineRecurringRepository(TransactionsDatabase.getDatabase(context).recurringDao())
    }

    override val dataStore: DataStoreManager by lazy {
        DataStoreManager(context)
    }
}