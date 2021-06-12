package com.example.vpnlearn.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.main.MainActivity
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton


@Singleton
class VpnWorker(
    val handler: Handler,
    var builder: VpnService.Builder,
    var ctx: Context,
    var databaseService: DatabaseService,
    var compositeDisposable: CompositeDisposable
) {

    companion object {
        const val TAG = "NetBlocker.VpnWorker"
        const val ipV4 = "10.1.10.1"
        const val ipV6 = "fd00:1:fd00:1:fd00:1:fd00:1"
        const val routeV4 = "0.0.0.0"
        const val routeV6 = "0:0:0:0:0:0:0:0"
        const val ipv4Length = 32
        const val ipv6Length = 128
        const val routeV4Length = 0
        const val routeV6Length = 0
    }


    @Singleton
    private var connectThread: ConnectThread? = null

    private var vpnState = State.NOUN


    @Synchronized
    fun start() {
        if (connectThread != null) {
            connectThread!!.cancelVpn()
            connectThread!!.startVpn()
        } else {
            connectThread =
                ConnectThread(builder, ctx, handler, databaseService, compositeDisposable)
            connectThread!!.startVpn()
        }
    }

    @Synchronized
    fun stop() {
        Log.i(TAG, "stop called")
        if (connectThread != null) {
            Log.i(TAG, "cancel called")
            connectThread!!.cancelVpn()
        }
        vpnState = State.NOUN
    }

    @Synchronized
    fun reload(){
        Log.i(TAG, "reload called")
        if (connectThread != null) {
            connectThread!!.cancelVpn()
            connectThread!!.startVpn()
        } else {
            connectThread =
                ConnectThread(builder, ctx, handler, databaseService, compositeDisposable)
            connectThread!!.startVpn()
        }
    }

    @Synchronized
    fun lost() {
        Log.i(TAG, "lost called")
        if (connectThread != null) {
            connectThread!!.cancelVpn()
            connectThread!!.startVpn()
        } else {
            connectThread =
                ConnectThread(builder, ctx, handler, databaseService, compositeDisposable)
            connectThread!!.startVpn()
        }
    }

    @Synchronized
    fun connectionFailed() {


        vpnState = State.NOUN
    }

    @Synchronized
    fun connectionLost() {

        vpnState = State.NOUN
    }

    class ConnectThread(
        var builder: VpnService.Builder,
        var ctx: Context,
        val handler: Handler,
        var databaseService: DatabaseService,
        var compositeDisposable: CompositeDisposable
    ) : Thread() {

        companion object {
            const val TAG = "NetBlocker.ConThread"
        }

        var vpn: ParcelFileDescriptor? = null


        fun startVpn() {
            builder.setSession("")
            builder.addAddress(ipV4, ipv4Length)
            builder.addAddress(ipV6, ipv6Length)
            builder.addRoute(routeV4, routeV4Length)
            builder.addRoute(routeV6, routeV6Length)
            handler.obtainMessage(Constant.STATE_CHANGED, Constant.STATE_CONNECTING).sendToTarget()

            if (Util.isWifiActive(ctx)) {
                compositeDisposable.add(
                    databaseService.packageDao()
                        .getDisAllowWifiPackages()
                        .subscribeOn(Schedulers.io())
                        .subscribe({

                            for (pkg in it) {
                                builder.addDisallowedApplication(pkg.packageName)
                                Log.i(TAG, "wifi disallow app: $pkg")
                            }
                            establishVpn()
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

                            val configure = Intent(ctx, MainActivity::class.java)
                            val pi = PendingIntent.getActivity(
                                ctx,
                                0,
                                configure,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                            builder.setConfigureIntent(pi)
                            establishVpn()
                            Log.i(TAG, "database time2: " + System.currentTimeMillis())
                        }, {
                            Log.e(TAG, "adding disallow other cause error: $it")
                        })
                )
            }
        }

        private fun establishVpn(): ParcelFileDescriptor? {
            return try {
                handler.obtainMessage(Constant.STATE_CHANGED, Constant.STATE_CONNECTED)
                    .sendToTarget()
                Log.i(TAG, "vpn connection established")
                vpn = builder.establish()
                handler.obtainMessage(Constant.MESSAGE_VPN, vpn).sendToTarget()
                return vpn
            } catch (ex: Throwable) {
                handler.obtainMessage(Constant.STATE_CHANGED, Constant.STATE_DISCONNECTED)
                    .sendToTarget()
                Log.e(TAG, "establishVpn: error in establishing vpn: ${ex.toString()}")
                null
            }
        }

        fun cancelVpn() {
            try {
                vpn!!.close()
                Log.i(TAG, "cancelVpn: vpn closed")
                handler.obtainMessage(Constant.STATE_CHANGED, Constant.STATE_DISCONNECTED)
                    .sendToTarget()
            } catch (ex: Exception) {
                Log.e(TAG, "cancelVpn: error in closing the von: ${ex.toString()}")
                handler.obtainMessage(Constant.STATE_CHANGED, Constant.STATE_DISCONNECTED)
                    .sendToTarget()
            }
        }
    }

}