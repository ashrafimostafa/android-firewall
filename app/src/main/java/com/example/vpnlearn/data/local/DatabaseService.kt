package com.example.vpnlearn.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vpnlearn.data.local.dao.PackageDao
import com.example.vpnlearn.data.local.entity.PackageDM

@Database(
    entities = [PackageDM::class],
    version = 1,
    exportSchema = false
)
abstract class DatabaseService : RoomDatabase() {

    abstract fun packageDao(): PackageDao

}
