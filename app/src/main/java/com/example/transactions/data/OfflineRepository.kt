package com.example.transactions.data

import kotlinx.coroutines.flow.Flow

class OfflineHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllTransactionsStream(): Flow<List<Transaction>> = historyDao.getAllTransactions()

    override suspend fun getTransaction(id: Int): Transaction = historyDao.getTransaction(id)

    override suspend fun insertTransaction(transaction: Transaction) = historyDao.insert(transaction)

    override suspend fun updateTransaction(transaction: Transaction) = historyDao.update(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) = historyDao.delete(transaction)

    override suspend fun deleteTransactions(transactions: List<Transaction>) = historyDao.delete(transactions)
}

class OfflineRecurringRepository(private val recurringDao: RecurringDao) : RecurringRepository {

}