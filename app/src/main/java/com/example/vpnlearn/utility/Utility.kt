package com.example.vpnlearn.utility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.lang.StringBuilder

class Utility {
    fun getSelfVersionName(context: Context): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName

        } catch (ex: PackageManager.NameNotFoundException) {
            ex.toString()
        }
    }

    fun isWifiActive(context: Context): Boolean {
        var cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var ni = cm.activeNetworkInfo
        return ni != null && ni.type == ConnectivityManager.TYPE_WIFI;
    }

    fun toast(message: String, length: Int, context: Context) {
        Toast.makeText(context, message, length).show()
    }

    fun logExtras(tag: String, intent: Intent) {
        intent.extras?.let { data -> logBundle(tag, data) }
    }

    fun logBundle(tag: String, data: Bundle) {
        data?.let {
            var stringBuilder = StringBuilder()
            var keys = data.keySet()
            for (key in keys) {
                stringBuilder?.let {
                    it.append(key)
                        .append("=")
                        .append(data.get(key))
                        .append("\r\n")
                }

            }
            Log.d(tag, stringBuilder.toString())
        }
    }
}
