package dev.xposed.downloadredirection.ui.subscreen.downloader

import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.Global
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DownloaderViewModel : ViewModel() {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Global.context)
    private val downloaders =
        sharedPreferences.getString(
            Global.context.getString(R.string.key_setting_downloader),
            null
        )?.let {
            Json.decodeFromString(it)
        } ?: Global.getDefaultDownloaders()
    private var downloadersAdapter: DownloadersAdapter? = null

    private fun saveDownloaders() {
        sharedPreferences.edit()
            .putString(
                Global.context.getString(R.string.key_setting_downloader),
                Json.encodeToString(downloaders)
            )
            .apply()
    }

    private fun saveCurrentDownloader(currentDownloaderName: String) {
        saveDownloaders()
        sharedPreferences.edit()
            .putString(
                Global.context.getString(R.string.key_current_downloader),
                currentDownloaderName
            )
            .apply()
    }

    fun putDownloader(downloader: Downloader, position: Int): Boolean {
        for (index in downloaders.indices) {
            if (downloaders[index].name == downloader.name && index != position) {
                return false
            }
        }
        if (position == -1) {
            downloaders.add(downloader)
        } else {
            downloaders[position] = downloader
        }
        downloadersAdapter?.downloaders = downloaders
        saveDownloaders()
        return true
    }

    fun removeDownloader(index: Int) {
        val downloader = downloaders.removeAt(index)
        downloadersAdapter?.downloaders = downloaders
        saveDownloaders()
        val currentDownloaderName = sharedPreferences.getString(
            Global.context.getString(R.string.key_current_downloader),
            null
        ) ?: ""
        if (downloader.name == currentDownloaderName) {
            sharedPreferences.edit()
                .remove(Global.context.getString(R.string.key_current_downloader))
                .apply()
        }
    }

    fun resetDefaultDownloaders() {
        downloaders.clear()
        downloaders.addAll(Global.getDefaultDownloaders())
        downloadersAdapter?.downloaders = downloaders
        saveDownloaders()
        val currentDownloaderName = sharedPreferences.getString(
            Global.context.getString(R.string.key_current_downloader),
            null
        ) ?: ""
        if (downloaders.count { it.name == currentDownloaderName } == 0) {
            sharedPreferences.edit()
                .remove(Global.context.getString(R.string.key_current_downloader))
                .apply()
        }
    }

    fun getDownloadersAdapter(launchEditDialog: (Downloader, Int) -> Unit): DownloadersAdapter? {
        val currentDownloaderName = sharedPreferences.getString(
            Global.context.getString(R.string.key_current_downloader),
            null
        ) ?: ""
        downloadersAdapter = DownloadersAdapter(
            downloaders,
            currentDownloaderName,
            launchEditDialog,
            ::saveCurrentDownloader,
        )
        return downloadersAdapter
    }
}