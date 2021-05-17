package com.example.vpnlearn.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import java.util.*

class ApplicationDm private constructor(
    info: PackageInfo, wifiBlocked: Boolean,
    otherBlocked: Boolean, changed: Boolean, context: Context
) : Comparable<ApplicationDm> {
    @JvmField
    var info: PackageInfo

    @JvmField
    var name: String

    @JvmField
    var system: Boolean

    @JvmField
    var disabled = false

    @JvmField
    var wifiBlocked: Boolean

    @JvmField
    var otherBlocked: Boolean
    var changed: Boolean
    fun getIcon(context: Context): Drawable {
        return info.applicationInfo.loadIcon(context.packageManager)
    }

    override fun compareTo(other: ApplicationDm): Int {
        if (changed == other.changed) {
            val i = name.compareTo(other.name, ignoreCase = true)
            return if (i == 0) info.packageName.compareTo(other.info.packageName) else i
        }
        return if (changed) -1 else 1
    }

    companion object {
        @JvmStatic
        fun getRules(context: Context): List<ApplicationDm> {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val wifi = context.getSharedPreferences("wifi", Context.MODE_PRIVATE)
            val other = context.getSharedPreferences("other", Context.MODE_PRIVATE)
            val wlWifi = prefs.getBoolean("whitelist_wifi", true)
            val wlOther = prefs.getBoolean("whitelist_other", true)
            val listApplicationDms: MutableList<ApplicationDm> = ArrayList()
            for (info in context.packageManager.getInstalledPackages(0)) {
                val blWifi = wifi.getBoolean(info.packageName, wlWifi)
                val blOther = other.getBoolean(info.packageName, wlOther)
                val changed = blWifi != wlWifi || blOther != wlOther
                listApplicationDms.add(ApplicationDm(info, blWifi, blOther, changed, context))
            }
            Collections.sort(listApplicationDms)
            return listApplicationDms
        }
    }

    init {
        val pm = context.packageManager
        this.info = info
        name = info.applicationInfo.loadLabel(pm).toString()
        system = info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        val setting = pm.getApplicationEnabledSetting(info.packageName)
        if (setting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) disabled =
            !info.applicationInfo.enabled else disabled =
            setting != PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        this.wifiBlocked = wifiBlocked
        this.otherBlocked = otherBlocked
        this.changed = changed
    }
}