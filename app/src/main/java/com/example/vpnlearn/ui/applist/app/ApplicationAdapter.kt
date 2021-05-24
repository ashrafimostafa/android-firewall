package com.example.vpnlearn.ui.applist.app

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import com.example.vpnlearn.ui.base.BaseAdapter

class ApplicationAdapter(
    parentLifecycle: Lifecycle,
    appList: ArrayList<Application>
) :
    BaseAdapter<Application, ApplicationViewHolder>(
        parentLifecycle, appList
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder =
        ApplicationViewHolder(parent)


}