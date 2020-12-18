package dev.xposed.downloadredirection.ui.subscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import dev.xposed.downloadredirection.ui.MainActivity

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        toggleActionBarBackButton(true)
    }

    override fun onStop() {
        super.onStop()
        toggleActionBarBackButton(false)
    }

    private fun toggleActionBarBackButton(status: Boolean) {
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(status)
    }
}