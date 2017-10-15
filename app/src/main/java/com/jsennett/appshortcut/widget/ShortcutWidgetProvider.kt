package com.jsennett.appshortcut.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.jsennett.appshortcut.data.WidgetPackageService

class ShortcutWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val updater = ShortcutWidgetUpdater(context, null, appWidgetManager)
        for (appWidgetId in appWidgetIds) {
            updater.updateWidget(appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val service = WidgetPackageService(context)
        for (appWidgetId in appWidgetIds) {
            service.delete(appWidgetId.toString())
        }
    }
}