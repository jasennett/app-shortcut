package com.jsennett.appshortcut.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.util.BitmapConverter

class ShortcutWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val updater = ShortcutWidgetUpdater(context, null, appWidgetManager)
        for (appWidgetId in appWidgetIds) {
            updater.updateWidget(appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val service = WidgetPackageService(context)
        val packages = mutableSetOf<String>()
        for (appWidgetId in appWidgetIds) {
            service.findById(appWidgetId.toString())?.packageName?.let { packages.add(it) }
            service.delete(appWidgetId.toString())
        }

        packages
                .filter { service.findByPackage(it).isEmpty() }
                .forEach { BitmapConverter.deletePackageBitmap(context, it) }
    }
}