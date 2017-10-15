package com.jsennett.appshortcut.widget.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.jsennett.appshortcut.data.WidgetPackageModel
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.util.BitmapConverter
import com.jsennett.appshortcut.widget.ShortcutWidgetUpdater

class PackageAddedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val addedPackage = intent.dataString.removePrefix("package:")
        val service = WidgetPackageService(context)
        val widgets = service.findByPackage(addedPackage)
        if (widgets.isEmpty()) {
            return
        }

        // Update the saved icon for this package
        try {
            val appInfo = context.packageManager.getApplicationInfo(addedPackage, PackageManager.GET_META_DATA)
            val icon = appInfo.loadIcon(context.packageManager)
            val bitmap = BitmapConverter.fromDrawable(icon)
            BitmapConverter.savePackageIconToFile(context, bitmap, addedPackage)

            // Update label for any shortcuts for this package
            val label = appInfo.loadLabel(context.packageManager)
            widgets
                    .map { WidgetPackageModel(it.widgetId, it.packageName, label.toString()) }
                    .forEach { service.upsert(it) }
        } catch (e: Exception) {}

        // Update all widgets
        val updater = ShortcutWidgetUpdater(context, service)
        widgets
                .mapNotNull { it.widgetId.toIntOrNull() }
                .forEach {
                    updater.updateWidget(it)
                }
    }
}