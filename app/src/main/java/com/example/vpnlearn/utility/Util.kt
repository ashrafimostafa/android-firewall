package com.example.vpnlearn.utility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

object Util {
    fun getSelfVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (ex: PackageManager.NameNotFoundException) {
            ex.toString()
        }
    }

    @JvmStatic
    fun isWifiActive(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
    }

    @JvmStatic
    fun showToast(message: String?, context: Context?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun logExtras(tag: String?, intent: Intent) {
        logBundle(tag, intent.extras)
    }

    fun logBundle(tag: String?, data: Bundle?) {
        if (data != null) {
            val keys = data.keySet()
            val stringBuilder = StringBuilder()
            for (key in keys) stringBuilder.append(key).append("=").append(data[key]).append("\r\n")
            Log.d(tag, stringBuilder.toString())
        }
    }
}