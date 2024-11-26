package com.shaikhabdulgani.customwidgetpoc

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.shaikhabdulgani.customwidgetpoc.util.enqueueWorkRequest

class QuoteWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH_QUOTE = "ACTION_REFRESH_WIDGET"

        fun notify(context: Context) {
            val intent = Intent(context, QuoteWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        views.setOnClickPendingIntent(R.id.btnRefresh, getRefreshPendingIntent(context))
        views.setTextViewText(R.id.tvQuote, getQuote(context))
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getRefreshPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, QuoteWidgetProvider::class.java).apply {
            action = ACTION_REFRESH_QUOTE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    private fun getQuote(context: Context): String? {
        val sharedPreferences =
            context.getSharedPreferences(SharePrefConst.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharePrefConst.QUOTE_KEY, "No quote available")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_QUOTE) {
            enqueueWorkRequest(context)
        }
    }
}
