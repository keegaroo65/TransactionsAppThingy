package com.example.transactions

import android.app.Application
import com.example.transactions.data.AppContainer
import com.example.transactions.data.AppDataContainer

class TransactionsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}