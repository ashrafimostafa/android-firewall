package com.example.vpnlearn.utility

import android.content.Context
import com.example.vpnlearn.di.qualifire.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHelper @Inject constructor(@ApplicationContext context: Context) {

    fun isNetworkConnected() = true
}