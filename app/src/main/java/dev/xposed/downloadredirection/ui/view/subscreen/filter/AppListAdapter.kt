package dev.xposed.downloadredirection.ui.view.subscreen.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.model.appliedapp.AppItem

class AppListAdapter(
    appList: List<AppItem> = emptyList()
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
    var appList = appList
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var listener: EventListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appItem = appList[position]
        holder.packageName.text = appItem.packageName
        holder.name.text = appItem.name
        holder.icon.setImageDrawable(appItem.icon)
        holder.checkBox.isChecked = appItem.isSelected
        holder.view.setOnClickListener {
            val isSelected = !holder.checkBox.isChecked
            holder.checkBox.isChecked = isSelected
            appItem.isSelected = isSelected
            listener?.onAppliedAppsChange(appItem)
        }
    }

    override fun getItemCount() = appList.size

    fun setEventListener(listener: EventListener) {
        this.listener = listener
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val packageName: TextView = view.findViewById(R.id.package_name)
        val name: TextView = view.findViewById(R.id.name)
        val icon: ImageView = view.findViewById(R.id.icon)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }

    interface EventListener {
        fun onAppliedAppsChange(appItem: AppItem)
    }
}