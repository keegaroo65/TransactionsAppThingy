package com.example.transactions.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val TAG = "DataStoreManager"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val BALANCE_PREF = intPreferencesKey("balance")
    }

    fun getBalance() : Flow<Int> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                }
                else {
                    throw exception
                }
            }.map { preferences ->
                val balance = preferences[BALANCE_PREF] ?: 0
                balance
            }
    }

    suspend fun setBalance(balance: Int) {
        dataStore.edit { preferences ->
            preferences[BALANCE_PREF] = balance
        }
    }

    suspend fun addBalance(balance: Int) {
        Log.d(TAG, "added balance $balance")
        getBalance().first {
            setBalance(it + balance)
            true
        }
    }

    suspend fun removeBalance(balance: Int) {
        getBalance().first {
            setBalance(it - balance)
            true
        }
    }
}