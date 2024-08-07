package com.example.transactions.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.transactions.Utility
import com.example.transactions.data.AppDataContainer
import com.example.transactions.data.Recurring
import com.example.transactions.data.RecurringRepository
import com.example.transactions.data.Transaction

private const val TAG = "SubChargeWorker"

class SubscriptionChargingWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val recurringId = inputData.getInt("recurringId", -1)

            val appDataContainer = AppDataContainer(appContext)
            val dataStore = appDataContainer.dataStore
            val historyRepository = appDataContainer.historyRepository
            val recurringRepository = appDataContainer.recurringRepository

            if (recurringId == -1) {
                val subscriptions = getSubscriptions(recurringRepository)
                chargeSubscriptions(
                    appDataContainer,
                    subscriptions
                )
            }
            else {
                chargeSubscription(
                    appDataContainer,
                    recurringRepository.getRecurring(recurringId)
                )
            }

            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error charging subscriptions", exception)
            Result.failure()
        }
    }

    private suspend fun getSubscriptions(recurringRepository: RecurringRepository): List<Recurring> {
        return recurringRepository.getAllRecurringsSync()
    }

    private suspend fun chargeSubscriptions(appDataContainer: AppDataContainer, subscriptions: List<Recurring>) {
        Log.d(TAG, "charging ${subscriptions.count()} subscriptions !!")

        for (subscription in subscriptions) {
            chargeSubscription(appDataContainer, subscription)
        }

        Log.d(TAG, "workManagerTest ended ${Utility.time()}")
    }

    private suspend fun chargeSubscription(appDataContainer: AppDataContainer, _recurring: Recurring) {
        val now = Utility.time()
        var recurring = _recurring

        var nextCharge = recurring.nextCharge

        // Recursively check if a new charge must be applied, apply it if so, and continue.
        //  this is in case subscription charges aren't checked for more than one charge period
        //  to ensure that no transactions are missed
        while (nextCharge < now) {
            addTransaction(appDataContainer, recurring)

            recurring = recurring.copy(
                lastCharge = nextCharge,
                nextCharge = Utility.nextCharge(recurring, nextCharge)
            )

            nextCharge = recurring.nextCharge
        }

        appDataContainer.recurringRepository.updateRecurring(recurring)
    }

    private suspend fun addTransaction(appDataContainer: AppDataContainer, recurring: Recurring) {
        Log.d(TAG, "charging ${recurring.amount} to ${recurring.title}")
        appDataContainer.historyRepository.insertTransaction(
            Transaction(
                type = 1, // Remove balance
                category = 13,
                amount = recurring.amount,
                reason = recurring.title,
                timestamp = recurring.nextCharge
            )
        )

        appDataContainer.dataStore.removeBalance(recurring.amount) // TODO: adding-balance recurring types? :eyes:
    }
}