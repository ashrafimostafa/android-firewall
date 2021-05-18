package com.example.vpnlearn.di.modules

import android.app.Activity
import android.content.Context
import com.example.vpnlearn.di.qualifire.ActivityContext
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity
}