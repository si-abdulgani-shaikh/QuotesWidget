package com.shaikhabdulgani.customwidgetpoc.worker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.shaikhabdulgani.customwidgetpoc.QuoteWidgetProvider
import com.shaikhabdulgani.customwidgetpoc.R
import com.shaikhabdulgani.customwidgetpoc.SharePrefConst
import com.shaikhabdulgani.customwidgetpoc.data.service.ApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class WidgetQuoteFetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService
) : CoroutineWorker(context, workerParams) {
    companion object{
        const val TAG = "WidgetQuoteFetchWorker"
    }

    private fun saveQuoteToPreferences(quote: String?) {
        val sharedPreferences = applicationContext.getSharedPreferences(SharePrefConst.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SharePrefConst.QUOTE_KEY, quote).apply()
    }

//    private fun notifyWidgetUpdate() {
//        val intent = Intent(applicationContext, QuoteWidgetProvider::class.java).apply {
//            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//        }
//        applicationContext.sendBroadcast(intent)
//    }

    override suspend fun doWork(): Result {
        return try {
            saveQuoteToPreferences("Loading...")
            QuoteWidgetProvider.notify(applicationContext)
            val response = apiService.getRandomQuote()
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    saveQuoteToPreferences(data.first().quote)
                    QuoteWidgetProvider.notify(applicationContext)
                }
                Result.success()
            } else {
                saveQuoteToPreferences("Error fetching data")
                QuoteWidgetProvider.notify(applicationContext)
                Result.retry()
            }
        } catch (e: Exception) {
            saveQuoteToPreferences("Error fetching data")
            QuoteWidgetProvider.notify(applicationContext)
            Result.retry()
        }
    }
}