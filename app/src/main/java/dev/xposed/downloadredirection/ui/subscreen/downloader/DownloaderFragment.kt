package dev.xposed.downloadredirection.ui.subscreen.downloader

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.Global
import dev.xposed.downloadredirection.ui.subscreen.BaseFragment

class DownloaderFragment : BaseFragment(), DownloaderDialogFragment.NoticeDialogListener {

    companion object {
        val TAG = DownloaderFragment::class.simpleName!!
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(DownloaderViewModel::class.java)
    }
    private val blankDialog = DownloaderDialogFragment(this)
    private val editDialog = DownloaderDialogFragment(this, true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_downloader, container, false)
        view.findViewById<RecyclerView>(R.id.downloaders).apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = viewModel.getDownloadersAdapter(::launchEditDialog)
        }
        return view
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
                            viewModel.resetDefaultDownloaders()
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

    override fun onDialogPositiveClick(dialog: DownloaderDialogFragment): Boolean {
        val result = viewModel.putDownloader(dialog.extractInput(), dialog.index)
        if (!result) {
            Toast.makeText(
                Global.context,
                getString(R.string.msg_same_downloader_exist),
                Toast.LENGTH_SHORT
            ).show()
        }
        return result
    }

    override fun onDialogNeutralClick(dialog: DownloaderDialogFragment) {
        viewModel.removeDownloader(dialog.index)
    }

    private fun launchEditDialog(downloader: Downloader, index: Int) {
        activity?.supportFragmentManager?.let {
            editDialog.fillInput(downloader, index)
            editDialog.show(it, DownloaderDialogFragment.TAG)
        }
    }
}