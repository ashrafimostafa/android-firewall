package com.example.vpnlearn.di.modules

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.qualifire.ActivityContext
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.ui.base.BaseActivity
import com.example.vpnlearn.utility.ViewModelProviderFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity

}