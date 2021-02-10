package dev.xposed.downloadredirection.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dev.xposed.downloadredirection.ui.model.AppDatabase
import dev.xposed.downloadredirection.ui.model.SharedPrefsRepository
import dev.xposed.downloadredirection.ui.model.appliedapp.AppliedAppRepository
import dev.xposed.downloadredirection.ui.model.downloader.DownloaderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class Global : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
        val scope = CoroutineScope(SupervisorJob())
        val appDatabase by lazy { AppDatabase.getInstance(context, scope) }
        val downloaderRepository by lazy { DownloaderRepository(appDatabase.downloaderDao()) }
        val appliedAppRepository by lazy { AppliedAppRepository(appDatabase.appliedAppDao()) }
        val sharedPrefsRepository by lazy { SharedPrefsRepository(context) }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}