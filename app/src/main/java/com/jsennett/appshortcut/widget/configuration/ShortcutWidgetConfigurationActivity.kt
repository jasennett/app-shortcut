package com.jsennett.appshortcut.widget.configuration

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.jsennett.appshortcut.R
import com.jsennett.appshortcut.data.WidgetPackageModel
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.util.BitmapUtil
import com.jsennett.appshortcut.widget.ShortcutWidgetUpdater
import com.jsennett.appshortcut.widget.service.WidgetCleanupJobService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ShortcutWidgetConfigurationActivity : AppCompatActivity() {
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val adapter: LauncherAppsAdapter = LauncherAppsAdapter()
    private val widgetId: Int by lazy { intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) }
    private val widgetService: WidgetPackageService by lazy { WidgetPackageService(this) }
    private val widgetUpdater: ShortcutWidgetUpdater by lazy { ShortcutWidgetUpdater(this, widgetService) }

    private var resolveInfoDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut_widget_configuration)

        setActivityResult(Activity.RESULT_CANCELED)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.addListener(object : ResolveInfoClickListener {
            override fun itemClicked(appItem: AppItem) {
                val packageName = appItem.resolveInfo.activityInfo.packageName

                // Save local record
                val model = WidgetPackageModel(widgetId.toString(), packageName, appItem.label.toString())
                widgetService.upsert(model)

                // Save the bitmap for the icon
                val bitmap = BitmapUtil.fromDrawable(appItem.resolveInfo.loadIcon(packageManager))
                BitmapUtil.savePackageIconToFile(this@ShortcutWidgetConfigurationActivity, bitmap, packageName)

                // Update widget view
                widgetUpdater.updateWidget(widgetId, model)

                // Schedule cleanup job if it is not already scheduled
                WidgetCleanupJobService.scheduleIfNeeded(this@ShortcutWidgetConfigurationActivity)

                setActivityResult(Activity.RESULT_OK)
                finish()
            }
        })

        resolveInfoDisposable = Single.fromCallable {
            val launcherIntent = Intent(Intent.ACTION_MAIN)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            packageManager.queryIntentActivities(launcherIntent, 0).sortedWith(ResolveInfo.DisplayNameComparator(packageManager)).map { AppItem(it, it.loadLabel(packageManager)) }
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> adapter.setApps(result) }
    }

    override fun onDestroy() {
        super.onDestroy()
        resolveInfoDisposable?.dispose()
        resolveInfoDisposable = null
    }

    private fun setActivityResult(resultCode: Int) {
        val intent = Intent()
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(resultCode, intent)
    }
}
