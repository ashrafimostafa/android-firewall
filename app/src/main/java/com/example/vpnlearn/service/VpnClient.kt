package com.example.vpnlearn.service

import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.TaskStackBuilder
import com.example.vpnlearn.ui.main.MainActivity
import com.example.vpnlearn.R
import com.example.vpnlearn.model.ApplicationDm
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util.isWifiActive
import com.example.vpnlearn.utility.Util.logExtras
import com.example.vpnlearn.utility.Util.toast
import java.io.IOException

class VpnClient : VpnService() {
    private var vpn: ParcelFileDescriptor? = null

    private val mConfigureIntent: PendingIntent? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Get enabled
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val enabled = prefs.getBoolean("enabled", false)

        // Get command
        val cmd = if (intent == null) Command.start else (intent.getSerializableExtra(
            EXTRA_COMMAND
        ) as Command)
        Log.i(
            TAG,
            "Start intent=" + intent + " command=" + cmd + " enabled=" + enabled + " vpn=" + (vpn != null)
        )
        when (cmd) {
            Command.start -> {
                updateForegroundNotification(R.string.starting)
                state = State.CONNECTING
                if (enabled && vpn == null) vpn = vpnStart()
                updateForegroundNotification(R.string.started)
                state = State.CONNECTED
            }
            Command.reload -> {
                // Seamless handover
                updateForegroundNotification(R.string.reloading)
                state = State.CONNECTING
                val prev = vpn
                if (enabled) vpn = vpnStart()
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

    private fun vpnStart(): ParcelFileDescriptor? {
        Log.i(TAG, "Starting")

        // Check if Wi-Fi
        val wifi = isWifiActive(this)
        Log.i(TAG, "wifi=$wifi")

        // Build VPN service
        val builder: Builder = Builder()
        builder.setSession(getString(R.string.app_name))
        builder.addAddress("10.1.10.1", 32)
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128)
        builder.addRoute("0.0.0.0", 0)
        builder.addRoute("0:0:0:0:0:0:0:0", 0)

        // Add list of allowed applications
        for (applicationDm in ApplicationDm.getRules(this)) if (!(if (wifi) applicationDm.wifiBlocked else applicationDm.otherBlocked)) {
            Log.i(TAG, "Allowing " + applicationDm.info.packageName)
            try {
                builder.addDisallowedApplication(applicationDm.info.packageName)
            } catch (ex: PackageManager.NameNotFoundException) {
                Log.e(
                    TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
                )
            }
        }

        // Build configure intent
        val configure = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, configure, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setConfigureIntent(pi)

        // Start VPN service
        return try {
            builder.establish()
        } catch (ex: Throwable) {
            Log.e(
                TAG, """
     $ex
     ${Log.getStackTraceString(ex)}
     """.trimIndent()
            )

            // Disable firewall
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean("enabled", false).apply()

            // Feedback
            toast(ex.toString(), Toast.LENGTH_LONG, this)
            null
        }
    }

    private fun vpnStop(pfd: ParcelFileDescriptor) {
        Log.i(TAG, "Stopping")
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
            ) reload(null, this@VpnClient)
        }
    }
    private val packageAddedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            reload(null, this@VpnClient)
        }
    }

    override fun onCreate() {
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

        // Disable firewall
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean("enabled", false).apply()
        super.onRevoke()
    }

    fun updateForegroundNotification(message: Int) {
        Log.i(TAG, "state changed int: ${getString(message)} ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, MainActivity::class.java)
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

    companion object {
        private const val TAG = "NetBlocker.Service"
        private const val EXTRA_COMMAND = "Command"
        var state: State = State.NOUN

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, VpnClient::class.java)
            intent.putExtra(EXTRA_COMMAND, Command.start)
            //        context.startService(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @JvmStatic
        fun reload(network: String?, context: Context) {
            if (network == null || (if ("wifi" == network) isWifiActive(context) else !isWifiActive(
                    context
                ))
            ) {
                val intent = Intent(context, VpnClient::class.java)
                intent.putExtra(EXTRA_COMMAND, Command.reload)
                //        context.startService(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        @JvmStatic
        fun stop(context: Context) {
            val intent = Intent(context, VpnClient::class.java)
            intent.putExtra(EXTRA_COMMAND, Command.stop)
            //        context.startService(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}