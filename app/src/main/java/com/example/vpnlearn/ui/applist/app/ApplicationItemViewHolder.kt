package com.example.vpnlearn.ui.applist.app

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
import javax.inject.Inject

class ApplicationViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<Application, ApplicationViewModel>(
        R.layout.item_application, parent
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
            itemView.app_package.text = it.packageName
            itemView.app_wifi.isChecked = it.isWifiDisabled
            itemView.app_other.isChecked = it.isOtherDisabled
            if (it.isSystemApp) {
                itemView.app_name.setTextColor(Color.parseColor("#FFBB86FC"))
                itemView.app_package.setTextColor(Color.parseColor("#FFBB86FC"))
            } else {
                itemView.app_name.setTextColor(Color.parseColor("#000000"))
                itemView.app_package.setTextColor(Color.parseColor("#000000"))
            }

            itemView.app_icon.setImageDrawable(it.icon)



            itemView.app_wifi.setOnClickListener { _ ->
                it.isWifiDisabled = !it.isWifiDisabled
                viewModel.onWifiCheckedClicked(it.isWifiDisabled, it.id)
                if (VpnClient.state == State.CONNECTED)
                    vpnClient.reload(context)
                Log.i(TAG, "select other: $it")
            }

            itemView.app_other.setOnClickListener { _ ->
                it.isOtherDisabled = !it.isOtherDisabled
                viewModel.onOtherCheckedClicked(it.isOtherDisabled, it.id)
                if (VpnClient.state == State.CONNECTED)
                    vpnClient.reload(context)
                Log.i(TAG, "select other: $it")
            }

        })
    }

    companion object {
        private val TAG = "NetBlocker.AppViewHolder"
    }

}