package dev.xposed.downloadredirection.ui.view.subscreen.downloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.model.downloader.Downloader

class DownloadersAdapter(
    private var downloaders: List<Downloader> = emptyList(),
    private var currentDownloader: Downloader? = null,
) : RecyclerView.Adapter<DownloadersAdapter.ViewHolder>() {
    private var listener: EventListener? = null
    private var lastCheckRadioButton: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.downloader_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val downloader = downloaders[position]
        val isCurrentDownloader = downloader == currentDownloader
        if (isCurrentDownloader) {
            lastCheckRadioButton = holder.radioButton
        }
        holder.radioButton.isChecked = isCurrentDownloader
        holder.name.text = downloader.name
        holder.editButton.setOnClickListener {
            listener?.onEdit(downloader)
        }
        holder.view.setOnClickListener {
            if (!holder.radioButton.isChecked) {
                lastCheckRadioButton?.isChecked = false
                holder.radioButton.isChecked = true
                lastCheckRadioButton = holder.radioButton
                currentDownloader = downloader
                listener?.onSelect(downloader)
            }
        }
    }

    override fun getItemCount() = downloaders.size

    fun setData(downloaders: List<Downloader>, currentDownloader: Downloader?) {
        this.downloaders = downloaders
        this.currentDownloader = currentDownloader
        notifyDataSetChanged()
    }

    fun setEventListener(listener: EventListener) {
        this.listener = listener
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val radioButton: RadioButton = view.findViewById(R.id.radio_button)
        val editButton: ImageView = view.findViewById(R.id.edit_button)
    }

    interface EventListener {
        fun onSelect(downloader: Downloader)
        fun onEdit(downloader: Downloader)
    }
}