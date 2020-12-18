package dev.xposed.downloadredirection.ui.subscreen.downloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R

class DownloadersAdapter(
    downloaders: List<Downloader>,
    private var currentDownloaderName: String,
    private val launchEditDialog: (Downloader, Int) -> Unit,
    private val saveCurrentDownloader: (String) -> Unit,
) : RecyclerView.Adapter<DownloadersAdapter.ViewHolder>() {

    var downloaders = downloaders
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var lastCheckRadioButton: RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.downloader_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isCurrentDownloader = downloaders[position].name == currentDownloaderName
        if (isCurrentDownloader) {
            lastCheckRadioButton = holder.radioButton
        }
        holder.radioButton.isChecked = isCurrentDownloader
        holder.radioButton.isClickable = false
        holder.view.setOnClickListener {
            if (!holder.radioButton.isChecked) {
                lastCheckRadioButton?.isChecked = false
                holder.radioButton.isChecked = true
                lastCheckRadioButton = holder.radioButton
                currentDownloaderName = downloaders[position].name
                saveCurrentDownloader(currentDownloaderName)
            }
        }
        holder.editButton.setOnClickListener {
            launchEditDialog(downloaders[position], position)
        }
        holder.name.text = downloaders[position].name
    }

    override fun getItemCount() = downloaders.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val radioButton: RadioButton = view.findViewById(R.id.radio_button)
        val editButton: ImageView = view.findViewById(R.id.edit_button)
    }
}