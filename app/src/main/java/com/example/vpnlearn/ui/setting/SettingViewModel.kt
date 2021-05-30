package com.example.vpnlearn.ui.setting

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.policy.DeviceAdmin
import com.example.vpnlearn.ui.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SettingViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    var ctx: Context
) : BaseViewModel(compositeDisposable) {


    var adminPermissionObserver = MutableLiveData<Boolean>()

    override fun onCreate() {

        checkAdminPermission()

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
}