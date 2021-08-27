package com.example.vpnlearn.di.components

import com.example.vpnlearn.di.modules.FragmentModule
import com.example.vpnlearn.di.modules.SheetModule
import com.example.vpnlearn.di.scope.SheetScope
import com.example.vpnlearn.ui.appsheet.AppListSheet
import com.example.vpnlearn.ui.appusage.permissionsheet.AppUsagePermissionSheet
import dagger.Component

@SheetScope
@Component(dependencies = [ApplicationComponent::class], modules = [SheetModule::class])
interface SheetComponent {

    fun inject(fragment: AppListSheet)

    fun inject(fragment: AppUsagePermissionSheet)

}
