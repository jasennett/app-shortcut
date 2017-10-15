package com.jsennett.appshortcut.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.jsennett.appshortcut.R
import com.jsennett.appshortcut.data.WidgetPackageService

class ShortcutWidgetUpdater(private val context: Context, service: WidgetPackageService? = null, appWidgetManager: AppWidgetManager? = null) {
    private val widgetManager = appWidgetManager ?: AppWidgetManager.getInstance(context)
    private val widgetService = service ?: WidgetPackageService(context)

    fun updateWidget(widgetId: Int) {
        val intent = Intent(context, ShortcutLauncherIntentService::class.java)
        intent.data = Uri.parse("content://appshortcut/$widgetId")
        val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
        val remoteView = ShortcutWidgetRemoteView(context)
        remoteView.setClickIntent(pendingIntent)

        val widgetPackageInfo = widgetService.findById(widgetId.toString())
        if (widgetPackageInfo != null) {
            try {
                val appInfo = context.packageManager.getApplicationInfo(widgetPackageInfo.packageName, PackageManager.GET_META_DATA)
                remoteView.setImage(appInfo.loadIcon(context.packageManager))
                remoteView.setLabel(appInfo.loadLabel(context.packageManager))
            } catch (e: Exception) {
                remoteView.setImage(R.drawable.ic_missing_accent_24dp)
                if (widgetPackageInfo.appName != null ) {
                    remoteView.setLabel(widgetPackageInfo.appName)
                } else {
                    remoteView.setLabel(context.getString(R.string.app_name))
                }
            }
        } else {
            remoteView.setImage(R.drawable.ic_missing_accent_24dp)
            remoteView.setLabel(context.getString(R.string.app_name))
        }

        widgetManager.updateAppWidget(widgetId, remoteView.remoteView)
    }
}