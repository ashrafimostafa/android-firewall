package com.example.vpnlearn.ui.forground

import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.ui.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ForegroundViewModel @Inject constructor(compositeDisposable: CompositeDisposable) :
    BaseViewModel(compositeDisposable) {

    val isServiceEnableLivedata = MutableLiveData<Boolean>()

    override fun onCreate() {

    }


    fun startService(){

    }

    fun stopService(){


    }

}