package com.example.vpnlearn.ui.applist.app

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.example.vpnlearn.R
import com.example.vpnlearn.di.components.ViewHolderComponent
import com.example.vpnlearn.ui.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_application.view.*

class ApplicationViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<Application, ApplicationViewModel>(
        R.layout.item_application, parent
    ) {

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
            }

            itemView.app_icon.setImageDrawable(it.icon)

            itemView.app_wifi.setOnCheckedChangeListener { view, isChecked ->

                viewModel.onWifiCheckedClicked(isChecked, it.id)
            }

            itemView.app_other.setOnCheckedChangeListener { view, isChecked ->
                viewModel.onOtherCheckedClicked(isChecked, it.id)
            }

        })
    }

}