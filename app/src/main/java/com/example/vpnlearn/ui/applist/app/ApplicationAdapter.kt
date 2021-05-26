package com.example.vpnlearn.ui.applist.app

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import com.example.vpnlearn.model.ApplicationDm
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

//    override fun getFilter(): Filter {
//        return object : Filter() {
//
//            override fun performFiltering(query: CharSequence?): FilterResults {
//                var query: CharSequence? = query
//                val listResult: MutableList<Application> = java.util.ArrayList()
//                if (query == null) listResult.addAll(appList) else {
//                    query = query.toString().toLowerCase()
//                    for (applicationDm in appList) if (applicationDm.name.toLowerCase()
//                            .contains(query)
//                    ) listResult.add(applicationDm)
//                }
//                val result = FilterResults()
//                result.values = listResult
//                result.count = listResult.size
//                return result
//            }
//
//            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
//                notifyDataSetChanged()
//            }
//        }
//    }


}