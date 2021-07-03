package com.example.vpnlearn.ui.connection

import android.content.*
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Bundle
import android.os.RemoteException
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.service.BackendVpnService
import com.example.vpnlearn.service.State
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.ui.appsheet.AppListSheet
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.setting.SettingFragment
import com.example.vpnlearn.utility.FragmentHelper
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.android.synthetic.main.fragment_connection.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class ConnectionFragment : BaseFragment<ConnectionViewModel>() {

    companion object {
        const val TAG = "NetBlocker.ConFrag"
        private const val REQUEST_VPN = 1
        private val vpnThread = OpenVPNThread()
        private val vpnService = OpenVPNService()
        fun newInstance(): ConnectionFragment {
            val args = Bundle()
            val fragment = ConnectionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var vpnStart = false //used for openvpn protocol


    val vpnClient = VpnClient()


    private var appListBottomSheet: AppListSheet? = null

    override fun provideLayoutId() = R.layout.fragment_connection

    override fun setUpViews(view: View) {
        setHasOptionsMenu(true)

        connection_ip.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onIpChanged(p0.toString())
            }
        })

        connection_port.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onPortChanged(
                    try {
                        Integer.parseInt(p0.toString())
                    } catch (ex: Exception) {
                        Log.e(TAG, "integer parse error: ${ex.toString()}")
                    }
                )
            }
        })

        connection_proxy_host.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onProxyIpChanged(p0.toString())
            }
        })

        connection_proxy_port.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onProxyPortChanged(
                    try {
                        Integer.parseInt(p0.toString())
                    } catch (ex: Exception) {
                        Log.e(TAG, "integer parse error: ${ex.toString()}")
                    }
                )
            }
        })

        connection_secret.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onSecretChanged(p0.toString())
            }
        })

        connection_local_group.setOnCheckedChangeListener { _, i ->
            if (i == R.id.connection_vpn_local_radio)
                viewModel.onLocalChanged(true)
            else
                viewModel.onLocalChanged(false)
        }

        connection_select_allow.setOnClickListener {
            if (appListBottomSheet == null) {
                appListBottomSheet = AppListSheet.newInstance()
            }
            appListBottomSheet?.show(activity!!.supportFragmentManager, appListBottomSheet?.tag)
        }

        connection_connect.setOnClickListener {
            val prepare = VpnService.prepare(context)
            if (prepare == null) {
                //user already grant permission
                onActivityResult(REQUEST_VPN, AppCompatActivity.RESULT_OK, null)
            } else {
                try {
                    //system show vpn connection allow dialog
                    startActivityForResult(prepare, REQUEST_VPN)
                } catch (ex: Throwable) {
                    onActivityResult(
                        REQUEST_VPN,
                        AppCompatActivity.RESULT_CANCELED, null
                    )
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show()
                }
            }


        }

        connection_disconnect.setOnClickListener {
            disconnectVpn()
        }

        connection_configure_pptp.setOnClickListener {
            configurePptp()
        }

        isServiceRunning()
        VpnStatus.initLogCache(context!!.cacheDir)
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu_no_toggle, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_network_setting -> {
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                true
            }

            R.id.menu_application_setting -> {
                FragmentHelper.openFragment(context, R.id.all_list_main_frame, SettingFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()

        viewModel.ipObserver.observe(this, {
            connection_ip.setText(it)
        })

        viewModel.portObserver.observe(this, {
            connection_port.setText("$it")
        })

        viewModel.secretObserver.observe(this, {
            connection_secret.setText(it)
        })

        viewModel.proxyIpObserver.observe(this, {
            connection_proxy_host.setText(it)
        })

        viewModel.proxyPortObserver.observe(this, {
            connection_proxy_port.setText("$it")
        })



        viewModel.localObserver.observe(this, {
            if (it)
                connection_vpn_local_radio.isChecked = true
            else
                connection_vpn_backend_radio.isChecked = true
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VPN) {
            if (resultCode == AppCompatActivity.RESULT_OK) connectVpn()
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun connectVpn() {
        if (connection_vpn_local_radio.isChecked) {
            //local
            context?.let { vpnClient.start(it) }
        } else if (connection_vpn_pptp_radio.isChecked) {
            //pptp protocol

        } else if (connection_vpn_pptp_open_vpn_radio.isChecked) {
            //openvpn protocol
            connectOpenVpn()
        } else {
            //toy vpn
            activity!!.startService(
                Intent(context, BackendVpnService::class.java).setAction(
                    BackendVpnService.ACTION_CONNECT
                )
            )
        }
    }

    private fun disconnectVpn() {
        if (connection_vpn_local_radio.isChecked) {
            context?.let {
                if (vpnClient.getVpnState() == State.CONNECTED ||
                    vpnClient.getVpnState() == State.CONNECTING
                ) {
                    vpnClient.stop(it)
                }
            }
        } else if (connection_vpn_pptp_open_vpn_radio.isChecked) {
            confirmDisconnect()
        } else {
            activity!!.startService(
                Intent(
                    context, BackendVpnService::
                    class.java
                ).setAction(
                    BackendVpnService.ACTION_DISCONNECT
                )
            )
        }

    }

    private fun configurePptp() {
        try {
            val intent = Intent("android.net.vpn.SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }



    // open vpn protocol function
    /**
     * Show show disconnect confirm dialog
     */
    private fun confirmDisconnect() {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(this.getString(R.string.connection_close_confirm))
        builder.setPositiveButton(this.getString(R.string.yes),
            DialogInterface.OnClickListener { dialog, id -> stopVpn() })
        builder.setNegativeButton(this.getString(R.string.no),
            DialogInterface.OnClickListener { dialog, id ->
                // User cancelled the dialog
            })

        // Create the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    private fun stopVpn(): Boolean {
        try {
            vpnThread.stop()
            status("connect")
            vpnStart = false
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Internet connection status.
     */
    fun getInternetStatus(): Boolean {
        val cm =
            context!!.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo

        return nInfo != null && nInfo.isConnectedOrConnecting
    }

    /**
     * Get service status
     */
    private fun isServiceRunning() {
        setStatus(OpenVPNService.getStatus())
    }

    private fun connectOpenVpn() {
        try {
            // .ovpn file
            val conf: InputStream = context!!.assets.open("client.ovpn")
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                $line
                
                """.trimIndent()
            }
            br.readLine()
            OpenVpnApi.startVpn(
                context!!,
                config,
                "Pashmakestan",
                "User",
                "Pass"
            )

            // Update log
            Log.i(TAG, "Connecting...")
            vpnStart = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }


    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    fun setStatus(connectionState: String?) {
        if (connectionState != null) when (connectionState) {
            "DISCONNECTED" -> {
                status("connect")
                vpnStart = false
                vpnService.setDefaultStatus()
            }
            "CONNECTED" -> {
                vpnStart = true // it will use after restart this activity
                status("connected")
            }
            "WAIT" -> Log.i(TAG, "waiting for server connection!!")
            "AUTH" -> Log.i(TAG, "server authenticating!!")
            "RECONNECTING" -> {
                status("connecting")
                Log.i(TAG, "Reconnecting...")
            }
            "NONETWORK" -> Log.i(TAG, "No network connection")
        }
    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    private fun status(status: String) {
        if (status == "connect") {
            Log.i(TAG, "connect")
        } else if (status == "connecting") {
            Log.i(TAG, "connecting")
        } else if (status == "connected") {
            Log.i(TAG, "disconnect")
        } else if (status == "tryDifferentServer") {
            Log.i(TAG, "trying")
        } else if (status == "loading") {
            Log.i(TAG, "loading")
        } else if (status == "invalidDevice") {
            Log.i(TAG, "invalid")
        } else if (status == "authenticationCheck") {
            Log.i(TAG, "auth checking")
        }
    }

    /**
     * Receive broadcast message
     */
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                setStatus(intent.getStringExtra("state"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update status UI
     * @param duration: running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn: incoming data
     * @param byteOut: outgoing data
     */
    fun updateConnectionStatus(
        duration: String,
        lastPacketReceive: String,
        byteIn: String,
        byteOut: String
    ) {

    }


    override fun onResume() {
        LocalBroadcastManager.getInstance(context!!)
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))

        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

}