package com.example.vpnlearn.ui.applist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.FragmentComponent
import com.example.vpnlearn.ui.applist.app.ApplicationAdapter
import com.example.vpnlearn.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_list.*
import javax.inject.Inject

class AppListFragment : BaseFragment<AppListViewModel>() {

    companion object {
        val TAG = "NetBlocker.AppListFragment"

        fun newInstance(): AppListFragment {
            val args = Bundle()
            val fragment = AppListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var applicationAdapter: ApplicationAdapter


    override fun provideLayoutId() = R.layout.fragment_app_list


    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)


    override fun setUpViews(view: View) {
        app_list_recycler.apply {
            adapter = applicationAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.packageLiveData.observe(this, Observer {
            applicationAdapter.appendDate(it)
        })
    }
}