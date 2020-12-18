package dev.xposed.downloadredirection.ui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import dev.xposed.downloadredirection.BuildConfig
import dev.xposed.downloadredirection.R
import dev.xposed.downloadredirection.ui.subscreen.BaseFragment
import dev.xposed.downloadredirection.ui.subscreen.downloader.DownloaderFragment
import dev.xposed.downloadredirection.ui.subscreen.filter.FilterFragment

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    companion object {
        val TAG = SettingsFragment::class.simpleName!!
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Global.context)
    private var prefDownloader: Preference? = null
    private var prefFilter: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        prefDownloader = findPreference(getString(R.string.key_setting_downloader))
        prefFilter = findPreference(getString(R.string.key_setting_filter))

        prefDownloader?.onPreferenceClickListener = this
        prefFilter?.onPreferenceClickListener = this
        findPreference<SwitchPreferenceCompat>(getString(R.string.key_setting_hide_icon))
            ?.onPreferenceChangeListener = this
        findPreference<Preference>(getString(R.string.key_about_version))
            ?.summary = BuildConfig.VERSION_NAME
        findPreference<Preference>(getString(R.string.key_about_github))?.let {
            it.intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.summary.toString()))
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.app_name)
        prefDownloader?.summary = getString(
            R.string.summary_setting_downloader,
            sharedPreferences.getString(
                getString(R.string.key_current_downloader),
                getString(R.string.summary_setting_downloader_none)
            )
        )
        prefFilter?.summary = getString(
            R.string.summary_setting_filter,
            sharedPreferences.getStringSet(
                getString(R.string.key_setting_filter),
                null
            )?.size ?: 0
        )
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val fragment: BaseFragment
        val tag: String
        when (preference?.key) {
            getString(R.string.key_setting_downloader) -> {
                fragment = DownloaderFragment()
                tag = DownloaderFragment.TAG
            }
            getString(R.string.key_setting_filter) -> {
                fragment = FilterFragment()
                tag = FilterFragment.TAG
            }
            else -> return false
        }
        return activity?.let {
            it.supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
            true
        } ?: false
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return activity?.let {
            val status =
                if (newValue as Boolean)
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                else
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            it.packageManager.setComponentEnabledSetting(
                ComponentName(it, it.packageName + ".Main"),
                status,
                PackageManager.DONT_KILL_APP
            )
            true
        } ?: false
    }
}