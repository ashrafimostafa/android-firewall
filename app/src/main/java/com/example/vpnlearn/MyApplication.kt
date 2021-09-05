package com.example.vpnlearn

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.components.ApplicationComponent
import com.example.vpnlearn.di.components.DaggerApplicationComponent
import com.example.vpnlearn.di.modules.ApplicationModule
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.worker.ServiceCheckWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyApplication : Application() {

    @Inject
    lateinit var databaseService: DatabaseService

    lateinit var applicationComponent: ApplicationComponent


    override fun onCreate() {
        super.onCreate()
        getDependencies()
        createNotificationChannel()
        initialWorker()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constant.CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initialWorker() {
        val workInterval =
            PeriodicWorkRequestBuilder<ServiceCheckWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance().enqueue(workInterval)
    }

    private fun getDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }
}