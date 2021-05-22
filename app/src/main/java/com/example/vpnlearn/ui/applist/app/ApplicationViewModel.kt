package com.example.vpnlearn.ui.applist.app

import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.data.local.entity.PackageDM
import com.example.vpnlearn.ui.base.BaseItemViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ApplicationViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    private val databaseService: DatabaseService
) :
    BaseItemViewModel<Application>(compositeDisposable) {

    override fun onCreate() {
        //todo we can make api call here
    }

    fun onWifiCheckedClicked(isChecked: Boolean, id: Long) {
        compositeDisposable.add(
            databaseService
                .packageDao()
                .updateWifi(isChecked, id)
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    fun onOtherCheckedClicked(isChecked: Boolean, id: Long) {
        compositeDisposable.add(
            databaseService
                .packageDao()
                .updateOther(isChecked, id)
                .subscribeOn(Schedulers.io())
                .subscribe()
        )

    }


}