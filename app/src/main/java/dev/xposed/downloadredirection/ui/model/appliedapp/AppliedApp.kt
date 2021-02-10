package dev.xposed.downloadredirection.ui.model.appliedapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appliedapps")
data class AppliedApp(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,
)