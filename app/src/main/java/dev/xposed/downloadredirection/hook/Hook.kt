package dev.xposed.downloadredirection.hook

import android.app.AndroidAppHelper
import android.app.Application
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.XModuleResources
import android.net.Uri
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.callbacks.XC_LoadPackage
import dev.xposed.downloadredirection.BuildConfig
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.MainActivity
import dev.xposed.downloadredirection.ui.subscreen.downloader.Downloader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Hook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private lateinit var moduleResources: XModuleResources
    private var debug = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            findAndHookMethod(
                MainActivity::class.qualifiedName,
                lpparam.classLoader,
                "isModuleActive",
                XC_MethodReplacement.returnConstant(true)
            )
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        moduleResources = XModuleResources.createInstance(startupParam.modulePath, null)
        findAndHookMethod(
            DownloadManager::class.java,
            "enqueue",
            DownloadManager.Request::class.java,
            enqueueHook
        )
    }

    private val enqueueHook = object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val currentApp = AndroidAppHelper.currentApplication()
            val pref = PreferenceFetcher(currentApp)
            debug = pref.getBoolean(
                moduleResources.getString(R.string.key_setting_debug_log),
                false
            )
            val moduleEnabled = pref.getBoolean(
                moduleResources.getString(R.string.key_setting_enable_module),
                false
            )
            if (!moduleEnabled) {
                log("Module disabled")
                return
            }
            val appliedApps = pref.getStringSet(
                moduleResources.getString(R.string.key_setting_filter),
                null
            )
            if (appliedApps == null || !appliedApps.contains(currentApp.packageName)) {
                log("No apps applied")
                return
            }
            log("Received download request")

            val currentDownloader = pref.getString(
                moduleResources.getString(R.string.key_setting_downloader),
                null
            )?.let {
                Json.decodeFromString<MutableList<Downloader>>(it).find { downloader ->
                    downloader.name == pref.getString(
                        moduleResources.getString(R.string.key_current_downloader),
                        null
                    )
                }
            }
            val notSpecifyDownloader = pref.getBoolean(
                moduleResources.getString(R.string.key_setting_not_specify_downloader),
                false
            )
            val uri = getObjectField(param.args[0], "mUri") as Uri
            log("Url to be redirected: $uri")
            if (!notSpecifyDownloader && redirectDownload(currentApp, uri, currentDownloader)
                || redirectDownload(currentApp, uri)
            ) {
                param.result = 0
                log("Redirection: succeeded!")
            } else {
                log("Redirection: failed!")
            }
        }
    }

    private fun redirectDownload(
        currentApp: Application,
        uri: Uri,
        downloader: Downloader? = null
    ): Boolean {
        val intent = Intent().apply {
            if (downloader != null) {
                if (downloader.useIntent) {
                    action = downloader.target
                    data = uri
                    `package` = downloader.packageName
                } else {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, uri.toString())
                    setClassName(downloader.packageName, downloader.target)
                }
                log("Selected downloader: $downloader")
            } else {
                action = Intent.ACTION_VIEW
                data = uri
                log("User selecting downloader")
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            currentApp.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    private fun log(msg: String) {
        if (debug) {
            Log.d("Xposed", "${moduleResources.getString(R.string.app_name)} -> $msg")
        }
    }
}