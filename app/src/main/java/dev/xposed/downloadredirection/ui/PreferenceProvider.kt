package dev.xposed.downloadredirection.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.preference.PreferenceManager

typealias ArrStr = Array<String>

class PreferenceProvider : ContentProvider() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(): Boolean {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return true
    }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: ArrStr?): Int = 0
    override fun update(u: Uri, v: ContentValues?, s: String?, sa: ArrStr?): Int = 0
    override fun query(u: Uri, p: ArrStr?, s: String?, sa: ArrStr?, so: String?): Cursor? = null

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (arg == null || !sharedPreferences.contains(arg)) {
            return null
        }
        val result = Bundle()
        when (method) {
            "getBoolean" -> {
                result.putBoolean(arg, sharedPreferences.getBoolean(arg, false))
            }
            "getString" -> {
                result.putString(arg, sharedPreferences.getString(arg, null))
            }
            "getStringSet" -> {
                result.putStringArrayList(
                    arg,
                    sharedPreferences.getStringSet(arg, null)?.mapTo(ArrayList<String>()) { it }
                )
            }
            else -> return null
        }
        return result
    }
}