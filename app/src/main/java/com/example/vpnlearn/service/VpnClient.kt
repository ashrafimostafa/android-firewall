package com.example.vpnlearn.service

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.core.app.TaskStackBuilder
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.R
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.receiver.RestartServiceReceiver
import com.example.vpnlearn.ui.applist.AppListActivity
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util.isWifiActive
import com.example.vpnlearn.utility.Util.logExtras
import com.example.vpnlearn.utility.Util.showToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnClient : VpnService() {

    companion object {
        private const val TAG = "NetBlocker.Service"
        private const val EXTRA_COMMAND = "Command"
        var state: State = State.NOUN
    }

    @Singleton
    private var vpn: ParcelFileDescriptor? = null

    private val mConfigureIntent: PendingIntent? = null

    @Inject
    lateinit var databaseService: DatabaseService

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Singleton
    var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Get command
        getLock(this)?.acquire(10 * 60 * 1000L /*10 minutes*/)
        val cmd = intent.getSerializableExtra(
            EXTRA_COMMAND
        ) as Command
        Log.i(
            TAG,
            "Start intent=" + intent + " command=" + cmd + " vpn=" + (vpn != null)
        )
        when (cmd) {
            Command.start -> {
                updateForegroundNotification(R.string.starting)
                state = State.CONNECTING
                if (vpn == null) {
//                    vpn = vpnStart()
                    vpnStart()
                }
                updateForegroundNotification(R.string.started)
                state = State.CONNECTED
            }
            Command.reload -> {
                updateForegroundNotification(R.string.reloading)
                state = State.CONNECTING
                val prev = vpn
//                vpn = vpnStart()
                vpnStart()
                updateForegroundNotification(R.string.started)
                state = State.CONNECTED
                prev?.let { vpnStop(it) }
            }
            Command.stop -> {
                updateForegroundNotification(R.string.stopping)
                state = State.STOPPING
                if (vpn != null) {
                    vpnStop(vpn!!)
                    updateForegroundNotification(R.string.stopped)
                    state = State.DISCONNECTED
                    vpn = null
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun vpnStart() {
        Log.i(TAG, "Starting")

        // Check if Wi-Fi
        val wifi = isWifiActive(this)
        Log.i(TAG, "wifi=$wifi")

        // Build VPN service
        val builder = Builder()
        builder.setSession(getString(R.string.app_name))
        builder.addAddress("10.1.10.1", 32)
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
        builder.addRoute("0.0.0.0", 0)
        builder.addRoute("0:0:0:0:0:0:0:0", 0)

//      builder.addDisallowedApplication("com.farsitel.bazaar")

        //adding disallow list
        if (isWifiActive(this)) {
            compositeDisposable.add(
                databaseService.packageDao()
                    .getDisAllowWifiPackages()
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                        for (pkg in it) {
                            builder.addDisallowedApplication(pkg.packageName)
                            Log.i(TAG, "wifi disallow app: $pkg")
                        }
                        establishVpn(builder)
                        Log.i(TAG, "database time1: " + System.currentTimeMillis())
                    }, {
                        Log.e(TAG, "adding disallow wifi cause error: $it")
                    })

            )
        } else {
            compositeDisposable.add(
                databaseService.packageDao()
                    .getDisAllowOtherPackages()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        for (pkg in it) {
                            builder.addDisallowedApplication(pkg.packageName)
                            Log.i(TAG, "other disallow app: $pkg")
                        }

                        val configure = Intent(this, AppListActivity::class.java)
                        val pi = PendingIntent.getActivity(
                            this,
                            0,
                            configure,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        builder.setConfigureIntent(pi)
                        establishVpn(builder)
                        Log.i(TAG, "database time2: " + System.currentTimeMillis())
                    }, {
                        Log.e(TAG, "adding disallow other cause error: $it")
                    })
            )
        }


        Log.i(TAG, "return time: " + System.currentTimeMillis())
    }

    private fun vpnStop(pfd: ParcelFileDescriptor) {
        Log.i(TAG, "Stopping1")
        try {
            pfd.close()
        } catch (ex: IOException) {
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )
        }
    }

    private val connectivityChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            if (intent.hasExtra(ConnectivityManager.EXTRA_NETWORK_TYPE) &&
                intent.getIntExtra(
                    ConnectivityManager.EXTRA_NETWORK_TYPE,
                    ConnectivityManager.TYPE_DUMMY
                ) == ConnectivityManager.TYPE_WIFI
            ) reload(context)
        }
    }
    private val packageAddedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            reload(context)
        }
    }

    override fun onCreate() {
        (applicationContext as MyApplication).applicationComponent.inject(this) //inject dependencies
        super.onCreate()
        Log.i(TAG, "Create")

        // Listen for connectivity updates
        val ifConnectivity = IntentFilter()
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityChangedReceiver, ifConnectivity)

        // Listen for added applications
        val ifPackage = IntentFilter()
        ifPackage.addAction(Intent.ACTION_PACKAGE_ADDED)
        ifPackage.addDataScheme("package")
        registerReceiver(packageAddedReceiver, ifPackage)
    }

    override fun onDestroy() {
        releaseLock(this)
        Log.i(TAG, "Destroy")
        if (vpn != null) {
            vpnStop(vpn!!)
            vpn = null
        }
        unregisterReceiver(connectivityChangedReceiver)
        unregisterReceiver(packageAddedReceiver)
        super.onDestroy()
    }

    override fun onRevoke() {
        Log.i(TAG, "Revoke")
        if (vpn != null) {
            vpnStop(vpn!!)
            vpn = null
        }

        super.onRevoke()
    }

    private fun updateForegroundNotification(message: Int) {
        Log.i(TAG, "state changed int: ${getString(message)} ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, AppListActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addNextIntentWithParentStack(intent)
            val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            startForeground(
                1, Notification.Builder(this, Constant.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_vpn_lock_24)
                    .setContentText(getString(message))
                    .setContentIntent(mConfigureIntent)
                    .setContentIntent(pendingIntent)
                    .build()
            )
        } else {
            //todo handle persist notification for old android version
        }
    }


    fun start(context: Context) {
        Log.i(TAG, "start called")
        val intent = Intent(context, VpnClient::class.java)
        intent.putExtra(EXTRA_COMMAND, Command.start)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }


    fun reload(context: Context) {
        Log.i(TAG, "reload called")
        val intent = Intent(context, VpnClient::class.java)
        intent.putExtra(EXTRA_COMMAND, Command.reload)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stop(context: Context) {
        Log.i(TAG, "stop called")
        val intent = Intent(context, VpnClient::class.java)
        intent.putExtra(EXTRA_COMMAND, Command.stop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun establishVpn(builder: Builder): ParcelFileDescriptor? {
        return try {
            builder.establish()
        } catch (ex: Throwable) {
            showToast(ex.toString(), this)
            state = State.DISCONNECTED
            null
        }
    }

    /**
     * initial the wake lock for acquire it when vpn service start
     * release it on onDestroy
     */
    @Synchronized
    private fun getLock(context: Context): WakeLock? {
        if (wakeLock == null) {
            val pm = context.getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                context.getString(R.string.app_name) + " wakelock"
            )
            wakeLock?.setReferenceCounted(true)
        }
        return wakeLock
    }

    /**
     * release the wake lock
     */
    @Synchronized
    private fun releaseLock(context: Context) {
        if (wakeLock != null) {
            while (wakeLock?.isHeld == true) wakeLock?.release()
            wakeLock = null
        }
    }

    /**
     * call when service closed
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved called")
        callResetBroadcast()
        callResetBroadcast()
    }

    /**
     * call this function when service close unwantedly
     */
    private fun callResetBroadcast() {
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }


}