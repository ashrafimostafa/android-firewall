package com.example.vpnlearn.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.Pair
import android.widget.Toast
import com.example.vpnlearn.service.ToyVpnConnection.OnEstablishListener
import com.example.vpnlearn.ui.connection.ConnectionFragment
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


class ToyVpnService : VpnService(), Handler.Callback {
    private var mHandler: Handler? = null

    private class Connection(thread: Thread?, pfd: ParcelFileDescriptor?) :
        Pair<Thread?, ParcelFileDescriptor?>(thread, pfd)

    private val mConnectingThread = AtomicReference<Thread?>()
    private val mConnection = AtomicReference<Connection?>()
    private val mNextConnectionId = AtomicInteger(1)
    private var mConfigureIntent: PendingIntent? = null
    override fun onCreate() {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = Handler(this)
        }

        // Create the intent to "configure" the connection (just start ToyVpnClient).
        mConfigureIntent = PendingIntent.getActivity(
            this, 0, Intent(this, ConnectionFragment::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return if (intent != null && ACTION_DISCONNECT == intent.action) {
            disconnect()
            START_NOT_STICKY
        } else {
            connect()
            START_STICKY
        }
    }

    override fun onDestroy() {
        disconnect()
    }

    override fun handleMessage(message: Message): Boolean {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show()
        if (message.what != R.string.disconnected) {
            updateForegroundNotification(message.what)
        }
        return true
    }

    private fun connect() {
        // Become a foreground service. Background services can be VPN services too, but they can
        // be killed by background check before getting a chance to receive onRevoke().
        updateForegroundNotification(R.string.connecting)
        mHandler!!.sendEmptyMessage(R.string.connecting)

        // Extract information from the shared preferences.
        val prefs = getSharedPreferences(ToyVpnClient.Prefs.NAME, MODE_PRIVATE)
        val server = prefs.getString(ToyVpnClient.Prefs.SERVER_ADDRESS, "")
        val secret = prefs.getString(ToyVpnClient.Prefs.SHARED_SECRET, "")!!.toByteArray()
        val allow = prefs.getBoolean(ToyVpnClient.Prefs.ALLOW, true)
        val packages = prefs.getStringSet(ToyVpnClient.Prefs.PACKAGES, emptySet())
        val port = prefs.getInt(ToyVpnClient.Prefs.SERVER_PORT, 0)
        val proxyHost = prefs.getString(ToyVpnClient.Prefs.PROXY_HOSTNAME, "")
        val proxyPort = prefs.getInt(ToyVpnClient.Prefs.PROXY_PORT, 0)
        startConnection(
            ToyVpnConnection(
                this, mNextConnectionId.getAndIncrement(), server!!, port, secret,
                proxyHost, proxyPort, allow, packages!!
            )
        )
    }

    private fun startConnection(connection: ToyVpnConnection) {
        // Replace any existing connecting thread with the  new one.
        val thread = Thread(connection, "ToyVpnThread")
        setConnectingThread(thread)

        // Handler to mark as connected once onEstablish is called.
        connection.setConfigureIntent(mConfigureIntent)
        connection.setOnEstablishListener(OnEstablishListener { tunInterface: ParcelFileDescriptor? ->
            mHandler!!.sendEmptyMessage(R.string.connected)
            mConnectingThread.compareAndSet(thread, null)
            setConnection(Connection(thread, tunInterface))
        })
        thread.start()
    }

    private fun setConnectingThread(thread: Thread?) {
        val oldThread = mConnectingThread.getAndSet(thread)
        oldThread?.interrupt()
    }

    private fun setConnection(connection: Connection?) {
        val oldConnection = mConnection.getAndSet(connection)
        if (oldConnection != null) {
            try {
                oldConnection.first!!.interrupt()
                oldConnection.second!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Closing VPN interface", e)
            }
        }
    }

    private fun disconnect() {
        mHandler!!.sendEmptyMessage(R.string.disconnected)
        setConnectingThread(null)
        setConnection(null)
        stopForeground(true)
    }

    private fun updateForegroundNotification(message: Int) {
        val NOTIFICATION_CHANNEL_ID = "ToyVpn"
        val mNotificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(
                1, Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_vpn)
                    .setContentText(getString(message))
                    .setContentIntent(mConfigureIntent)
                    .build()
            )
        }
    }

    companion object {
        private val TAG = ToyVpnService::class.java.simpleName
        const val ACTION_CONNECT = "com.example.android.toyvpn.START"
        const val ACTION_DISCONNECT = "com.example.android.toyvpn.STOP"
    }
}
