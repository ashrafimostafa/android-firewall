package com.example.vpnlearn.policy

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log
import com.example.vpnlearn.ui.enable.EnableProfileActivity
import com.example.vpnlearn.utility.Util

class DeviceAdmin : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "DeviceAdmin"

        /**
         * @param context The context of the application.
         * @return The component name of this component in the given context.
         */
        fun getComponentName(context: Context?): ComponentName {
            return context?.let {
                ComponentName(
                    it.applicationContext,
                    DeviceAdminReceiver::class.java
                )
            }!!
        }
    }


    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        Log.i(TAG, "onProfileProvisioningComplete")

        val helper = PostProvisioningHelper(context)
        if (!helper.isDone()) {
            Log.i(TAG, "onProfileProvisioningComplete: Done")
            // EnableProfileActivity is launched with the newly set up profile.
            val launch = Intent(context, EnableProfileActivity::class.java)
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launch)
        }

        Log.i(TAG, "profile created: $intent")
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "onEnabled: device admin enabled")
        Util.showToast("device admin enabled", context)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i(TAG, "onDisabled: device admin disabled")
        Util.showToast("device admin disabled", context)
    }

    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        Log.i(TAG, "password changed")
        Util.showToast("password changed", context)
    }


}