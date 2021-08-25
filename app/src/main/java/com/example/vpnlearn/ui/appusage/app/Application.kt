package com.example.vpnlearn.ui.appusage.app

import android.graphics.drawable.Drawable

data class Application(
    var id: Long,
    var appName: String,
    var packageName: String,
    var icon: Drawable,
    var isSystemApp: Boolean,
    var isSelected: Boolean,
    var appUsageTime: Int,
    var appNetworkUsage: Int,
)