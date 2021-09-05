package com.example.vpnlearn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vpnlearn.service.VpnClient

class VpnClientRestarter : BroadcastReceiver() {
    companion object {
        const val TAG = "NetBlocker.VR"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Service restarted")
        context?.let { VpnClient().start(it) }
    }
}