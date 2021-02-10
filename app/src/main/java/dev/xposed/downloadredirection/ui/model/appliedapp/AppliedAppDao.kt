package dev.xposed.downloadredirection.ui.model.appliedapp

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppliedAppDao {
    @Query("SELECT COUNT(*) FROM appliedapps")
    fun count(): Flow<Int>

    @Query("SELECT package_name FROM appliedapps")
    fun loadAllRaw(): Cursor

    @Query("SELECT package_name FROM appliedapps")
    suspend fun loadAll(): List<String>

    @Query("INSERT INTO appliedapps VALUES (:packageName)")
    suspend fun insert(packageName: String)

    @Query("DELETE FROM appliedapps WHERE package_name = :packageName")
    suspend fun delete(packageName: String)
}