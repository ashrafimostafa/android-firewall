package com.example.vpnlearn.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vpnlearn.model.ApplicationDm
import com.example.vpnlearn.R
import com.example.vpnlearn.service.VpnClient1.Companion.reload
import java.util.*

class ApplicationAdapter(listApplicationDm: List<ApplicationDm>, private val context: Context) :
    RecyclerView.Adapter<ApplicationAdapter.ViewHolder>(), Filterable {
    private var colorText = 0
    private val colorAccent: Int
    private val listAll: List<ApplicationDm>
    private val listSelected: MutableList<ApplicationDm>

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(
        view
    ) {
        var ivIcon: ImageView
        var tvName: TextView
        var tvPackage: TextView
        var cbWifi: CheckBox
        var cbOther: CheckBox

        init {
            ivIcon = itemView.findViewById<View>(R.id.ivIcon) as ImageView
            tvName = itemView.findViewById<View>(R.id.tvName) as TextView
            tvPackage = itemView.findViewById<View>(R.id.tvPackage) as TextView
            cbWifi = itemView.findViewById<View>(R.id.cbWifi) as CheckBox
            cbOther = itemView.findViewById<View>(R.id.cbOther) as CheckBox
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get rule
        val applicationDm = listSelected[position]

        // Rule change listener
        val cbListener =
            CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                val network: String
                if (buttonView === holder.cbWifi) {
                    network = "wifi"
                    applicationDm.wifiBlocked = isChecked
                } else {
                    network = "other"
                    applicationDm.otherBlocked = isChecked
                }
                Log.i(TAG, applicationDm.info.packageName + ": " + network + "=" + isChecked)
                val prefs = context.getSharedPreferences(network, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(applicationDm.info.packageName, isChecked).apply()
                reload(network, context)
            }
        var color = if (applicationDm.system) colorAccent else colorText
        if (applicationDm.disabled) color =
            Color.argb(100, Color.red(color), Color.green(color), Color.blue(color))
        holder.ivIcon.setImageDrawable(applicationDm.getIcon(context))
        holder.tvName.text = applicationDm.name
        holder.tvName.setTextColor(color)
        holder.tvPackage.text = applicationDm.info.packageName
        holder.tvPackage.setTextColor(color)
        holder.cbWifi.setOnCheckedChangeListener(null)
        holder.cbWifi.isChecked = applicationDm.wifiBlocked
        holder.cbWifi.setOnCheckedChangeListener(cbListener)
        holder.cbOther.setOnCheckedChangeListener(null)
        holder.cbOther.isChecked = applicationDm.otherBlocked
        holder.cbOther.setOnCheckedChangeListener(cbListener)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence): FilterResults {
                var query: CharSequence? = query
                val listResult: MutableList<ApplicationDm> = ArrayList()
                if (query == null) listResult.addAll(listAll) else {
                    query = query.toString().toLowerCase()
                    for (applicationDm in listAll) if (applicationDm.name.toLowerCase()
                            .contains(query)
                    ) listResult.add(applicationDm)
                }
                val result = FilterResults()
                result.values = listResult
                result.count = listResult.size
                return result
            }

            override fun publishResults(query: CharSequence, result: FilterResults) {
                listSelected.clear()
                if (result == null) listSelected.addAll(listAll) else for (applicationDm in result.values as List<ApplicationDm>) listSelected.add(
                    applicationDm
                )
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_app, parent, false))
    }

    override fun getItemCount(): Int {
        return listSelected.size
    }

    companion object {
        private const val TAG = "NetBlocker.Adapter"
    }

    init {
        colorAccent = ContextCompat.getColor(context, R.color.blue)
        val ta = context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
        colorText = try {
            ta.getColor(0, 0)
        } finally {
            ta.recycle()
        }
        listAll = listApplicationDm
        listSelected = ArrayList()
        listSelected.addAll(listApplicationDm)
    }
}