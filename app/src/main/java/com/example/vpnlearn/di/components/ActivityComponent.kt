package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.ActivityModule
import com.example.vpnlearn.di.modules.ApplicationModule
import com.example.vpnlearn.di.scope.ActivityScope
import com.example.vpnlearn.ui.main.MainActivity
import dagger.Component

@ActivityScope
@Component(modules = [ActivityModule::class], dependencies = [ApplicationComponent::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
}