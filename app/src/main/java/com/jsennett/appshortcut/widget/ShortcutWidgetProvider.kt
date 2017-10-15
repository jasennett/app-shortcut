package com.jsennett.appshortcut.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.widget.service.WidgetCleanupJobService

class ShortcutWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        WidgetCleanupJobService.scheduleIfNeeded(context)
        val updater = ShortcutWidgetUpdater(context, null, appWidgetManager)
        appWidgetIds.forEach { updater.updateWidget(it) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val service = WidgetPackageService(context)
        val packages = mutableSetOf<String>()
        for (appWidgetId in appWidgetIds) {
            service.findById(appWidgetId.toString())?.packageName?.let { packages.add(it) }
            service.delete(appWidgetId.toString())
        }
    }
}