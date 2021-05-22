package com.example.vpnlearn.utility

import android.content.Context
import android.content.pm.ApplicationInfo
import com.example.vpnlearn.data.local.entity.PackageDM
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.ui.applist.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProvideAppList @Inject constructor(
    // Should be Application Context
    @ApplicationContext private val context: Context
) {

    val packageList: MutableList<PackageDM> = arrayListOf()

    val appList: MutableList<Application> = arrayListOf()

    fun provide(): MutableList<Application> {
        if (packageList.isEmpty())
            fetchAppList()
        convertDbTpModel()
        return appList
    }

    fun fetchAppList(): MutableList<PackageDM> {
        val pm = context.packageManager


        for (info in context.packageManager.getInstalledPackages(0)) {
            packageList.add(
                PackageDM(
                    appName = info.applicationInfo.loadLabel(pm).toString(),
                    packageName = info.packageName,
                    icon = "icon",
                    isSystemApp = info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                    isOtherDisabled = true,
                    isWifiDisabled = true
                )
            )

        }

        return packageList
    }

    fun convertDbTpModel() {
        for (pkg in packageList) {
            appList.add(
                Application(
                    appName = pkg.appName,
                    packageName = pkg.packageName,
                    icon = pkg.icon,
                    isSystemApp = pkg.isSystemApp,
                    isOtherDisabled = pkg.isOtherDisabled,
                    isWifiDisabled = pkg.isWifiDisabled
                )
            )
        }
    }

    fun convertDbTpModel(packageList: List<PackageDM>):MutableList<Application>  {
        val appList: MutableList<Application> = arrayListOf()
        for (pkg in packageList) {
            appList.add(
                Application(
                    appName = pkg.appName,
                    packageName = pkg.packageName,
                    icon = pkg.icon,
                    isSystemApp = pkg.isSystemApp,
                    isOtherDisabled = pkg.isOtherDisabled,
                    isWifiDisabled = pkg.isWifiDisabled
                )
            )
        }
        return appList
    }

}