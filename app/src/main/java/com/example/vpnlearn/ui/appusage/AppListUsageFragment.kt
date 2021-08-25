package com.example.vpnlearn.ui.appusage

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
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

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var applicationAdapter: com.example.vpnlearn.ui.appusage.app.ApplicationAdapter

    override fun provideLayoutId() = R.layout.fragment_app_list_usage

    override fun setUpViews(view: View) {
        app_list_usage_recycler.apply {
            adapter = applicationAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.packageLiveData.observe(this,  {
            applicationAdapter.appendDate(it)
            app_list_usage_progress.visibility = View.GONE

        })
    }
    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)


}