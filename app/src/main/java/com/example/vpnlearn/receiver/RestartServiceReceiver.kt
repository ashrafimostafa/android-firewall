package com.example.vpnlearn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RestartServiceReceiver : BroadcastReceiver() {

    companion object{
        private const val TAG = "NetBlocker.Restart"
    }
    override fun onReceive(context: Context, p1: Intent?) {
        Log.i(TAG, "onReceive: service reset")
//        VpnClient().start(context)
    }

}