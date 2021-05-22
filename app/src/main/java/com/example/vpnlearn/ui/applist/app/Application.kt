package com.example.vpnlearn.ui.applist.app

import android.graphics.drawable.Drawable

data class Application(
    var id: Long,
    var appName: String,
    var packageName: String,
    var icon: Drawable,
    var isSystemApp: Boolean,
    var isOtherDisabled: Boolean,
    var isWifiDisabled: Boolean
)
