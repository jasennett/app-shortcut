package com.jsennett.appshortcut.data.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "widgetPackage", indices = arrayOf(Index("packageName")))
data class WidgetPackageEntity(
        @ColumnInfo(name = "id") @PrimaryKey val widgetId: String,
        @ColumnInfo val packageName: String,
        @ColumnInfo val appName: String?)