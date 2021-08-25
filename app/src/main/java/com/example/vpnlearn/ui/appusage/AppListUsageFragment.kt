package com.example.vpnlearn.ui.appusage

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseFragment


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

    override fun provideLayoutId() = R.layout.fragment_app_list_usage

    override fun setUpViews(view: View) {

    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)


}