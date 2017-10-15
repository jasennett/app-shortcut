package com.jsennett.appshortcut.widget.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.jsennett.appshortcut.data.WidgetPackageService
import com.jsennett.appshortcut.util.BitmapUtil
import com.jsennett.appshortcut.widget.ShortcutWidgetProvider
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WidgetCleanupJobService : JobService() {
    private var disposable: Disposable? = null

    override fun onStopJob(params: JobParameters): Boolean {
        val disposable = this.disposable
        if (disposable?.isDisposed == false) {
            disposable.dispose()
            this.disposable = null
            return true
        }

        return false
    }

    override fun onStartJob(params: JobParameters): Boolean {
        disposable?.dispose()
        disposable = Completable.create {
            val context = this@WidgetCleanupJobService
            val service = WidgetPackageService(context)
            val bitmapPackages = BitmapUtil.getBitmapPackages(context)

            for (packageName in bitmapPackages) {
                if (it.isDisposed) {
                    return@create
                }

                if (service.findByPackage(packageName).isEmpty()) {
                    BitmapUtil.deletePackageBitmap(context, packageName)
                }
            }

            // Cancel job if no widgets exist anymore
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val remainingWidgets = appWidgetManager.getAppWidgetIds(ComponentName(context, ShortcutWidgetProvider::class.java))?.isNotEmpty() ?: false
            if (!remainingWidgets) {
                val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                jobScheduler.cancel(WidgetCleanupJobService.JOB_ID)
            }

            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    disposable = null
                    jobFinished(params, false)
                }


        return true
    }

    companion object {
        const val JOB_ID = 1

        fun scheduleIfNeeded(context: Context) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val job = jobScheduler.allPendingJobs.firstOrNull { it.id == WidgetCleanupJobService.JOB_ID }
            if (job == null) {
                val jobInfo = JobInfo.Builder(WidgetCleanupJobService.JOB_ID, ComponentName(context, WidgetCleanupJobService::class.java))
                        .setPeriodic(1000 * 60 * 60 * 24)
                        .setPersisted(true)
                        .build()
                jobScheduler.schedule(jobInfo)
            }
        }
    }
}
