package com.example.vpnlearn.ui.connection

import android.os.Bundle
import android.view.View
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.AppListFragment
import com.example.vpnlearn.ui.base.BaseFragment


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
        //complete here
    }

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }
}