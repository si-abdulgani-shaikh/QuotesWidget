package com.shaikhabdulgani.customwidgetpoc.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.shaikhabdulgani.customwidgetpoc.worker.WidgetQuoteFetchWorker

fun enqueueWorkRequest(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<WidgetQuoteFetchWorker>()
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "DataFetchWork",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}
