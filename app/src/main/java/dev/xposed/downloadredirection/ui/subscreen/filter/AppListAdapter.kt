package dev.xposed.downloadredirection.ui.subscreen.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R

class AppListAdapter(
    appList: List<AppItem>,
    private val appliedApps: MutableSet<String>,
    private val saveAppliedApps: (MutableSet<String>) -> Unit,
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    var appList = appList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.icon.setImageDrawable(appList[position].icon)
        holder.name.text = appList[position].name
        holder.packageName.text = appList[position].packageName
        holder.checkBox.isChecked = appliedApps.contains(appList[position].packageName)
        holder.view.setOnClickListener {
            val applied = !holder.checkBox.isChecked
            holder.checkBox.isChecked = applied
            if (applied) {
                appliedApps.add(appList[position].packageName)
            } else {
                appliedApps.remove(appList[position].packageName)
            }
            saveAppliedApps(appliedApps)
        }
    }

    override fun getItemCount() = appList.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val name: TextView = view.findViewById(R.id.name)
        val packageName: TextView = view.findViewById(R.id.package_name)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }
}