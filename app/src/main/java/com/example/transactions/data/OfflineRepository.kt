package com.example.transactions.data

import kotlinx.coroutines.flow.Flow

class OfflineHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllTransactionsStream(): Flow<List<Transaction>> = historyDao.getAllTransactions()

    override suspend fun getTransaction(id: Int): Transaction = historyDao.getTransaction(id)

    override suspend fun insertTransaction(transaction: Transaction) = historyDao.insert(transaction)

    override suspend fun updateTransaction(transaction: Transaction) = historyDao.update(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) = historyDao.delete(transaction)

    override suspend fun deleteTransactions(transactions: List<Transaction>) = historyDao.delete(transactions)

    override suspend fun deleteAllTransactions() = historyDao.deleteAllTransactions()
}

class OfflineRecurringRepository(private val recurringDao: RecurringDao) : RecurringRepository {
    override fun getAllRecurringsStream(): Flow<List<Recurring>> = recurringDao.getAllRecurrings()

    override fun getAllRecurringsSync(): List<Recurring> = recurringDao.getAllRecurringsSync()

    override suspend fun getRecurring(id: Int): Recurring = recurringDao.getRecurring(id)

    override suspend fun insertRecurring(recurring: Recurring) = recurringDao.insert(recurring)

    override suspend fun updateRecurring(recurring: Recurring) = recurringDao.update(recurring)

    override suspend fun deleteRecurring(recurring: Recurring) = recurringDao.delete(recurring)
}