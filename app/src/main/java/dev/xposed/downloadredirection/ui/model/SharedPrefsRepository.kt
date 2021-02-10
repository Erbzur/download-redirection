package dev.xposed.downloadredirection.ui.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import dev.xposed.downloadredirection.R

class SharedPrefsRepository(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val editor by lazy { prefs.edit() }
    private val keyEnableModule = context.getString(R.string.key_setting_enable_module)
    private val keyDebugLog = context.getString(R.string.key_setting_debug_log)
    private val keyNotSpectifyDownloader =
        context.getString(R.string.key_setting_not_specify_downloader)
    private val keyCurrentDownloader = context.getString(R.string.key_current_downloader)
    private val keyShowSystemApps = context.getString(R.string.key_menu_filter_show_system_apps)
    private val keyShowOnlyAppliedApps =
        context.getString(R.string.key_menu_filter_show_only_applied_apps)

    private val _currentDownloaderIdLiveData = MutableLiveData(currentDownloaderId)
    val currentDownloaderIdLiveData: LiveData<Int?> = _currentDownloaderIdLiveData

    val moduleEnabled get() = getBoolean(keyEnableModule)
    val debug get() = getBoolean(keyDebugLog)
    val notSpecifyDownloader get() = getBoolean(keyNotSpectifyDownloader)
    var currentDownloaderId
        get() = getInt(keyCurrentDownloader)
        set(value) {
            if (value != null) {
                editor.putInt(keyCurrentDownloader, value).apply()
            } else {
                editor.remove(keyCurrentDownloader).apply()
            }
            _currentDownloaderIdLiveData.value = value
        }
    var showSystemApps
        get() = prefs.getBoolean(keyShowSystemApps, true)
        set(value) = editor.putBoolean(keyShowSystemApps, value).apply()
    var showOnlyAppliedApps
        get() = prefs.getBoolean(keyShowOnlyAppliedApps, false)
        set(value) = editor.putBoolean(keyShowOnlyAppliedApps, value).apply()

    private fun getBoolean(key: String) =
        if (prefs.contains(key)) prefs.getBoolean(key, false) else null

    private fun getInt(key: String) = if (prefs.contains(key)) prefs.getInt(key, -1) else null
}