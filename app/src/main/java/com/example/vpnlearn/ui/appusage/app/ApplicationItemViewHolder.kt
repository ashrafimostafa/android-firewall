package com.example.vpnlearn.ui.appusage.app

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.ViewHolderComponent
import com.example.vpnlearn.di.qualifire.ApplicationContext
import com.example.vpnlearn.service.State
import com.example.vpnlearn.service.VpnClient
import com.example.vpnlearn.ui.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_application.view.*
import kotlinx.android.synthetic.main.item_application.view.app_icon
import kotlinx.android.synthetic.main.item_application.view.app_name
import kotlinx.android.synthetic.main.item_application_usage.view.*
import javax.inject.Inject

class ApplicationViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<Application, ApplicationViewModel>(
        R.layout.item_application_usage, parent
    ) {


    @ApplicationContext
    @Inject
    lateinit var context: Context

    val vpnClient = VpnClient()

    override fun setUpViews(view: View) {
        //todo complete here
    }

    override fun injectDependencies(viewholderComponent: ViewHolderComponent) {
        viewholderComponent.inject(this)
    }

    override fun setUpObservers() {
        super.setUpObservers()
        viewModel.date.observe(this, Observer {
            itemView.app_name.text = it.appName
            itemView.app_icon.setImageDrawable(it.icon)
            itemView.app_usage_time.text = it.appUsageTime.toString()
            itemView.app_network_usage.text = it.appNetworkUsage.toString()
        })
    }

    companion object {
        private val TAG = "NetBlocker.AppUsageVH"
    }

}