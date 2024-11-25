package com.shaikhabdulgani.customwidgetpoc

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.shaikhabdulgani.customwidgetpoc.util.enqueueWorkRequest

class QuoteWidgetProvider : AppWidgetProvider() {

    companion object{
        const val ACTION_REFRESH_QUOTE = "ACTION_REFRESH_WIDGET"

        fun notify(context: Context){
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

        val pendingIntent = getPendingIntent(context)
        views.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent)
        Log.d("QuoteWidgetProvider", "updateAppWidget: Here")
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingIntent(context: Context): PendingIntent {
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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_QUOTE){
            enqueueWorkRequest(context)
        } else if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, QuoteWidgetProvider::class.java))
            for (appWidgetId in appWidgetIds) {
                updateWidgetWithQuote(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun updateWidgetWithQuote(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val sharedPreferences = context.getSharedPreferences(SharePrefConst.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val quote = sharedPreferences.getString(SharePrefConst.QUOTE_KEY, "No quote available")

        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.setTextViewText(R.id.tvQuote, quote)
        val pendingIntent = getPendingIntent(context)
        views.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
