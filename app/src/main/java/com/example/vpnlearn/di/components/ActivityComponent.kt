package com.example.vpnlearn.di.components

import android.app.Activity
import android.content.Context
import com.example.vpnlearn.di.modules.ActivityModule
import com.example.vpnlearn.di.qualifire.ActivityContext
import com.example.vpnlearn.di.scope.ActivityScope
import com.example.vpnlearn.ui.applist.AppListActivity
import com.example.vpnlearn.ui.main.MainActivity
import dagger.Component
import dagger.Provides

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: AppListActivity)

    @ActivityContext
    fun getContext(): Context

}