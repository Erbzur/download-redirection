package dev.xposed.downloadredirection.ui.model.downloader

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "downloaders", indices = [Index(value = ["name"], unique = true)])
data class Downloader(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    val target: String,
    @ColumnInfo(name = "use_intent") val useIntent: Boolean = false,
)