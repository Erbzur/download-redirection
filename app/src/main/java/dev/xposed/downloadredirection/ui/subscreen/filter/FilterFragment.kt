package dev.xposed.downloadredirection.ui.subscreen.filter

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.updatePadding
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.subscreen.BaseFragment

class FilterFragment : BaseFragment() {

    companion object {
        val TAG = FilterFragment::class.simpleName!!
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(FilterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)
        val progressBar = view.findViewById<ContentLoadingProgressBar>(R.id.progress_bar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.app_list).apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = viewModel.getAppListAdapter()
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) { status ->
            if (status) {
                recyclerView.visibility = View.GONE
                progressBar.show()
            } else {
                recyclerView.visibility = View.VISIBLE
                progressBar.hide()
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
        val searchSrcText = searchView.findViewById<View>(R.id.search_src_text)
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
        searchSrcText.updatePadding(left = 0)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.queryText = newText
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        searchView.queryHint = getString(R.string.hint_filter_search)

        menu.findItem(R.id.show_system_apps).isChecked = viewModel.showSystemApps
        menu.findItem(R.id.show_only_applied_apps).isChecked = viewModel.showOnlyAppliedApps
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_system_apps -> {
                item.isChecked = !item.isChecked
                viewModel.showSystemApps = item.isChecked
            }
            R.id.show_only_applied_apps -> {
                item.isChecked = !item.isChecked
                viewModel.showOnlyAppliedApps = item.isChecked
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.title_setting_filter)
        viewModel.init()
    }
}