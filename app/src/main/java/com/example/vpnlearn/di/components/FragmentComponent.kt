package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.FragmentModule
import com.example.vpnlearn.di.scope.FragmentScope
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.appsheet.AppListSheet
import com.example.vpnlearn.ui.appusage.AppListUsageFragment
import com.example.vpnlearn.ui.connection.ConnectionFragment
import com.example.vpnlearn.ui.home.HomeFragment
import com.example.vpnlearn.ui.setting.SettingFragment
import dagger.Component

@FragmentScope
@Component(dependencies = [ApplicationComponent::class], modules = [FragmentModule::class])
interface FragmentComponent {

    fun inject(fragment: AppListFragment)

    fun inject(fragment: SettingFragment)

    fun inject(fragment: ConnectionFragment)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: AppListUsageFragment)

}