package com.example.vpnlearn.ui.applist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.service.State
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.setting.SettingFragment
import com.example.vpnlearn.utility.Constant
import com.example.vpnlearn.utility.FragmentHelper
import kotlinx.android.synthetic.main.fragment_app_list.*
import javax.inject.Inject


class AppListFragment : BaseFragment<AppListViewModel>() {

    companion object {
        const val TAG = "NetBlocker.AppList"
        private const val REQUEST_VPN = 1
        private val VPN_INTENT = Intent(VpnService.SERVICE_INTERFACE)

        private const val VPN_STATE_CHANGE = "VPN_STATE"
        private const val MY_RECEIVER = "VPN_STATE_RECEIVER"

        fun newInstance(): AppListFragment {
            val args = Bundle()
            val fragment = AppListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    //ui widget
    lateinit var toggleService: MenuItem
    lateinit var actionView: SwitchCompat

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var applicationAdapter: ApplicationAdapter

    @ApplicationContext
    @Inject
    lateinit var ctx: Context

    //State enum
    var state = State.NOUN

    val vpnClient = VpnClient()


    override fun provideLayoutId() = R.layout.fragment_app_list


    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)


    override fun setUpViews(view: View) {
        activity?.setTitle(R.string.app_name)
        app_list_recycler.apply {
            adapter = applicationAdapter
            layoutManager =
                LinearLayoutManager(context) //we should use linearLayoutManager here but it cause crashes
        }

        setHasOptionsMenu(true)

        checkBatteryOptimizationPermission()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        state = vpnClient.getVpnState()
        inflater.inflate(R.menu.main_menu, menu)

        toggleService = menu.findItem(R.id.menu_vpn_enable)
        actionView = MenuItemCompat.getActionView(toggleService) as SwitchCompat

        actionView.setOnClickListener {
            it as SwitchCompat

            if (it.isChecked) {
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
            } else {
                vpnClient.stop(ctx)
            }
        }
        actionView.isChecked = state == State.CONNECTED

        Log.i(TAG, "onCreateOptionsMenu: state is: $state")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_network_setting -> {
                openSettingMenu()
                true
            }
            R.id.menu_refresh_app_list -> {
                viewModel.refreshList()
                app_list_progress.visibility = View.VISIBLE
                true
            }

            R.id.menu_application_setting -> {
                FragmentHelper.openFragment(context, R.id.all_list_main_frame, SettingFragment())
                true
            }
            R.id.menu_vpn_enable -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.packageLiveData.observe(this, Observer {
            applicationAdapter.appendDate(it)
            app_list_progress.visibility = View.GONE
//            if (state == State.CONNECTED)
//                reset()
        })
    }

    private fun openSettingMenu() {
        startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        )
    }

    private fun reset() {
        Log.i(TAG, "reset: app rule changed")
        vpnClient.reload(ctx)
    }

    override fun onStart() {
        super.onStart()
        val stateChanged = IntentFilter()
        stateChanged.addAction(MY_RECEIVER)
        activity!!.registerReceiver(connectivityChangedReceiver, stateChanged)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VPN) {

            // Start service
            if (resultCode == AppCompatActivity.RESULT_OK) vpnClient.start(ctx)
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private val connectivityChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "onReceive: ${intent.getIntExtra(VPN_STATE_CHANGE, Constant.STATE_NOUN)}")
            actionView.isChecked = intent.getIntExtra(
                VPN_STATE_CHANGE,
                Constant.STATE_DISCONNECTED
            ) == Constant.STATE_CONNECTED
        }
    }

    private fun checkBatteryOptimizationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName: String = context!!.packageName
            val pm = context!!.getSystemService(POWER_SERVICE) as PowerManager?
            if (!pm!!.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

}