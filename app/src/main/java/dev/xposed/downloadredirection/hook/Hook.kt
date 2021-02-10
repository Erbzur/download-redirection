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
import dev.xposed.downloadredirection.ui.model.downloader.Downloader
import dev.xposed.downloadredirection.ui.view.MainActivity

class Hook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private lateinit var moduleName: String
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
        with(XModuleResources.createInstance(startupParam.modulePath, null)) {
            moduleName = getString(R.string.app_name)
        }
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
            val config = ModuleConfig(currentApp)
            debug = config.debug

            if (!config.moduleEnabled) {
                log("Module not enabled")
                return
            }
            if (!config.appliedApps.contains(currentApp.packageName)) {
                log("Not applied appliedapp: ${currentApp.packageName}")
                return
            }
            log("Received download request")

            val uri = getObjectField(param.args[0], "mUri") as Uri
            log("Url to be redirected: $uri")
            if (!config.notSpecifyDownloader
                && redirectDownload(currentApp, uri, config.currentDownloader)
                || redirectDownload(currentApp, uri)
            ) {
                param.result = 0
                log("Redirection succeeded!")
            } else {
                log("Redirection failed!")
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
            Log.d("Xposed", "$moduleName -> $msg")
        }
    }
}