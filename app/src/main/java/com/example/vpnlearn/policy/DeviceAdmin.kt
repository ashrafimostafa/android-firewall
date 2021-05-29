package com.example.vpnlearn.policy

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vpnlearn.utility.Util
import com.example.vpnlearn.utility.Utility

class DeviceAdmin : DeviceAdminReceiver() {

    private val TAG = "DeviceAdmin"

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "onEnabled: device admin enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i(TAG, "onDisabled: device admin disabled")
    }


}