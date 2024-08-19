package com.example.transactions.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * from transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * from transactions WHERE id = :id")
    suspend fun getTransaction(id: Int): Transaction

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Delete
    suspend fun delete(transactions: List<Transaction>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface RecurringDao {
    @Query("SELECT * from recurrings ORDER BY nextCharge ASC")
    fun getAllRecurrings(): Flow<List<Recurring>>

    @Query("SELECT * from recurrings ORDER BY nextCharge ASC")
    fun getAllRecurringsSync(): List<Recurring>

    @Query("SELECT * from recurrings WHERE id = :id")
    suspend fun getRecurring(id: Int): Recurring

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recurring: Recurring)

    @Update
    suspend fun update(recurring: Recurring)

    @Delete
    suspend fun delete(recurring: Recurring)
}