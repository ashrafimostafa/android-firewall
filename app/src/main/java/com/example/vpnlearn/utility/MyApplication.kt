package com.example.vpnlearn.utility

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.vpnlearn.R
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.di.components.ApplicationComponent
import com.example.vpnlearn.di.components.DaggerApplicationComponent
import com.example.vpnlearn.di.modules.ApplicationModule
import javax.inject.Inject

class MyApplication : Application() {

    @Inject
    lateinit var databaseService: DatabaseService

    lateinit var applicationComponent: ApplicationComponent


    override fun onCreate() {
        super.onCreate()
        getDependencies()
        createNotificationChannel()
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

    private fun getDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }
}