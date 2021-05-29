package com.example.vpnlearn.ui.setting

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.policy.DeviceAdmin
import com.example.vpnlearn.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment<SettingViewModel>() {

    override fun provideLayoutId() = R.layout.fragment_setting

    val DEVICE_ADMIN_REQUEST_CODE = 101;


    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setUpViews(view: View) {

        val devicePolicyManager =
            activity?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val deviceAdmin = context?.let { ComponentName(it, DeviceAdmin::class.java) }

        if (deviceAdmin?.let { devicePolicyManager.isAdminActive(it) } == true) {
            Log.i(TAG, "already device admin granted")
            setting_allow_uninstall.isChecked = false

//            devicePolicyManager.setCameraDisabled(deviceAdmin, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                devicePolicyManager.setAlwaysOnVpnPackage(deviceAdmin,null,false)
            }
        } else {
            setting_allow_uninstall.isChecked = true
        }

        setting_allow_uninstall.setOnClickListener {
            it as CheckBox

            it.isChecked = !it.isChecked

            if (it.isChecked) {
                disableDeviceAdmin()

            } else {
                requestEnableDeviceAdminPermission()
            }
        }


    }


    private fun requestEnableDeviceAdminPermission() {
        val deviceAdmin = context?.let { ComponentName(it, DeviceAdmin::class.java) }
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "We need to be device admin:)"
        )
        startActivityForResult(intent, DEVICE_ADMIN_REQUEST_CODE)

    }

    private fun disableDeviceAdmin() {
        val deviceAdmin = context?.let { ComponentName(it, DeviceAdmin::class.java) }
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "We need to be device admin:)"
        )
        startActivityForResult(intent, DEVICE_ADMIN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEVICE_ADMIN_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
//                setting_allow_uninstall.isChecked = false
                showToast("permission granted hora")
            } else {
//                setting_allow_uninstall.isChecked = false
                showToast("Permission not granted")
            }
        }
    }

    companion object {
        const val TAG = "SettingFragment"
    }
}