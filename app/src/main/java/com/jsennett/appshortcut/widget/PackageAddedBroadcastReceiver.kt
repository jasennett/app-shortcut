package com.jsennett.appshortcut.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jsennett.appshortcut.data.WidgetPackageService

class PackageAddedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("package_added", "got broadcast")
        val addedPackage = intent.dataString.removePrefix("package:")
        Log.d("package_added", "package added $addedPackage")
        val service = WidgetPackageService(context)
        val widgets = service.findByPackage(addedPackage)
        Log.d("package_added", "found widgets ${widgets.size}")
        if (widgets.isEmpty()) {
            return
        }

        val updater = ShortcutWidgetUpdater(context, service)
        widgets
                .mapNotNull { it.widgetId.toIntOrNull() }
                .forEach {
                    Log.d("package_added", "updating widget")
                    updater.updateWidget(it)
                }
    }
}