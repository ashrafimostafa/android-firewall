package com.example.vpnlearn.ui.appsheet.app

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
import com.example.vpnlearn.ui.applist.app.Application
import com.example.vpnlearn.ui.base.BaseItemViewHolder
import kotlinx.android.synthetic.main.item_application.view.*
import kotlinx.android.synthetic.main.item_application.view.app_icon
import kotlinx.android.synthetic.main.item_application.view.app_name
import kotlinx.android.synthetic.main.item_application.view.app_package
import kotlinx.android.synthetic.main.item_application_selectable.view.*
import javax.inject.Inject

class ApplicationViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<Application, ApplicationViewModel>(
        R.layout.item_application_selectable, parent
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
            itemView.app_select.isChecked = it.isSelected
            if (it.isSystemApp) {
                itemView.app_name.setTextColor(Color.parseColor("#FFBB86FC"))
                itemView.app_package.setTextColor(Color.parseColor("#FFBB86FC"))
            } else {
                itemView.app_name.setTextColor(Color.parseColor("#000000"))
                itemView.app_package.setTextColor(Color.parseColor("#000000"))
            }

            itemView.app_icon.setImageDrawable(it.icon)



            itemView.app_select.setOnClickListener { _ ->
                it.isSelected = !it.isSelected
                viewModel.onSelectPackageClicked(it.isSelected, it.id)
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