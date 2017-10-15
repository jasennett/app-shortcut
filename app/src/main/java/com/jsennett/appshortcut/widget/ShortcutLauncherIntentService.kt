package com.jsennett.appshortcut.widget

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.jsennett.appshortcut.R

class ShortcutLauncherIntentService : IntentService("ShortcutLauncherIntentService") {
    private lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent?.data == null) {
            return
        }

        val packageName = intent.data.path.trim('/')
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            handler.post {
                Toast.makeText(this, getString(R.string.not_installed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}