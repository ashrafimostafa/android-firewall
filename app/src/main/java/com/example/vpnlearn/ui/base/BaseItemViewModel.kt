package com.example.vpnlearn.ui.base

import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import io.reactivex.disposables.CompositeDisposable

abstract class BaseItemViewModel<T : Any>(
    compositeDisposable: CompositeDisposable,
    databaseService: DatabaseService
) : BaseViewModel(compositeDisposable) {

    val date = MutableLiveData<T>()

    fun onManualCleared() = onCleared()

    fun updatedData(data: T) {
        this.date.postValue(data)
    }

}