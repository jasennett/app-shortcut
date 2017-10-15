package com.jsennett.appshortcut.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(WidgetPackageEntity::class), version = 1, exportSchema = false)
abstract class WidgetDatabase : RoomDatabase() {
    abstract fun widgetPackageDao(): WidgetPackageDao
}