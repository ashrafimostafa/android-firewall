package com.example.vpnlearn.policy

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import com.example.vpnlearn.R

class PostProvisioningHelper(context: Context) {
    private val PREFS = "post-provisioning"
    private val PREF_DONE = "done"

    private var mContext: Context? = null
    private var mDevicePolicyManager: DevicePolicyManager? = null
    private var mSharedPrefs: SharedPreferences? = null

    init {
        mContext = context
        mDevicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mSharedPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }


    fun completeProvisioning() {
        if (isDone()) {
            return
        }
        val componentName: ComponentName = DeviceAdmin.getComponentName(mContext)
        // This is the name for the newly created managed profile.
        mDevicePolicyManager!!.setProfileName(
            componentName,
            mContext!!.getString(R.string.profile_name)
        )
        // We enable the profile here.
        mDevicePolicyManager!!.setProfileEnabled(componentName)
    }

    fun isDone(): Boolean {
        return mSharedPrefs!!.getBoolean(PREF_DONE, false)
    }
}