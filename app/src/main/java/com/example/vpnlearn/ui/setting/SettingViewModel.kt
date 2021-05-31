package com.example.vpnlearn.ui.setting

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.policy.DeviceAdmin
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.Constant
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception
import javax.inject.Inject

class SettingViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    var ctx: Context
) : BaseViewModel(compositeDisposable) {


    var adminPermissionObserver = MutableLiveData<Boolean>()

    var enableDisableUninstallObserver = MutableLiveData<Boolean>()

    var alwaysOnVpnObserver = MutableLiveData<Boolean>()

    override fun onCreate() {

        checkAdminPermission()

        checkAppUninstall()

        checkAlwaysOnVpn()

    }

    private fun checkAdminPermission() {

        val devicePolicyManager =
            ctx.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = ctx.let { ComponentName(it, DeviceAdmin::class.java) }

        if (deviceAdmin.let { devicePolicyManager.isAdminActive(it) }) {
            adminPermissionObserver.postValue(true)
        } else {
            adminPermissionObserver.postValue(false)
        }

    }

    private fun checkAppUninstall() {
        val devicePolicyManager =
            ctx.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = ctx.let { ComponentName(it, DeviceAdmin::class.java) }

        try {
            enableDisableUninstallObserver.postValue(
                devicePolicyManager.isUninstallBlocked(deviceAdmin, Constant.PACKAGE_NAME)
            )
        } catch (ex: Exception) {
            enableDisableUninstallObserver.postValue(false)
            Log.e(AppListFragment.TAG, "block: ${ex.toString()}")
        }
    }

    private fun checkAlwaysOnVpn() {
        val devicePolicyManager =
            ctx.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = ctx.let { ComponentName(it, DeviceAdmin::class.java) }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                alwaysOnVpnObserver.postValue(
                    devicePolicyManager
                        .isAlwaysOnVpnLockdownEnabled(deviceAdmin)
                )
            }

        } catch (ex: Exception) {
            alwaysOnVpnObserver.postValue(false)
            Log.e(AppListFragment.TAG, "always-on vpn: ${ex.toString()}")
        }
    }
}

// devicePolicyManager.setCameraDisabled(deviceAdmin, true)
// devicePolicyManager.getPermissionPolicy()
// devicePolicyManager.setGlobalPrivateDnsModeSpecifiedHost()

/*

    todo fix this error and search about it for doing task without creating the profile
    Error is : java.lang.SecurityException:Admin ComponentInfo {
    com.example.vpnlearn/com.example.vpnlearn.policy.DeviceAdmin} does not own the profile

*/

/*

    if (deviceAdmin.let { devicePolicyManager.isAdminActive(it) }) {
        //do task
    }

*/
