package dev.xposed.downloadredirection.hook

import android.content.Context
import android.net.Uri

class PreferenceFetcher(private val context: Context) {

    private val uri = Uri.parse("content://dev.xposed.downloadredirection.preference")

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return context.contentResolver.call(uri, "getBoolean", key, null)
            ?.getBoolean(key) ?: defValue
    }

    fun getString(key: String, defValue: String?): String? {
        return context.contentResolver.call(uri, "getString", key, null)
            ?.getString(key) ?: defValue
    }

    fun getStringSet(key: String, defValue: Set<String>?): Set<String>? {
        return context.contentResolver.call(uri, "getStringSet", key, null)
            ?.getStringArrayList(key)?.toHashSet() ?: defValue
    }
}