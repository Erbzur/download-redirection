package dev.xposed.downloadredirection.ui.view

import androidx.lifecycle.*
import dev.xposed.downloadredirection.ui.model.SharedPrefsRepository
import dev.xposed.downloadredirection.ui.model.appliedapp.AppliedAppRepository
import dev.xposed.downloadredirection.ui.model.downloader.DownloaderRepository

class SettingsViewModel(
    downloaderRepository: DownloaderRepository,
    appliedAppRepository: AppliedAppRepository,
    sharedPrefsRepository: SharedPrefsRepository,
) : ViewModel() {
    val currentDownloaderName = sharedPrefsRepository.currentDownloaderIdLiveData.switchMap { id ->
        liveData {
            emit(id?.let { downloaderRepository.findById(it)?.name } ?: "")
        }
    }
    val appliedAppsCount = appliedAppRepository.count.asLiveData()

    class Factory(
        private val downloaderRepository: DownloaderRepository,
        private val appliedAppRepository: AppliedAppRepository,
        private val sharedPrefsRepository: SharedPrefsRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(
                    downloaderRepository,
                    appliedAppRepository,
                    sharedPrefsRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}