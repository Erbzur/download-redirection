package dev.xposed.downloadredirection.ui.subscreen.filter

import android.graphics.drawable.Drawable

data class AppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isSystemApp: Boolean,
)