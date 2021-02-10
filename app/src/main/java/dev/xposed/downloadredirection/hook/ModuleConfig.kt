package dev.xposed.downloadredirection.hook

import android.content.Context
import android.database.Cursor
import android.net.Uri
import dev.xposed.downloadredirection.ui.ConfigProvider
import dev.xposed.downloadredirection.ui.model.downloader.Downloader

class ModuleConfig(private val context: Context) {

    companion object {
        private val configUri = listOf(
            "base",
            ConfigProvider.KEY_APPLIED_APPS,
            ConfigProvider.KEY_CURRENT_DOWNLOADER,
        ).associateBy({ key -> key }, { value ->
            Uri.parse(
                "content://${ConfigProvider.AUTHORITY}/${value.takeIf { it != "base" } ?: ""}"
            )
        })
    }

    val moduleEnabled = getConfig(ConfigProvider.KEY_MODULE_ENABLED, false)
    val debug = getConfig(ConfigProvider.KEY_DEBUG, false)
    val notSpecifyDownloader = getConfig(ConfigProvider.KEY_NOT_SPECIFY_DOWNLOADER, false)
    val appliedApps = getConfigRaw(ConfigProvider.KEY_APPLIED_APPS) {
        mutableSetOf<String>().apply {
            do {
                add(it.getString(it.getColumnIndex("package_name")))
            } while (it.moveToNext())
        }
    } ?: emptySet()
    val currentDownloader = getConfigRaw(ConfigProvider.KEY_CURRENT_DOWNLOADER) {
        Downloader(
            id = it.getInt(it.getColumnIndex("id")),
            name = it.getString(it.getColumnIndex("name")),
            packageName = it.getString(it.getColumnIndex("package_name")),
            target = it.getString(it.getColumnIndex("target")),
            useIntent = it.getInt(it.getColumnIndex("use_intent")) == 1,
        )
    }

    private fun getConfig(configName: String, defValue: Boolean) =
        context.contentResolver.call(configUri.getValue("base"), configName, null, null)
            ?.getBoolean(ConfigProvider.KEY_RESULT, defValue) ?: defValue

    private fun <R> getConfigRaw(configName: String, initializer: (Cursor) -> R) =
        context.contentResolver.query(configUri.getValue(configName), null, null, null, null)
            ?.use { if (it.moveToFirst()) initializer(it) else null }
}