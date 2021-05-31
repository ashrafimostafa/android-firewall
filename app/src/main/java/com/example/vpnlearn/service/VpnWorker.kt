package com.example.vpnlearn.service

import android.content.Context
import android.net.VpnService
import android.os.Handler


class VpnWorker(val handler: Handler, var ctx: Context) {

    companion object{
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

    var state = State.NOUN

    var newState = State.NOUN


    @Synchronized
    fun vpnState() = state


    @Synchronized
    fun start() {
        if (connectThread != null) {
            connectThread!!.cancelVpn()
            connectThread!!.startVpn()
        } else {
            connectThread = ConnectThread()
            connectThread!!.startVpn()
        }
    }

    @Synchronized
    fun stop() {

        if (connectThread != null) {
            connectThread!!.cancelVpn()
        }
        state = State.NOUN
    }

    @Synchronized
    fun connectionFailed() {


        state = State.NOUN
    }

    @Synchronized
    fun connectionLost() {

        state = State.NOUN
    }

    class ConnectThread : Thread() {

        fun startVpn() {

            val builder = Builder()
            builder.setSession("")
            builder.addAddress(ipV4, ipv4Length)
            builder.addAddress(ipV6, ipv6Length)
            builder.addRoute(routeV4, routeV4Length)
            builder.addRoute(routeV6, routeV6Length)


        }

        fun cancelVpn() {

        }
    }

}