package com.example.vpnlearn.ui.appusage

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.appusage.permissionsheet.AppUsagePermissionSheet
import com.example.vpnlearn.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_list.*
import kotlinx.android.synthetic.main.fragment_app_list_usage.*
import java.util.Observer
import javax.inject.Inject


class AppListUsageFragment : BaseFragment<AppListUsageViewModel>() {

    companion object {
        const val TAG = "NetBlocker.AppListUsage"

        fun newInstance(): AppListUsageFragment {
            val args = Bundle()
            val fragment = AppListUsageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var appUsagePermissionSheet: AppUsagePermissionSheet? = null


    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var applicationAdapter: com.example.vpnlearn.ui.appusage.app.ApplicationAdapter

    override fun provideLayoutId() = R.layout.fragment_app_list_usage

    override fun setUpViews(view: View) {
        setHasOptionsMenu(true)
        app_list_usage_recycler.apply {
            adapter = applicationAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.updateAppUsageTime()
        viewModel.packageLiveData.observe(this, {
            applicationAdapter.appendDate(it)
            app_list_usage_progress.visibility = View.GONE

        })
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_usage_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_app_usage_refresh -> {
                viewModel.updateAppUsageTime()
                true
            }
            R.id.menu_app_usage_permission -> {
                if (appUsagePermissionSheet == null)
                    appUsagePermissionSheet = AppUsagePermissionSheet.newInstance()
                appUsagePermissionSheet?.show(activity!!.supportFragmentManager, appUsagePermissionSheet?.tag)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}