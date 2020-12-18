package dev.xposed.downloadredirection.ui.subscreen.filter

import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.Global
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterViewModel : ViewModel() {

    val isLoadingLiveData = MutableLiveData(false)
    private val appList = mutableListOf<AppItem>()
    private var filteredAppList = appList as List<AppItem>
    private var initialized = false
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Global.context)
    private val appliedApps = sharedPreferences.getStringSet(
        Global.context.getString(R.string.key_setting_filter),
        null
    )?.toMutableSet() ?: mutableSetOf<String>()
    private var appListAdapter: AppListAdapter? = null
    var showSystemApps = sharedPreferences.getBoolean("show_system_apps", true)
        set(value) {
            field = value
            filterApps()
            sharedPreferences.edit()
                .putBoolean("show_system_apps", value)
                .apply()
        }
    var showOnlyAppliedApps = sharedPreferences.getBoolean("show_only_applied_apps", false)
        set(value) {
            field = value
            filterApps()
            sharedPreferences.edit()
                .putBoolean("show_only_applied_apps", value)
                .apply()
        }
    var queryText = ""
        set(value) {
            field = value
            filterAppsWithQueryText()
        }

    private fun loadAppList() {
        viewModelScope.launch(Dispatchers.Default) {
            isLoadingLiveData.postValue(true)
            appList.clear()
            val packageManager = Global.context.packageManager
            packageManager?.getInstalledApplications(0)?.forEach {
                appList.add(
                    AppItem(
                        packageManager.getApplicationLabel(it).toString(),
                        it.packageName,
                        packageManager.getApplicationIcon(it),
                        it.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                    )
                )
            }
            appList.sortBy { it.packageName }
            withContext(Dispatchers.Main) {
                filterApps()
            }
            isLoadingLiveData.postValue(false)
        }
    }

    private fun filterApps() {
        filteredAppList = appList.filter {
            (showSystemApps || !it.isSystemApp) &&
                    (!showOnlyAppliedApps || appliedApps.contains(it.packageName))
        }
        filterAppsWithQueryText()
    }

    private fun filterAppsWithQueryText() {
        appListAdapter?.appList =
            if (queryText.isNotEmpty())
                filteredAppList.filter {
                    it.name.contains(queryText, true) ||
                            it.packageName.contains(queryText, true)
                }
            else
                filteredAppList
    }

    private fun saveAppliedApps(appliedAppsList: MutableSet<String>) {
        sharedPreferences.edit()
            .putStringSet(
                Global.context.getString(R.string.key_setting_filter),
                appliedAppsList.toSet()
            )
            .apply()
    }

    fun init() {
        if (!initialized) {
            loadAppList()
            initialized = true
        }
    }

    fun getAppListAdapter(): AppListAdapter? {
        appListAdapter = AppListAdapter(filteredAppList, appliedApps, ::saveAppliedApps)
        return appListAdapter
    }
}