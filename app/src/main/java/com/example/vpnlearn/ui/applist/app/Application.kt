package com.example.vpnlearn.ui.applist.app

data class Application(
    var appName: String,
    var packageName: String,
    var icon: String,
    var isSystemApp: Boolean,
    var isOtherDisabled: Boolean,
    var isWifiDisabled: Boolean,
)
