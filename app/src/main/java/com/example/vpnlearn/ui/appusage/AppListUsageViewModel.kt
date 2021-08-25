package com.example.vpnlearn.ui.appusage

import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.ProvideAppList
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AppListUsageViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    private var databaseService: DatabaseService,
    private var provideAppList: ProvideAppList
) : BaseViewModel(compositeDisposable) {

    companion object {
        var TAG = "NetBlocker.AppListUsageVM"
    }

    override fun onCreate() {

    }

}