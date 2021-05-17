package com.example.vpnlearn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.util.Log
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.utility.Util.logExtras

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Received $intent")
        logExtras(TAG, intent)

        // Start service
        if (VpnService.prepare(context) == null) VpnClient.start(context)
    }

    companion object {
        private const val TAG = "NetBlocker.Receiver"
    }
}