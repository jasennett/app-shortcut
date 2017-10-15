package com.jsennett.appshortcut.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface WidgetPackageDao {
    @Query("select * from widgetPackage where id = :id")
    fun findById(id: String): WidgetPackageEntity?

    @Query("select * from widgetPackage where packageName = :packageName")
    fun findByPackageName(packageName: String): List<WidgetPackageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: WidgetPackageEntity)

    @Delete
    fun delete(entity: WidgetPackageEntity)
}