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
import androidx.lifecycle.Observer
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.policy.DeviceAdmin
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.utility.Util
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment<SettingViewModel>() {

    companion object {
        const val TAG = "NetBlocker.Setting"
        const val DEVICE_ADMIN_REQUEST_CODE = 101;
    }

    var isAdminPermissionGranted = false


    override fun provideLayoutId() = R.layout.fragment_setting


    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }


    override fun setUpViews(view: View) {
        setting_allow_uninstall.setOnClickListener {
            it as CheckBox

            if (it.isChecked) {
                Util.showToast(getString(R.string.disabling_permission_unavailable), context)
            }

            var checked = it.isChecked

            Log.i(TAG, "checked: $checked")

            if (!checked) {
                requestEnableDeviceAdminPermission()
            }
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()

        viewModel.adminPermissionObserver.observe(this, {
            setting_allow_uninstall.isChecked = !it
            isAdminPermissionGranted = it
        })

    }


    private fun requestEnableDeviceAdminPermission() {
        if (isAdminPermissionGranted)
            return

        val deviceAdmin = context?.let { ComponentName(it, DeviceAdmin::class.java) }
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            getString(R.string.device_admin_message)
        )
        startActivityForResult(intent, DEVICE_ADMIN_REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEVICE_ADMIN_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                showToast(getString(R.string.permission_granted))
            } else {
                showToast(getString(R.string.permission_not_granted))
            }
        }
    }
}