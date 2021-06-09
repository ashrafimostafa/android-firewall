package com.example.vpnlearn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.vpnlearn.service.VpnClient

class StartUpReceiver : BroadcastReceiver() {

    companion object {
        val TAG = "NetBlocker.Boot"
    }

    override fun onReceive(context: Context, p1: Intent?) {

        Log.i(TAG, "Boot received ${System.currentTimeMillis()}")
        VpnClient().start(context)
    }
}