package dev.xposed.downloadredirection.ui

import android.app.Application
import android.content.Context
import dev.xposed.downloadredirection.ui.subscreen.downloader.Downloader

class Global : Application() {

    companion object {
        lateinit var context: Context
            private set

        fun getDefaultDownloaders() = mutableListOf(
            Downloader(
                "ADM",
                "com.dv.adm",
                "com.dv.adm.AEditor"
            ),
            Downloader(
                "ADMPro",
                "com.dv.adm.pay",
                "com.dv.adm.pay.AEditor"
            ),
            Downloader(
                "IDM",
                "idm.internet.download.manager",
                "idm.internet.download.manager.Downloader"
            ),
            Downloader(
                "IDM+",
                "idm.internet.download.manager.plus",
                "idm.internet.download.manager.Downloader"
            ),
            Downloader(
                "LoaderDroid",
                "org.zloy.android.downloader",
                "org.zloy.android.downloader.action.ADD_LOADING",
                true
            ),
            Downloader(
                "QKADM",
                "com.vanda_adm.vanda",
                "com.vanda.adm.friends",
                true
            ),
        )
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}