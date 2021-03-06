package com.example.vpnlearn.ui.appsheet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.data.local.entity.PackageDM
import com.example.vpnlearn.ui.applist.AppListViewModel
import com.example.vpnlearn.ui.applist.app.Application
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.ProvideAppList
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AppSheetViewModel
@Inject
constructor(
    compositeDisposable: CompositeDisposable,
    private var databaseService: DatabaseService,
    private var provideAppList: ProvideAppList
) : BaseViewModel(compositeDisposable) {

    val packageLiveData = MutableLiveData<List<Application>>()

    override fun onCreate() {
        queryPackageList()
    }

    private fun queryPackageList() {
        compositeDisposable.add(
            databaseService.packageDao()
                .count()
                .flatMap {
                    if (it == 0) {
                        databaseService.packageDao()
                            .insertMany(
                                provideAppList.fetchAppList()
                            )
                    } else {
                        databaseService.packageDao().getAllApplication()
                    }
                }.flatMap {
                    databaseService.packageDao().getAllApplication()
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    packageLiveData.postValue(provideAppList.convertDbTpModel(it as List<PackageDM>))
                    Log.d(AppListViewModel.TAG, "application exist in table $it")
                }, {
                    Log.d(AppListViewModel.TAG, it.message)
                })

        )
    }

}