package com.example.transactions.data

import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllTransactionsStream(): Flow<List<Transaction>>

    suspend fun getTransaction(id: Int): Transaction

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun deleteTransactions(transactions: List<Transaction>)
}

interface RecurringRepository {

}