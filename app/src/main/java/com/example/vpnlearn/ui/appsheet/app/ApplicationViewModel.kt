package com.example.vpnlearn.ui.appsheet.app

import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.applist.app.Application
import com.example.vpnlearn.ui.base.BaseItemViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ApplicationViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    private val databaseService: DatabaseService,
) :
    BaseItemViewModel<Application>(compositeDisposable) {

    override fun onCreate() {
        //todo we can make api call here
    }

    fun onSelectPackageClicked(isChecked: Boolean, id: Long) {
        compositeDisposable.add(
            databaseService
                .packageDao()
                .updatePackageSelected(isChecked, id)
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

}