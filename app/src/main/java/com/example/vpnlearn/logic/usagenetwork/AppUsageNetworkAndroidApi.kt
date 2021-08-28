package com.example.vpnlearn.logic.usagenetwork

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import com.example.vpnlearn.di.qualifire.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context.ACTIVITY_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.core.content.ContextCompat.getSystemService
import java.time.DayOfWeek
import java.util.*


@Singleton
class AppUsageNetworkAndroidApi @Inject constructor(
    // Should be Application Context
    @ApplicationContext private val context: Context
) {

    companion object {
        const val TAG = "NetBlocker.NUsage"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("HardwareIds")
    fun getNetworkUsageStatistics() {
        var networkUsage: NetworkStatsManager? = null

//        networkUsage.queryDetailsForUid()
//        networkUsage.queryDetailsForUidTagState()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //higher than api 23
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

            val telephonyManager: TelephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


            val subscriberId: String = telephonyManager.subscriberId

            val packageManager: PackageManager = context.packageManager

            val info: ApplicationInfo = packageManager.getApplicationInfo("com.example.vpnlearn", 0)

            val uid: Int = info.uid
            var cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, -2)

            var r = networkStatsManager.queryDetailsForUidTagState(
                ConnectivityManager.TYPE_WIFI,
                subscriberId,
                cal.timeInMillis,
                System.currentTimeMillis(),
                uid,
                NetworkStats.Bucket.TAG_NONE,
                NetworkStats.Bucket.STATE_ALL
            )

            Log.i(TAG, r.toString())


        } else {
            val manager: ActivityManager? =
                context.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
            val runningApps: List<ActivityManager.RunningAppProcessInfo> =
                manager!!.getRunningAppProcesses()

            for (runningApp in runningApps) {

//                val uid: Int = (getListAdapter().getItem(position) as ActivityManager.RunningAppProcessInfo).uid

                val uid = runningApp.uid

                val received: Long = TrafficStats.getUidRxBytes(uid)
                val send: Long = TrafficStats.getUidTxBytes(uid)
                Log.v("" + uid, "Send :$send, Received :$received")
            }

        }


    }
}