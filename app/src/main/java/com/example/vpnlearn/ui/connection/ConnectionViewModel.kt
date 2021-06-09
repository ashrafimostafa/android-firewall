package com.example.vpnlearn.ui.connection

import com.example.vpnlearn.ui.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ConnectionViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable) :
    BaseViewModel(compositeDisposable) {

    companion object{
        private const val TAG = "NetBlocker.ConVM"
    }

    override fun onCreate() {

    }

}