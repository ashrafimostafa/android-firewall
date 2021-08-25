package com.example.vpnlearn.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseFragment
import com.example.vpnlearn.ui.connection.ConnectionFragment
import com.example.vpnlearn.utility.FragmentHelper
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>() {


    companion object{
        const val TAG = "NetBlocker.Home"
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun provideLayoutId() = R.layout.fragment_home

    override fun setUpViews(view: View) {
        home_vpn_item.setOnClickListener {
            FragmentHelper.openFragment(
                context,
                R.id.all_list_main_frame,
                ConnectionFragment()
            )
        }
        home_app_usage_item.setOnClickListener {
            FragmentHelper.openFragment(
                context,
                R.id.all_list_main_frame,
                AppListFragment()
            )
        }
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

}