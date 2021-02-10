package dev.xposed.downloadredirection.ui.view.subscreen.downloader

import android.content.Context
import androidx.lifecycle.*
import dev.xposed.downloadredirection.ui.model.AppDatabase
import dev.xposed.downloadredirection.ui.model.SharedPrefsRepository
import dev.xposed.downloadredirection.ui.model.downloader.Downloader
import dev.xposed.downloadredirection.ui.model.downloader.DownloaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloaderViewModel(
    private val downloaderRepository: DownloaderRepository,
    private val sharedPrefsRepository: SharedPrefsRepository,
) : ViewModel() {
    private var downloaders: List<Downloader> = emptyList()
    private val _isLoadingLiveData = MutableLiveData(true)
    val isLoadingLiveData: LiveData<Boolean> = _isLoadingLiveData
    val downloadersAdapterLiveData = downloaderRepository.all.asLiveData().map { downloaders ->
        this.downloaders = downloaders
        _isLoadingLiveData.value = false
        Pair(downloaders, downloaders.find { downloader ->
            downloader.id == sharedPrefsRepository.currentDownloaderId
        })
    }

    fun resetToDefaultDownloaders(context: Context) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            val downloaders = withContext(Dispatchers.Default) {
                AppDatabase.getDefaultDownloaders(appContext)
            }
            downloaderRepository.deleteAll()
            downloaderRepository.insertAll(downloaders)
            if (downloaders.count { it.id == sharedPrefsRepository.currentDownloaderId } == 0) {
                sharedPrefsRepository.currentDownloaderId = null
            }
        }
    }

    fun setCurrentDownloader(downloader: Downloader) {
        sharedPrefsRepository.currentDownloaderId = downloader.id
    }

    fun putDownloader(downloader: Downloader): Boolean {
        if (downloaders.find { it.name == downloader.name && it.id != downloader.id } != null) {
            return false
        }
        viewModelScope.launch {
            downloaderRepository.insert(downloader)
        }
        return true
    }

    fun removeDownloader(downloader: Downloader) {
        viewModelScope.launch {
            downloaderRepository.delete(downloader)
            if (downloader.id == sharedPrefsRepository.currentDownloaderId) {
                sharedPrefsRepository.currentDownloaderId = null
            }
        }
    }

    class Factory(
        private val downloaderRepository: DownloaderRepository,
        private val sharedPrefsRepository: SharedPrefsRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DownloaderViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DownloaderViewModel(
                    downloaderRepository,
                    sharedPrefsRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}