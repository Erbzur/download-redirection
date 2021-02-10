package dev.xposed.downloadredirection.ui.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.xposed.downloadredirection.ui.model.appliedapp.AppliedApp
import dev.xposed.downloadredirection.ui.model.appliedapp.AppliedAppDao
import dev.xposed.downloadredirection.ui.model.downloader.Downloader
import dev.xposed.downloadredirection.ui.model.downloader.DownloaderDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Database(
    entities = [
        Downloader::class,
        AppliedApp::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun downloaderDao(): DownloaderDao
    abstract fun appliedAppDao(): AppliedAppDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope) = instance ?: synchronized(this) {
            val appContext = context.applicationContext
            Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "config"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    instance?.let {
                        scope.launch {
                            it.downloaderDao().insertAll(
                                getDefaultDownloaders(appContext)
                            )
                        }
                    }
                }
            }).build().also { instance = it }
        }

        fun getDefaultDownloaders(context: Context) =
            context.assets.open("default_downloaders.json")
                .bufferedReader().use { it.readText() }
                .let { Json.decodeFromString<MutableList<Downloader>>(it) }
    }
}