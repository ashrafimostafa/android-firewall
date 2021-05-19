package com.example.vpnlearn.di.components

import android.content.Context
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.modules.ApplicationModule
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.MyApplication
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(myApplication: MyApplication)

    fun getDataBaseService(): DatabaseService

    fun getCompositeDisposable(): CompositeDisposable

    @ApplicationContext
    fun getContext(): Context

}