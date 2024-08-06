package com.example.transactions.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transactions.data.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {
    val balance: StateFlow<Double> = dataStore.getBalance()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = 0.0
        )

//    val balance = _balance.asStateFlow()

    fun add25() {
        viewModelScope.launch {
            dataStore.addBalance(25.0)
        }
    }

    fun resetBalance() {
        viewModelScope.launch {
            dataStore.setBalance(0.0)
        }
    }

    fun remove25() {
        viewModelScope.launch {
            dataStore.removeBalance(25.0)
        }
    }
}