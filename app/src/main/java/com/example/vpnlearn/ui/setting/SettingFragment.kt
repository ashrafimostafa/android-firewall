package com.example.vpnlearn.ui.setting

import android.accounts.AccountManager
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.policy.DeviceAdmin
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : BaseFragment<SettingViewModel>() {

    companion object {
        const val TAG = "NetBlocker.Setting"
        const val DEVICE_ADMIN_REQUEST_CODE = 101;
        const val REQUEST_PROVISION_MANAGED_PROFILE = 102;
    }

    var isAdminPermissionGranted = false


    override fun provideLayoutId() = R.layout.fragment_setting


    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }


    override fun setUpViews(view: View) {
        setting_admin_permission.setOnClickListener {
            it as CheckBox

            val checked = it.isChecked
            if (!it.isChecked) {
                Util.showToast(getString(R.string.disabling_permission_unavailable), context)
                it.isChecked = true
            } else {
                requestEnableDeviceAdminPermission()
            }
        }

        setting_allow_uninstall.setOnClickListener {
            it as CheckBox

            val devicePolicyManager =
                activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val deviceAdmin =
                activity.let { it?.let { it1 -> ComponentName(it1, DeviceAdmin::class.java) } }

            try {
                devicePolicyManager.setUninstallBlocked(
                    deviceAdmin,
                    "com.example.vpnlearn", !it.isChecked
                )
            } catch (ex: Exception) {
                Log.e(AppListFragment.TAG, "block: ${ex.toString()}")
                showToast(R.string.profile_not_created)
                setting_allow_uninstall.isChecked = false
            }

        }

        setting_add_profile.setOnClickListener {
            provisionManagedProfile()
        }

        setting_always_on_vpn.setOnClickListener {
            it as CheckBox

            val devicePolicyManager =
                activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val deviceAdmin =
                activity.let { it?.let { it1 -> ComponentName(it1, DeviceAdmin::class.java) } }


            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    deviceAdmin?.let { it1 ->
                        devicePolicyManager.setAlwaysOnVpnPackage(
                            it1,
                            Constant.PACKAGE_NAME,
                            false
                        )
                    }
                    Log.i(TAG, "always on added")
                }
            } catch (ex: Exception) {
                Log.e(AppListFragment.TAG, "block: ${ex.toString()}")
                showToast(R.string.profile_not_created)
                setting_always_on_vpn.isChecked = false
            }

        }

        setting_lock_phone.setOnClickListener {
            val devicePolicyManager =
                activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val deviceAdmin =
                activity.let { it?.let { it1 -> ComponentName(it1, DeviceAdmin::class.java) } }

            try {
                if (deviceAdmin?.let { it1 -> devicePolicyManager.isAdminActive(it1) } == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        devicePolicyManager.lockNow()
                        devicePolicyManager.setCameraDisabled(deviceAdmin, false)
                    } else {
                        Log.i(TAG, "setUpViews: inam android versionee akhe!")
                    }
//                devicePolicyManager.lockNow()
                } else {
                    Log.i(TAG, "setUpViews: permission not granted")
                }
            } catch (ex: Exception) {
                Log.e(AppListFragment.TAG, "block1: ${ex.toString()}")
            }

        }

        setting_mac_address.setOnClickListener {
            val devicePolicyManager =
                activity!!.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val deviceAdmin =
                activity.let { it?.let { it1 -> ComponentName(it1, DeviceAdmin::class.java) } }

            try {
                var mac = deviceAdmin?.let { it1 ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        devicePolicyManager.getWifiMacAddress(it1)
                    } else {
                        Log.i(TAG, "setUpViews: inam android versionee akhe!")
                    }
                }
                Log.i(TAG, "mac address: ${mac} ")
            } catch (ex: Exception) {
                Log.e(AppListFragment.TAG, "mac address: ${ex.toString()}")
            }

        }

//        getAccountList()

    }

    override fun setUpObservers() {
        super.setUpObservers()

        viewModel.adminPermissionObserver.observe(this, {
            setting_admin_permission.isChecked = it
            isAdminPermissionGranted = it
        })

        viewModel.enableDisableUninstallObserver.observe(this, Observer {
            setting_allow_uninstall.isChecked = it
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
                setting_admin_permission.isChecked = false
                showToast(getString(R.string.permission_not_granted))
            }
        } else if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Util.showToast("Provisioning done.", activity)
            } else {
                Util.showToast("Provisioning failed.", activity)
            }
        }
    }

    /**
     * Initiates the managed profile provisioning. If we already have a managed profile set up on
     * this device, we will get an error dialog in the following provisioning phase.
     */
    private fun provisionManagedProfile1() {

        if (!context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_MANAGED_USERS)) {
            Log.e(TAG, "This device does not support work profiles!")
            return
        }

        var intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)


        // Use a different intent extra below M to configure the admin component.
        val component = activity?.let {
            ComponentName(
                it,
                DeviceAdmin::class.java.name
            )
        }
        intent.putExtra(
            DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
            component
        )

        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE)
            activity!!.finish()
        } else {
            Toast.makeText(
                activity, "Device provisioning is not enabled. Stopping.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun provisionManagedProfile() {
        val activity = activity ?: return
        val intent = Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE)

        // Use a different intent extra below M to configure the admin component.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            intent.putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME,
                activity.applicationContext.packageName
            )
        } else {
            val component = ComponentName(
                activity,
                DeviceAdmin::class.java.name
            )
            intent.putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                component
            )
        }
        if (intent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(
                intent,
                REQUEST_PROVISION_MANAGED_PROFILE
            )
            activity.finish()
        } else {
            Toast.makeText(
                activity, "Device provisioning is not enabled. Stopping.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getAccountList() {
        val accountManager = activity!!.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        var accounts = accountManager.accounts
        Log.i(TAG, "getAccountList size is: ${accounts.size}")
        for (account in accounts) {
            Log.i(TAG, "accout: $account")
        }
    }
}