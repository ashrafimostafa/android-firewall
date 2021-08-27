package com.example.vpnlearn.ui.appusage.permissionsheet

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.applist.app.Application
import com.example.vpnlearn.ui.base.BaseViewModel
import com.example.vpnlearn.utility.ProvideAppList
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class PermissionSheetViewModel @Inject constructor(
    compositeDisposable: CompositeDisposable,
    var context: Context
) : BaseViewModel(compositeDisposable) {

    val appUsagePermission = MutableLiveData<Boolean>()

    override fun onCreate() {
        checkAppUsagePermission()
    }

    @SuppressLint("WrongConstant")
    fun checkAppUsagePermission(){

        var mUsageStatsManager: UsageStatsManager? = null
        mUsageStatsManager = context
            .getSystemService("usagestats") as UsageStatsManager //Context.USAGE_STATS_SERVICE

        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)

        val queryUsageStats = mUsageStatsManager.queryAndAggregateUsageStats(
            cal.timeInMillis,
            System.currentTimeMillis()
        )
        if(!queryUsageStats.isEmpty()){
            appUsagePermission.postValue(true)
        }else{
            appUsagePermission.postValue(false)
        }
    }

}