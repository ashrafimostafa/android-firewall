package com.example.vpnlearn.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "package")
data class PackageDM(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var appName: String,

    @ColumnInfo(name = "package_name")
    var packageName: String,

    @ColumnInfo(name = "icon")
    var icon: String,

    @ColumnInfo(name = "is_system_app")
    var isSystemApp: Boolean,

    @ColumnInfo(name = "is_wifi_disabled")
    var isWifiDisabled: Boolean,

    @ColumnInfo(name = "is_other_disabled")
    var isOtherDisabled: Boolean,

    @ColumnInfo(name = "is_selected")
    var isSelected: Boolean,

    @ColumnInfo(name = "usage_time")
    var usageTime: Int = 0,

    @ColumnInfo(name = "internet_usage")
    var internetUsage: Int = 0,

    )
