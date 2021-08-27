package com.example.vpnlearn.ui.appusage.permissionsheet

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.SheetComponent
import com.example.vpnlearn.ui.appsheet.AppListSheet
import com.example.vpnlearn.ui.base.BaseSheet
import kotlinx.android.synthetic.main.sheet_app_usage_permission.*

class AppUsagePermissionSheet : BaseSheet<PermissionSheetViewModel>() {

    companion object {
        const val TAG = "NetBlocker.AppSheet"
        fun newInstance(): AppUsagePermissionSheet {
            val args = Bundle()
            val fragment = AppUsagePermissionSheet()
            fragment.arguments = args
            return fragment
        }
    }

    var appUsagePermissionStatus = false

    override fun provideLayoutId() = R.layout.sheet_app_usage_permission

    override fun setUpViews(view: View) {
        app_usage_permission_status.setOnClickListener{
            if(!appUsagePermissionStatus){
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                dismiss()
            }
        }
    }

    override fun injectDependencies(sheetComponent: SheetComponent) {
        sheetComponent.inject(this)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.appUsagePermission.observe(this, {
            appUsagePermissionStatus = it
            app_usage_permission_progress.visibility = View.GONE
            if (it) {
                app_usage_permission_status.text = getString(R.string.permission_granted)
                app_usage_permission_status.setTextColor(resources.getColor(R.color.green))
            } else {
                app_usage_permission_status.text = getString(R.string.permission_not_granted)
                app_usage_permission_status.setTextColor(resources.getColor(R.color.red))
            }

        })
    }
}