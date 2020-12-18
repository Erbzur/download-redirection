package dev.xposed.downloadredirection.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.xposed.downloadredirection.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportFragmentManager.findFragmentByTag(SettingsFragment.TAG) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment(), SettingsFragment.TAG)
                .commit()
            if (!isModuleActive()) {
                Toast.makeText(
                    this, getString(R.string.msg_module_not_active),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun isModuleActive(): Boolean {
        return false
    }
}