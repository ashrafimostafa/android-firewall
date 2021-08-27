package com.example.vpnlearn.logic.usagetime

import android.annotation.SuppressLint
import android.app.Activity
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.vpnlearn.R
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.utility.Util
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUsageTimeAndroidApi @Inject constructor(
    // Should be Application Context
    @ApplicationContext private val context: Context
) {

    companion object {
        const val TAG = "NetBlocker.Usage"
    }

    /**
     * Returns map of aggregate usage of each package
     *
     * @return A map of [String,UsageStats]
     */
    @SuppressLint("WrongConstant")
    fun getUsageStatistics(): MutableMap<String, UsageStats>? {
        // Get the app statistics since one year ago from the current time.
        var mUsageStatsManager: UsageStatsManager? = null
        mUsageStatsManager = context
            .getSystemService("usagestats") as UsageStatsManager //Context.USAGE_STATS_SERVICE


        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)


        val queryUsageStats = mUsageStatsManager.queryAndAggregateUsageStats(
            cal.timeInMillis,
            System.currentTimeMillis()
        )

        if (queryUsageStats.isEmpty()) {
            Log.i(
                TAG, context.getString(R.string.app_usage_permission_not_allow)
            )
            Util.showToast(
                context.getString(R.string.explanation_access_to_appusage_is_not_enabled),
                context
            )
        }
        return queryUsageStats
    }
}