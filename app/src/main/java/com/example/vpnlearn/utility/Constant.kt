package com.example.vpnlearn.utility

object Constant {
    const val PACKAGE_NAME = "com.example.vpnlearn"
    const val CHANNEL_ID = "VpnLearChanel"
    const val STATE_CHANGED = 1
    const val MESSAGE_VPN = 2

    //VPN STATE MESSAGE
    const val STATE_NOUN = 0
    const val STATE_CONNECTING = 1
    const val STATE_CONNECTED = 2
    const val STATE_DISCONNECTED = 3

    interface Prefs {
        companion object {
            const val NAME = "connection"
            const val SERVER_ADDRESS = "server_address"
            const val SERVER_PORT = "server_port"
            const val SHARED_SECRET = "shared_secret"
            const val PROXY_HOSTNAME = "proxy_host"
            const val PROXY_PORT = "proxy_port"
            const val ALLOW = "allow"
            const val PACKAGES = "packages"
            const val LOCAL_MODE = "local_mode"
            const val ALLOW_WIFI = "allow_wifi"
            const val ALLOW_OTHER = "allow_other"
        }
    }
}