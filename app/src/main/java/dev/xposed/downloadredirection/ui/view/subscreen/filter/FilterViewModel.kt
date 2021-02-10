package dev.xposed.downloadredirection.ui.view.subscreen.filter

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.*
import dev.xposed.downloadredirection.ui.model.SharedPrefsRepository
import dev.xposed.downloadredirection.ui.model.appliedapp.AppItem
import dev.xposed.downloadredirection.ui.model.appliedapp.AppliedAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilterViewModel(
    private val appliedAppRepository: AppliedAppRepository,
    private val sharedPrefsRepository: SharedPrefsRepository,
) : ViewModel() {
    private val appList = mutableListOf<AppItem>()
    private var filteredAppList: List<AppItem> = appList
    private val _appListLiveData = MutableLiveData<List<AppItem>>()
    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    private var isInitialized = false
    val appListLiveData: LiveData<List<AppItem>> = _appListLiveData
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData
    var showSystemApps = sharedPrefsRepository.showSystemApps
        set(value) {
            field = value
            sharedPrefsRepository.showSystemApps = value
            viewModelScope.launch { filterApps() }
        }
    var showOnlyAppliedApps = sharedPrefsRepository.showOnlyAppliedApps
        set(value) {
            field = value
            sharedPrefsRepository.showOnlyAppliedApps = value
            viewModelScope.launch { filterApps() }
        }
    var queryText = ""
        set(value) {
            field = value
            viewModelScope.launch { filterAppsWithQueryText() }
        }

    fun loadAppList(context: Context) {
        val packageManager = context.packageManager
        viewModelScope.launch(Dispatchers.Default) {
            _isLoadingLiveData.postValue(true)
            appliedAppRepository.loadAll().toHashSet().let { appliedApps ->
                appList.clear()
                packageManager.getInstalledApplications(0).mapTo(appList) {
                    AppItem(
                        packageName = it.packageName,
                        name = packageManager.getApplicationLabel(it).toString(),
                        icon = packageManager.getApplicationIcon(it),
                        isSystemApp = it.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                    ).apply {
                        if (appliedApps.contains(packageName)) {
                            isSelected = true
                            appliedApps.remove(packageName)
                        }
                    }
                }.sortBy { it.packageName }
                appliedApps.forEach { appliedAppRepository.delete(it) }
                filterApps()
            }
            _isLoadingLiveData.postValue(false)
        }
    }

    private suspend fun filterApps() = withContext(Dispatchers.Default) {
        filteredAppList = appList.filter {
            (showSystemApps || !it.isSystemApp) && (!showOnlyAppliedApps || it.isSelected)
        }
        filterAppsWithQueryText()
    }

    private suspend fun filterAppsWithQueryText() = withContext(Dispatchers.Default) {
        _appListLiveData.postValue(
            if (queryText.isNotEmpty())
                filteredAppList.filter {
                    it.name.contains(queryText, true) ||
                            it.packageName.contains(queryText, true)
                }
            else
                filteredAppList
        )
    }

    fun updateAppliedApps(appItem: AppItem) {
        viewModelScope.launch {
            if (appItem.isSelected) {
                appliedAppRepository.insert(appItem.packageName)
            } else {
                appliedAppRepository.delete(appItem.packageName)
            }
        }
    }

    fun init(context: Context) {
        if (!isInitialized) {
            loadAppList(context)
            isInitialized = true
        }
    }

    class Factory(
        private val appliedAppRepository: AppliedAppRepository,
        private val sharedPrefsRepository: SharedPrefsRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FilterViewModel(
                    appliedAppRepository,
                    sharedPrefsRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}