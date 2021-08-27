package com.example.vpnlearn.di.components

import android.content.Context
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.modules.ApplicationModule
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.logic.usagetime.AppUsageTimeAndroidApi
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.service.VpnWorker
import com.example.vpnlearn.utility.ProvideAppList
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(myApplication: MyApplication)

    fun inject(vpnClient: VpnClient)

    fun inject(connectThread: VpnWorker.ConnectThread)

    fun getDataBaseService(): DatabaseService

    fun getVpnClient(): VpnClient

    fun getCompositeDisposable(): CompositeDisposable

    fun getProvideAppList(): ProvideAppList

    fun getProvideAppUsageTime(): AppUsageTimeAndroidApi

    @ApplicationContext
    fun getContext(): Context

}