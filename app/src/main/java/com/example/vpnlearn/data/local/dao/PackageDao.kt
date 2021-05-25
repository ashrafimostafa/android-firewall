package com.example.vpnlearn.data.local.dao

import android.content.pm.PackageInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.vpnlearn.data.local.entity.PackageDM
import io.reactivex.Single

@Dao
interface PackageDao {

    @Insert
    fun insert(packageDM: PackageDM): Single<Long>

    @Delete
    fun delete(packageDM: PackageDM): Single<Int>

    @Insert
    fun insertMany(vararg packageDM: PackageDM): Single<List<Long>>

    @Insert
    fun insertMany(packageDMS: MutableList<PackageDM>): Single<List<Long>>


    @Query("SELECT * FROM package WHERE is_system_app = 0")
    fun getAllApplication(): Single<List<PackageDM>>

    @Query("SELECT * FROM package WHERE ID = :id LIMIT 1")
    fun getApplicationById(id: Long): Single<PackageDM>

    @Query("SELECT COUNT(*) FROM package")
    fun count(): Single<Int>

    @Query("UPDATE package SET is_wifi_disabled = :isChecked WHERE id = :id")
    fun updateWifi(isChecked: Boolean, id: Long): Single<Int>

    @Query("UPDATE package SET is_other_disabled = :isChecked WHERE id = :id")
    fun updateOther(isChecked: Boolean, id: Long): Single<Int>

    @Query("UPDATE package SET is_wifi_disabled = 0")
    fun disableAllWifi(): Single<Int>

    @Query("UPDATE package SET is_other_disabled = 0")
    fun disableAllOther(): Single<Int>

    @Query("UPDATE package SET is_wifi_disabled = 1")
    fun enableAllWifi(): Single<Int>

    @Query("UPDATE package SET is_other_disabled = 1")
    fun enableAllOther(): Single<Int>

    @Query("SELECT * FROM package WHERE is_wifi_disabled = 1")
    fun getAllowWifiPackages(): Single<List<PackageDM>>

    @Query("SELECT * FROM package WHERE is_other_disabled = 1")
    fun getAllowOtherPackages(): Single<List<PackageDM>>

}