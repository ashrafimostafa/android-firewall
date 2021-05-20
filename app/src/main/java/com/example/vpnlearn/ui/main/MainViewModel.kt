package com.example.vpnlearn.ui.main

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.data.local.entity.PackageDM
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.ui.base.BaseViewModel

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainViewModel(
    compositeDisposable: CompositeDisposable,
    private var databaseService: DatabaseService,
    private var context: Context
) : BaseViewModel(compositeDisposable) {

    companion object {
        var TAG = "NetBlocker.MainViewModel"
    }


    override fun onCreate() {
        queryPackageList()
    }

    val packages = MutableLiveData<List<PackageDM>>()

    init {
        //todo fetch list of package and save them on database
        messageTxt.postValue("salam")
        queryPackageList()
    }

    fun getAllPackages() {
        compositeDisposable.add(
            databaseService.packageDao()
                .getAllApplication()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    packages.postValue(it)
                }, {
                    Log.d(TAG, it.message)
                })
        )
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    private fun queryPackageList() {
        compositeDisposable.add(
            databaseService.packageDao()
                .count()
                .flatMap {
                    if (it == 0) {
                        databaseService.packageDao()
                            .insertMany(
                                fetchAppList()
                            )
                    } else {
                        Single.just(0)
                    }
                }.subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d(TAG, "application exist in table $it")
                }, {
                    Log.d(TAG, it.message)
                })

        )
    }

    private fun fetchAppList(): MutableList<PackageDM> {
        val pm = context.packageManager
        val packageList: MutableList<PackageDM> = arrayListOf()

        for (info in context.packageManager.getInstalledPackages(0)) {
            packageList.add(
                PackageDM(
                    appName = info.applicationInfo.loadLabel(pm).toString(),
                    packageName = info.packageName,
                    icon = "icon",
                    isSystemApp = info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                    isOtherDisabled = true,
                    isWifiDisabled = true
                )
            )

        }
        return packageList
    }

}