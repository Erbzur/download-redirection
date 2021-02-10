package dev.xposed.downloadredirection.ui.view.subscreen.filter

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.updatePadding
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.Global
import dev.xposed.downloadredirection.ui.model.appliedapp.AppItem
import dev.xposed.downloadredirection.ui.view.subscreen.BaseFragment

class FilterFragment : BaseFragment(), AppListAdapter.EventListener {

    companion object {
        val TAG = FilterFragment::class.simpleName!!
    }

    private val filterViewModel by viewModels<FilterViewModel> {
        FilterViewModel.Factory(Global.appliedAppRepository, Global.sharedPrefsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.app_list)
        val progressBar = view.findViewById<ContentLoadingProgressBar>(R.id.progress_bar)
        val appListAdapter = AppListAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = appListAdapter
        }
        appListAdapter.setEventListener(this)

        filterViewModel.appListLiveData.observe(viewLifecycleOwner) {
            appListAdapter.appList = it
        }
        filterViewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
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
        inflater.inflate(R.menu.filter, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
        val searchSrcText = searchView.findViewById<View>(R.id.search_src_text)
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
        searchSrcText.updatePadding(left = 0)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                filterViewModel.queryText = newText
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        searchView.queryHint = getString(R.string.hint_filter_search)

        menu.findItem(R.id.show_system_apps).isChecked = filterViewModel.showSystemApps
        menu.findItem(R.id.show_only_applied_apps).isChecked = filterViewModel.showOnlyAppliedApps
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_system_apps -> {
                item.isChecked = !item.isChecked
                filterViewModel.showSystemApps = item.isChecked
            }
            R.id.show_only_applied_apps -> {
                item.isChecked = !item.isChecked
                filterViewModel.showOnlyAppliedApps = item.isChecked
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.title_setting_filter)
        filterViewModel.init(requireContext())
    }

    override fun onAppliedAppsChange(appItem: AppItem) = filterViewModel.updateAppliedApps(appItem)
}