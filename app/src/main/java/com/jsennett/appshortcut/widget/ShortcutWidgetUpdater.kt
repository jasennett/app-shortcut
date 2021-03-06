package com.jsennett.appshortcut.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jsennett.appshortcut.R
import com.jsennett.appshortcut.data.WidgetPackageModel
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.util.BitmapUtil

class ShortcutWidgetUpdater(private val context: Context, service: WidgetPackageService? = null, appWidgetManager: AppWidgetManager? = null) {
    private val widgetManager = appWidgetManager ?: AppWidgetManager.getInstance(context)
    private val widgetService = service ?: WidgetPackageService(context)

    fun updateWidget(widgetId: Int, widgetModel: WidgetPackageModel? = null) {
        val remoteView = ShortcutWidgetRemoteView(context)
        val widgetPackageInfo = widgetModel ?: widgetService.findById(widgetId.toString())
        if (widgetPackageInfo != null) {
            val intent = Intent(context, ShortcutLauncherActivity::class.java)
            intent.data = Uri.parse("content://appshortcut/${widgetPackageInfo.packageName}")
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            remoteView.setClickIntent(pendingIntent)
            remoteView.setLabel(widgetPackageInfo.appName)
            val bitmap = BitmapUtil.loadPackageBitmap(context, widgetPackageInfo.packageName)
            if (bitmap == null) {
                remoteView.setImage(R.drawable.placeholder_icon)
            } else {
                remoteView.setImage(bitmap)
            }
        } else {
            remoteView.setImage(R.drawable.placeholder_icon)
            remoteView.setLabel(context.getString(R.string.app_name))
        }

        widgetManager.updateAppWidget(widgetId, remoteView.remoteView)
    }
}