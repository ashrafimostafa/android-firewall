package com.example.vpnlearn.ui.applist

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.data.local.entity.PackageDM
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.ui.main.MainViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@ApplicationContext
class AppListViewModel(
    compositeDisposable: CompositeDisposable,
    private var databaseService: DatabaseService,
    private var context: Context
) : BaseViewModel(compositeDisposable) {

    val packages = MutableLiveData<List<PackageDM>>()

    companion object {
        var TAG = "NetBlocker.AppListViewModel"
    }


    var data = MutableLiveData<String>()
    override fun onCreate() {
        messageTxt.postValue("salam")
        queryPackageList()
        data.postValue("Salam man applist viewmodel hastam")
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