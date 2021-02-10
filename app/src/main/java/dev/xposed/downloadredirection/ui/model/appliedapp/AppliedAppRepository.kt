package dev.xposed.downloadredirection.ui.model.appliedapp

import kotlinx.coroutines.flow.conflate

class AppliedAppRepository(private val appliedAppDao: AppliedAppDao) {

    val count = appliedAppDao.count().conflate()

    suspend fun loadAll() = appliedAppDao.loadAll()
    suspend fun insert(packageName: String) = appliedAppDao.insert(packageName)
    suspend fun delete(packageName: String) = appliedAppDao.delete(packageName)
}