package dev.xposed.downloadredirection.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Bundle

class ConfigProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "dev.xposed.downloadredirection.configprovider"
        const val KEY_MODULE_ENABLED = "moduleEnabled"
        const val KEY_DEBUG = "debug"
        const val KEY_NOT_SPECIFY_DOWNLOADER = "notSpecifyDownloader"
        const val KEY_APPLIED_APPS = "appliedapps"
        const val KEY_CURRENT_DOWNLOADER = "currentdownloader"
        const val KEY_RESULT = "result"

        private const val CODE_APPLIED_APPS = 1
        private const val CODE_CURRENT_DOWNLOADER = 2
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, KEY_APPLIED_APPS, CODE_APPLIED_APPS)
            addURI(AUTHORITY, KEY_CURRENT_DOWNLOADER, CODE_CURRENT_DOWNLOADER)
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val result = Bundle()
        when (method) {
            KEY_MODULE_ENABLED -> {
                Global.sharedPrefsRepository.moduleEnabled?.let {
                    result.putBoolean(KEY_RESULT, it)
                } ?: return null
            }
            KEY_DEBUG -> {
                Global.sharedPrefsRepository.debug?.let {
                    result.putBoolean(KEY_RESULT, it)
                } ?: return null
            }
            KEY_NOT_SPECIFY_DOWNLOADER -> {
                Global.sharedPrefsRepository.notSpecifyDownloader?.let {
                    result.putBoolean(KEY_RESULT, it)
                } ?: return null
            }
            else -> return null
        }
        return result
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = when (uriMatcher.match(uri)) {
        CODE_APPLIED_APPS -> {
            Global.appDatabase.appliedAppDao().loadAllRaw()
        }
        CODE_CURRENT_DOWNLOADER -> {
            Global.sharedPrefsRepository.currentDownloaderId?.let {
                Global.appDatabase.downloaderDao().findByIdRaw(it)
            }
        }
        else -> null
    }

    override fun onCreate() = true
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, vals: ContentValues?): Uri? = null
    override fun delete(uri: Uri, sel: String?, selArgs: Array<String>?) = 0
    override fun update(uri: Uri, v: ContentValues?, s: String?, sa: Array<String>?) = 0
}