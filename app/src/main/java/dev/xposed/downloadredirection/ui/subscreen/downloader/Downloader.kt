package dev.xposed.downloadredirection.ui.subscreen.downloader

import kotlinx.serialization.Serializable

@Serializable
data class Downloader(
    val name: String,
    val packageName: String,
    val target: String,
    val useIntent: Boolean = false,
)