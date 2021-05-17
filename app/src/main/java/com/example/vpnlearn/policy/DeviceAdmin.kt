package com.example.vpnlearn.policy

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context

object DeviceAdmin : DeviceAdminReceiver() {
    /**
     * @param context The context of the application.
     * @return The component name of this component in the given context.
     */
    @JvmStatic
    fun getComponentName(context: Context): ComponentName {
        return ComponentName(context.applicationContext, DeviceAdminReceiver::class.java)
    }
}