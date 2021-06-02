package com.example.vpnlearn.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.vpnlearn.MyApplication
import com.example.vpnlearn.data.local.DatabaseService
import com.example.vpnlearn.ui.applist.AppListActivity
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Util
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import javax.inject.Inject


class VpnWorker(val handler: Handler, var builder: VpnService.Builder, var ctx: Context) {

    companion object {
        const val ipV4 = "10.1.10.1"
        const val ipV6 = "fd00:1:fd00:1:fd00:1:fd00:1"
        const val routeV4 = "0.0.0.0"
        const val routeV6 = "0:0:0:0:0:0:0:0"
        const val ipv4Length = 32
        const val ipv6Length = 128
        const val routeV4Length = 0
        const val routeV6Length = 0
    }


    private var connectThread: ConnectThread? = null

    private var vpnState = State.NOUN


    @Synchronized
    fun vpnState() = vpnState


    @Synchronized
    fun start() {
        if (connectThread != null) {
            connectThread!!.cancelVpn()
            connectThread!!.startVpn()
        } else {
            connectThread = ConnectThread(builder, ctx, handler)
            connectThread!!.startVpn()
        }
    }

    @Synchronized
    fun stop() {

        if (connectThread != null) {
            connectThread!!.cancelVpn()
        }
        vpnState = State.NOUN
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
        var builder: VpnService.Builder, var ctx: Context,val handler: Handler
    ) : Thread() {

//        init {
//            (ctx as MyApplication).applicationComponent.inject(this)
//        }

        companion object {
            const val TAG = "NetBlocker.ConThread"
        }


        var vpn: ParcelFileDescriptor? = null

//        @Inject
//        lateinit var databaseService: DatabaseService
//
//        @Inject
//        lateinit var compositeDisposable: CompositeDisposable

        fun startVpn() {

            builder.setSession("")
            builder.addAddress(ipV4, ipv4Length)
            builder.addAddress(ipV6, ipv6Length)
            builder.addRoute(routeV4, routeV4Length)
            builder.addRoute(routeV6, routeV6Length)
            handler.obtainMessage(Constant.STATE_CHANGED, "starting").sendToTarget()

//            if (Util.isWifiActive(ctx)) {
//                compositeDisposable.add(
//                    databaseService.packageDao()
//                        .getDisAllowWifiPackages()
//                        .subscribeOn(Schedulers.io())
//                        .subscribe({
//
//                            for (pkg in it) {
//                                builder.addDisallowedApplication(pkg.packageName)
//                                Log.i(TAG, "wifi disallow app: $pkg")
//                            }
//                            establishVpn()
//                            Log.i(TAG, "database time1: " + System.currentTimeMillis())
//                        }, {
//                            Log.e(TAG, "adding disallow wifi cause error: $it")
//                        })
//
//                )
//            }
//            else {
//                compositeDisposable.add(
//                    databaseService.packageDao()
//                        .getDisAllowOtherPackages()
//                        .subscribeOn(Schedulers.io())
//                        .subscribe({
//                            for (pkg in it) {
//                                builder.addDisallowedApplication(pkg.packageName)
//                                Log.i(TAG, "other disallow app: $pkg")
//                            }
//
//                            val configure = Intent(ctx, AppListActivity::class.java)
//                            val pi = PendingIntent.getActivity(
//                                ctx,
//                                0,
//                                configure,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                            )
//                            builder.setConfigureIntent(pi)
//                            establishVpn()
//                            Log.i(TAG, "database time2: " + System.currentTimeMillis())
//                        }, {
//                            Log.e(TAG, "adding disallow other cause error: $it")
//                        })
//                )
//            }

            establishVpn()

        }

        private fun establishVpn(): ParcelFileDescriptor? {
            return try {
                handler.obtainMessage(Constant.STATE_CHANGED, "established").sendToTarget()
                Log.i(TAG, "vpn connection established")
                builder.establish()
            } catch (ex: Throwable) {
                Log.e(TAG, "establishVpn: error in establishing vpn: ${ex.toString()}")
                Util.showToast(ex.toString(), ctx)
                null
            }
        }

        fun cancelVpn() {
            try {
                vpn!!.close()
                Log.i(TAG, "cancelVpn: vpn closed")
                handler.obtainMessage(Constant.STATE_CHANGED, "closed").sendToTarget()
            } catch (ex: Exception) {
                Log.e(TAG, "cancelVpn: error in closing the von: ${ex.toString()}")
                handler.obtainMessage(Constant.STATE_CHANGED, "error in closing").sendToTarget()

            }


        }
    }

}