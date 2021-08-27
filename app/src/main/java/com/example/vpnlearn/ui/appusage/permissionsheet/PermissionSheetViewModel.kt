package com.example.vpnlearn.ui.appusage.permissionsheet

import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.applist.app.Application
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.ProvideAppList
import io.reactivex.disposables.CompositeDisposable

class PermissionSheetViewModel(
    compositeDisposable: CompositeDisposable,
) : BaseViewModel(compositeDisposable) {

    val appUsagePermission = MutableLiveData<Boolean>()

    override fun onCreate() {
        checkAppUsagePermission()
    }

    fun checkAppUsagePermission(){


    }
}