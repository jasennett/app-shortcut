package com.jsennett.appshortcut.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import java.io.File
import java.io.FileOutputStream

object BitmapConverter {
    private const val FILE_SUFFIX = "_icon.png"

    fun fromDrawable(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun savePackageIconToFile(context: Context, bitmap: Bitmap, packageName: String) {
        val file = getFile(context, packageName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {}
    }

    fun loadPackageBitmap(context: Context, packageName: String): Bitmap? {
        val file = getFile(context, packageName)
        if (!file.exists()) {
            return null
        }

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    fun deletePackageBitmap(context: Context, packageName: String) {
        val file = getFile(context, packageName)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getFile(context: Context, packageName: String): File = File(context.getExternalFilesDir(null).absolutePath, "$packageName$FILE_SUFFIX")
}