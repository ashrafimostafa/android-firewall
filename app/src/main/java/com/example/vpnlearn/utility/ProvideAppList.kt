package com.example.vpnlearn.utility

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
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

    companion object {
        const val TAG = "ProvideAppList"
    }

    private val packageList: MutableList<PackageDM> = arrayListOf()

    val appList: MutableList<Application> = arrayListOf()


    fun fetchAppList(): MutableList<PackageDM> {
        val pm = context.packageManager

        packageList.clear()
        for (info in pm.getInstalledPackages(PackageManager.GET_META_DATA)) {
            var pkg = PackageDM(
                appName = info.applicationInfo.loadLabel(pm).toString(),
                packageName = info.packageName,
                icon = "icon",
                isSystemApp = info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                isOtherDisabled = true,
                isWifiDisabled = true,
                isSelected = false
            )
            if (!packageList.contains(pkg))
                packageList.add(pkg)

        }

        return packageList
    }

    fun convertDbTpModel(packageList: List<PackageDM>): MutableList<Application> {
        val appList: MutableList<Application> = arrayListOf()
        for (pkg in packageList) {
            try {
                appList.add(
                    Application(
                        id = pkg.id,
                        appName = pkg.appName,
                        packageName = pkg.packageName,
                        icon = context.packageManager.getApplicationIcon(pkg.packageName),
                        isSystemApp = pkg.isSystemApp,
                        isOtherDisabled = pkg.isOtherDisabled,
                        isWifiDisabled = pkg.isWifiDisabled,
                        isSelected = pkg.isSelected
                    )
                )
            } catch (ex: Exception) {
                Log.e(TAG, ex.toString())
            }
        }
        return appList
    }

    fun convertDbTpModelUsage(packageList: List<PackageDM>): MutableList<com.example.vpnlearn.ui.appusage.app.Application> {
        val appList: MutableList<com.example.vpnlearn.ui.appusage.app.Application> = arrayListOf()
        for (pkg in packageList) {
            try {
                appList.add(
                    com.example.vpnlearn.ui.appusage.app.Application(
                        id = pkg.id,
                        appName = pkg.appName,
                        packageName = pkg.packageName,
                        icon = context.packageManager.getApplicationIcon(pkg.packageName),
                        isSystemApp = pkg.isSystemApp,
                        appNetworkUsage = pkg.internetUsage,
                        appUsageTime = pkg.usageTime,
                        isSelected = pkg.isSelected

                    )
                )
            } catch (ex: Exception) {
                Log.e(TAG, ex.toString())
            }
        }
        return appList
    }

}