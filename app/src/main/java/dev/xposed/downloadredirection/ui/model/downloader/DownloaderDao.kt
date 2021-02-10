package dev.xposed.downloadredirection.ui.model.downloader

import android.database.Cursor
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloaderDao {
    @Query("SELECT * FROM downloaders")
    fun loadAll(): Flow<List<Downloader>>

    @Query("SELECT * FROM downloaders WHERE id = :id")
    fun findByIdRaw(id: Int): Cursor

    @Query("SELECT * FROM downloaders WHERE id = :id")
    suspend fun findById(id: Int): Downloader?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(downloaders: List<Downloader>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(downloader: Downloader)

    @Query("DELETE FROM downloaders")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(downloader: Downloader)
}