package com.example.vpnlearn.ui.connection

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.service.BackendVpnService
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.ui.appsheet.AppListSheet
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.setting.SettingFragment
import com.example.vpnlearn.utility.FragmentHelper
import kotlinx.android.synthetic.main.fragment_connection.*

class ConnectionFragment : BaseFragment<ConnectionViewModel>() {

    companion object {
        const val TAG = "NetBlocker.ConFrag"
        private const val REQUEST_VPN = 1
        fun newInstance(): ConnectionFragment {
            val args = Bundle()
            val fragment = ConnectionFragment()
            fragment.arguments = args
            return fragment
        }
    }

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
            context?.let { vpnClient.start(it) }
        } else {
            activity!!.startService(
                Intent(context, BackendVpnService::class.java).setAction(
                    BackendVpnService.ACTION_CONNECT
                )
            )
        }
    }

    private fun disconnectVpn() {
        if (connection_vpn_local_radio.isChecked) {
            context?.let { vpnClient.stop(it) }
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

}