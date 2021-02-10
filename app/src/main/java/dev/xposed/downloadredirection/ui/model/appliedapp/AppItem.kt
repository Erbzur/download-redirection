package dev.xposed.downloadredirection.ui.model.appliedapp

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    val isSystemApp: Boolean,
    var isSelected: Boolean = false,
)