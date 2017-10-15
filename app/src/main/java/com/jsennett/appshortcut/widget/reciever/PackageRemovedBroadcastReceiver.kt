package com.jsennett.appshortcut.widget.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.widget.ShortcutWidgetUpdater

class PackageRemovedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val addedPackage = intent.dataString.removePrefix("package:")
        val service = WidgetPackageService(context)
        val widgets = service.findByPackage(addedPackage)
        if (widgets.isEmpty()) {
            return
        }

        val updater = ShortcutWidgetUpdater(context, service)
        widgets
                .mapNotNull { it.widgetId.toIntOrNull() }
                .forEach {
                    updater.updateWidget(it)
                }
    }
}