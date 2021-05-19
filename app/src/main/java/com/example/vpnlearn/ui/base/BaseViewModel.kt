package com.example.vpnlearn.ui.base

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

class BaseViewModel
    (protected var compositeDisposable: CompositeDisposable) : ViewModel() {



    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}