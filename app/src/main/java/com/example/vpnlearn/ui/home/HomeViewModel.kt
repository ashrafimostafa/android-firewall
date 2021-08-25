package com.example.vpnlearn.ui.home

import android.content.Context
import com.example.vpnlearn.ui.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.disposables.ArrayCompositeDisposable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    ctx: Context
) : BaseViewModel(compositeDisposable) {

    companion object {
        private const val TAG = "NetBlocker.HomeVM"
    }

    override fun onCreate() {

    }
}