package com.example.vpnlearn.ui.connection

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.Constant
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_connection.*
import javax.inject.Inject

class ConnectionViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    var ctx: Context
) :
    BaseViewModel(compositeDisposable) {

    val ipObserver = MutableLiveData<String>()
    val portObserver = MutableLiveData<Int>()
    val secretObserver = MutableLiveData<String>()
    val proxyIpObserver = MutableLiveData<String>()
    val proxyPortObserver = MutableLiveData<Int>()
    val localObserver = MutableLiveData<Boolean>()


    companion object {
        private const val TAG = "NetBlocker.ConVM"
    }

    override fun onCreate() {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)

        ipObserver.postValue(
            pref.getString(Constant.Prefs.SERVER_ADDRESS, "")
        )

        portObserver.postValue(
            pref.getInt(Constant.Prefs.SERVER_PORT, 0)
        )

        secretObserver.postValue(
            pref.getString(Constant.Prefs.SHARED_SECRET, "")
        )

        proxyIpObserver.postValue(
            pref.getString(Constant.Prefs.PROXY_HOSTNAME, "")
        )

        proxyPortObserver.postValue(
            pref.getInt(Constant.Prefs.PROXY_PORT, 0)
        )

        localObserver.postValue(
            pref.getBoolean(Constant.Prefs.LOCAL_MODE, false)
        )

    }

    fun onIpChanged(ip: String) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putString(Constant.Prefs.SERVER_ADDRESS, ip).apply()
    }

    fun onPortChanged(port: Int) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putInt(Constant.Prefs.SERVER_PORT, port).apply()
    }

    fun onSecretChanged(secret: String) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putString(Constant.Prefs.SHARED_SECRET, secret).apply()
    }

    fun onProxyIpChanged(ip: String) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putString(Constant.Prefs.PROXY_HOSTNAME, ip).apply()
    }

    fun onProxyPortChanged(port: Int) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putInt(Constant.Prefs.PROXY_PORT, port).apply()
    }

    fun onLocalChanged(local: Boolean) {
        val pref = ctx.getSharedPreferences(Constant.Prefs.NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(Constant.Prefs.LOCAL_MODE, local).apply()
    }

}