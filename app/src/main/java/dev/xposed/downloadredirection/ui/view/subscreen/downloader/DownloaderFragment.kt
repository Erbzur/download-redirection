package dev.xposed.downloadredirection.ui.view.subscreen.downloader

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.Global
import dev.xposed.downloadredirection.ui.model.downloader.Downloader
import dev.xposed.downloadredirection.ui.view.subscreen.BaseFragment

class DownloaderFragment : BaseFragment(), DownloadersAdapter.EventListener,
    DownloaderDialogFragment.ButtonClickListener {

    companion object {
        val TAG = DownloaderFragment::class.simpleName!!
    }

    private val downloaderViewModel by viewModels<DownloaderViewModel> {
        DownloaderViewModel.Factory(
            Global.downloaderRepository,
            Global.sharedPrefsRepository,
        )
    }
    private val blankDialog by lazy {
        DownloaderDialogFragment().also { it.setButtonClickListener(this) }
    }
    private val editDialog by lazy {
        DownloaderDialogFragment(true).also { it.setButtonClickListener(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_downloader, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.downloaders)
        val progressBar = view.findViewById<ContentLoadingProgressBar>(R.id.progress_bar)
        val downloadersAdapter = DownloadersAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = downloadersAdapter
        }
        downloadersAdapter.setEventListener(this)

        downloaderViewModel.downloadersAdapterLiveData.observe(viewLifecycleOwner) {
            downloadersAdapter.setData(it.first, it.second)
        }
        downloaderViewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
            if (it) {
                recyclerView.visibility = View.GONE
                progressBar.show()
            } else {
                recyclerView.visibility = View.VISIBLE
                progressBar.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.downloader, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                activity?.supportFragmentManager?.let {
                    blankDialog.show(it, DownloaderDialogFragment.TAG)
                }
            }
            R.id.reset -> {
                activity?.let {
                    AlertDialog.Builder(it)
                        .setMessage(R.string.msg_reset_downloaders_confirm)
                        .setPositiveButton(R.string.reset) { _, _ ->
                            downloaderViewModel.resetToDefaultDownloaders(requireContext())
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.title_setting_downloader)
    }

    override fun onSelect(downloader: Downloader) {
        downloaderViewModel.setCurrentDownloader(downloader)
    }

    override fun onEdit(downloader: Downloader) {
        activity?.supportFragmentManager?.let {
            editDialog.downloader = downloader
            editDialog.show(it, DownloaderDialogFragment.TAG)
        }
    }

    override fun onPositiveClick(dialog: DownloaderDialogFragment): Boolean {
        val result = downloaderViewModel.putDownloader(dialog.extractInput())
        if (!result) {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_same_downloader_exist),
                Toast.LENGTH_SHORT
            ).show()
        }
        return result
    }

    override fun onNeutralClick(dialog: DownloaderDialogFragment) {
        dialog.downloader?.let {
            downloaderViewModel.removeDownloader(it)
        }
    }
}