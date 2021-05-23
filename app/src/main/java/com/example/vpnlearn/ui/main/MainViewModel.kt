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

    }

    val packages = MutableLiveData<List<PackageDM>>()

    init {
        messageTxt.postValue("salam")
    }


    fun onDestroy() {
        compositeDisposable.dispose()
    }


}