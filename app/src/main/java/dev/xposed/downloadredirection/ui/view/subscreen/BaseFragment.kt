package dev.xposed.downloadredirection.ui.view.subscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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
        (activity as AppCompatActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(status)
    }
}