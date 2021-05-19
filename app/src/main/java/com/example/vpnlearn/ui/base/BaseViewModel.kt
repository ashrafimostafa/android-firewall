package com.example.vpnlearn.ui.base

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vpnlearn.utility.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel
    (protected var compositeDisposable: CompositeDisposable) : ViewModel() {


    val messageId = MutableLiveData<Int>()
    val messageTxt = MutableLiveData<String>()


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    abstract fun onCreate()

}