package com.jsennett.appshortcut.widget

import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import com.jsennett.appshortcut.R
import com.jsennett.appshortcut.util.BitmapConverter

class ShortcutWidgetRemoteView(context: Context) {
    val remoteView = RemoteViews(context.packageName, R.layout.icon_with_label)

    fun setClickIntent(pendingIntent: PendingIntent) {
        remoteView.setOnClickPendingIntent(R.id.container, pendingIntent)
    }

    fun setLabel(text: CharSequence) {
        remoteView.setTextViewText(R.id.shortcut_text, text)
    }

    fun setImage(resourceId: Int) {
        remoteView.setImageViewResource(R.id.shortcut_icon, resourceId)
    }

    fun setImage(drawable: Drawable) {
        remoteView.setImageViewBitmap(R.id.shortcut_icon, BitmapConverter.fromDrable(drawable))
    }
}