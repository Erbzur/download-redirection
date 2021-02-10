package dev.xposed.downloadredirection.ui.model.downloader

import kotlinx.coroutines.flow.conflate

class DownloaderRepository(private val downloaderDao: DownloaderDao) {

    val all = downloaderDao.loadAll().conflate()

    suspend fun findById(id: Int) = downloaderDao.findById(id)
    suspend fun insertAll(downloaders: List<Downloader>) = downloaderDao.insertAll(downloaders)
    suspend fun insert(downloader: Downloader) = downloaderDao.insert(downloader)
    suspend fun deleteAll() = downloaderDao.deleteAll()
    suspend fun delete(downloader: Downloader) = downloaderDao.delete(downloader)
}