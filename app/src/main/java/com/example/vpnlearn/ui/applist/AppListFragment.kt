package com.example.vpnlearn.ui.applist

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
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
import com.example.vpnlearn.utility.FragmentHelper
import kotlinx.android.synthetic.main.fragment_app_list.*
import javax.inject.Inject


class AppListFragment : BaseFragment<AppListViewModel>() {


    companion object {
        const val TAG = "NetBlocker.AppList"
        private const val REQUEST_VPN = 1
        private val VPN_INTENT = Intent(VpnService.SERVICE_INTERFACE)

        fun newInstance(): AppListFragment {
            val args = Bundle()
            val fragment = AppListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var toggleService: MenuItem

    lateinit var actionView: SwitchCompat

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var applicationAdapter: ApplicationAdapter

    @ApplicationContext
    @Inject
    lateinit var ctx: Context

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

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        toggleService = menu.findItem(R.id.menu_vpn_enable)
        actionView = MenuItemCompat.getActionView(toggleService) as SwitchCompat


        actionView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
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
                VpnClient().stop(ctx)
            }
        }
        actionView.isChecked = VpnClient.state == State.CONNECTED

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
            if (VpnClient.state == State.CONNECTED)
                reset()
        })
    }

    private fun openSettingMenu() {
        startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        )
    }

    private fun reset() {
        VpnClient().reload(ctx)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VPN) {

            // Start service
            if (resultCode == AppCompatActivity.RESULT_OK) VpnClient().start(ctx)
        } else super.onActivityResult(requestCode, resultCode, data)
    }
}