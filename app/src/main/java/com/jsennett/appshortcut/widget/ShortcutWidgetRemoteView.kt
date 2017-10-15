package com.jsennett.appshortcut.widget

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.jsennett.appshortcut.R

class ShortcutWidgetRemoteView(context: Context) {
    val remoteView = RemoteViews(context.packageName, R.layout.icon_with_label)

    fun setClickIntent(pendingIntent: PendingIntent) {
        remoteView.setOnClickPendingIntent(R.id.icon_and_label, pendingIntent)
    }

    fun setLabel(text: CharSequence) {
        remoteView.setTextViewText(R.id.shortcut_text, text)
    }

    fun setImage(resourceId: Int) {
        remoteView.setImageViewResource(R.id.shortcut_icon, resourceId)
    }

    fun setImage(bitmap: Bitmap) {
        remoteView.setImageViewBitmap(R.id.shortcut_icon, bitmap)
    }
}