package com.jsennett.appshortcut.data

import android.arch.persistence.room.Room
import android.content.Context
import com.jsennett.appshortcut.data.room.WidgetDatabase
import com.jsennett.appshortcut.data.room.WidgetPackageDao
import com.jsennett.appshortcut.data.room.WidgetPackageEntity

class WidgetPackageService(context: Context) {
    private val database: WidgetDatabase by lazy { Room.databaseBuilder(context, WidgetDatabase::class.java, "widget-db").allowMainThreadQueries().build() }
    private val widgetPackages: WidgetPackageDao by lazy { database.widgetPackageDao() }

    fun findById(id: String): WidgetPackageModel? {
        return widgetPackages.findById(id)?.let { map(it) }
    }

    fun findByPackage(packageName: String): List<WidgetPackageModel> {
        return widgetPackages.findByPackageName(packageName).map { map(it) }
    }

    fun upsert(model: WidgetPackageModel) {
        widgetPackages.upsert(map(model))
    }

    fun delete(id: String) {
        widgetPackages.delete(WidgetPackageEntity(id, "", ""))
    }

    private fun map(entity: WidgetPackageEntity): WidgetPackageModel {
        return WidgetPackageModel(entity.widgetId, entity.packageName, entity.appName)
    }

    private fun map(model: WidgetPackageModel): WidgetPackageEntity {
        return WidgetPackageEntity(model.widgetId, model.packageName, model.appName)
    }
}