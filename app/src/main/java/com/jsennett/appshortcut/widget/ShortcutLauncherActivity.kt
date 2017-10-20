package com.jsennett.appshortcut.widget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jsennett.appshortcut.R

class ShortcutLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.data.path.trim('/')
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, getString(R.string.not_installed), Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}