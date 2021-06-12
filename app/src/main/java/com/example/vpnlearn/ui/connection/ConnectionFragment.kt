package com.example.vpnlearn.ui.connection

import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.Constant.Prefs
import kotlinx.android.synthetic.main.fragment_connection.*
import java.util.*
import java.util.stream.Collectors


class ConnectionFragment : BaseFragment<ConnectionViewModel>() {

    companion object {
        const val TAG = "NetBlocker.ConFrag"
        fun newInstance(): AppListFragment {
            val args = Bundle()
            val fragment = AppListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun provideLayoutId() = R.layout.fragment_connection

    override fun setUpViews(view: View) {

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
                viewModel.onPortChanged(Integer.parseInt(p0.toString()))
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
                viewModel.onProxyPortChanged(Integer.parseInt(p0.toString()))
            }
        })

        connection_secret.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onSecretChanged(p0.toString())
            }
        })

        connection_packages.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onPackageChanged(p0.toString())
            }
        })

        connection_allow_group.setOnCheckedChangeListener { _, i ->
            if (i == R.id.connection_allowed_radio)
                viewModel.onAllowChanged(true)
            else
                viewModel.onAllowChanged(false)
        }

        connection_local_group.setOnCheckedChangeListener { _, i ->
            if (i == R.id.connection_vpn_local_radio)
                viewModel.onLocalChanged(true)
            else
                viewModel.onLocalChanged(false)
        }

        connection_connect.setOnClickListener {

        }

        connection_disconnect.setOnClickListener {

        }
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
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

        viewModel.packagesObserver.observe(this, {
            connection_packages.setText(it)
        })

        viewModel.allowObserver.observe(this, {
            if (it)
                connection_allowed_radio.isChecked = true
            else
                connection_disallowed_radio.isChecked = true
        })

        viewModel.localObserver.observe(this, {
            if (it)
                connection_vpn_local_radio.isChecked = true
            else
                connection_vpn_backend_radio.isChecked = true
        })
    }

}