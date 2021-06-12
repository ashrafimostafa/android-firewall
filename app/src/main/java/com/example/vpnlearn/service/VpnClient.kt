package com.example.vpnlearn.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.*
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.TaskStackBuilder
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.R
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.receiver.RestartServiceReceiver
import com.example.vpnlearn.ui.main.MainActivity
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util.logExtras
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class VpnClient() : VpnService() {

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


    val builder = Builder()

    private val VPN_STATE_CHANGE = "VPN_STATE"
    private val MY_RECEIVER = "VPN_STATE_RECEIVER"


    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, "there is a new message: ${msg.toString()}")
            when (msg.what) {
                Constant.STATE_CHANGED -> {
                    when (msg.obj as Int) {
                        Constant.STATE_NOUN -> {
                            state = State.NOUN
                            updateForegroundNotification(R.string.stopped)
                        }
                        Constant.STATE_DISCONNECTED -> {
                            state = State.DISCONNECTED
                            updateForegroundNotification(R.string.stopped)
                            var intent = Intent()
                            intent.action = MY_RECEIVER
                            intent.putExtra(VPN_STATE_CHANGE, Constant.STATE_DISCONNECTED)
                            sendBroadcast(intent)
                        }
                        Constant.STATE_CONNECTED -> {
                            state = State.CONNECTED
                            updateForegroundNotification(R.string.started)
                            var intent = Intent()
                            intent.action = MY_RECEIVER
                            intent.putExtra(VPN_STATE_CHANGE, Constant.STATE_CONNECTED)
                            sendBroadcast(intent)
                        }
                        Constant.STATE_CONNECTING -> {
                            state = State.CONNECTING
                            updateForegroundNotification(R.string.starting)
                        }

                    }
                    Log.i(TAG, "state chnage: ${msg.obj}")
                }
                Constant.MESSAGE_VPN -> {
                    Log.i(TAG, "vpn message: ${msg.obj}")
                    vpn = msg.obj as ParcelFileDescriptor
                }
            }
        }
    }


    private lateinit var vpnWorker: VpnWorker


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Get command
        getLock(this)?.acquire()

        val cmd = if (intent.getSerializableExtra(EXTRA_COMMAND) != null) {
            intent.getSerializableExtra(
                EXTRA_COMMAND
            ) as Command
        } else {
            Command.start
        }


        Log.i(TAG, "Start intent=" + intent + " command=" + cmd + " vpn=" + (vpn != null))

        when (cmd) {
            Command.start -> {
                if (vpn == null) {
                    vpnWorker.start()
                }
            }
            Command.reload -> {
                stopSelf()
                vpnWorker.start()
            }
            Command.stop -> {
                if (vpn != null) {
                    vpnWorker.stop()
                    vpn = null
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    /**
     * trigger when connectivity state changed
     */
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

    /**
     * trigger when a package install or uninstall
     */
    private val packageAddedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "Received $intent")
            logExtras(TAG, intent)
            reload(context)
        }
    }

    override fun onCreate() {
        (applicationContext as MyApplication).applicationComponent.inject(this) //inject dependencies
        vpnWorker = VpnWorker(mHandler, builder, this, databaseService, compositeDisposable)
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

    /**
     * unregister the receivers for avoid crashing
     */
    override fun onDestroy() {
        releaseLock(this)
        Log.i(TAG, "Destroy")
        unregisterReceiver(connectivityChangedReceiver)
        unregisterReceiver(packageAddedReceiver)
        super.onDestroy()
    }

    /**
     * show a persistent notification which show the state of notification
     *
     * @param message the resource id of required message with show in notification
     */
    private fun updateForegroundNotification(@StringRes message: Int) {
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
            /*
                it is possible to start vpn without showing persistent notification,
                add persistent notification if it is necessary
             */
        }
    }


    /**
     * start the VpnService
     */
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


    /**b
     * reload the VpnService, reloading done with stop and then start the VpnService
     */
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

    /**
     * stop the VpnService
     */
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

    fun getVpnState() = state


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
     * call this function when service close unwantedly
     */
    private fun callResetBroadcast() {
        val broadcastIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}