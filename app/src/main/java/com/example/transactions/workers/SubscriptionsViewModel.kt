package com.example.transactions.workers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.transactions.data.Recurring

class SubscriptionsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    internal fun test() {
        val chargeRequest = OneTimeWorkRequestBuilder<SubscriptionChargingWorker>()
            .build()

        workManager.beginWith(chargeRequest).enqueue()
    }

    internal fun test(recurring: Recurring) {
        val chargeRequest = OneTimeWorkRequestBuilder<SubscriptionChargingWorker>()
            .setInputData(workDataOf(
                "recurringId" to recurring.id
            ))
            .build()

        workManager.beginWith(chargeRequest).enqueue()
    }
}