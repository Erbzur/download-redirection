package dev.xposed.downloadredirection.ui.subscreen.downloader

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import dev.xposed.downloadredirection.R

class DownloaderDialogFragment(
    private val listener: NoticeDialogListener,
    private val editable: Boolean = false,
) : DialogFragment() {

    companion object {
        val TAG = DownloaderDialogFragment::class.simpleName!!
    }

    var index = -1
        private set
    private var downloader: Downloader? = null
    private lateinit var nameEditText: EditText
    private lateinit var packageNameEditText: EditText
    private lateinit var targetEditText: EditText
    private lateinit var useIntentSwitch: SwitchCompat

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val view = it.layoutInflater.inflate(R.layout.dialog_downloader, null)
            nameEditText = setInputProperty(
                view.findViewById(R.id.name),
                Regex("[^\\s]")
            )
            packageNameEditText = setInputProperty(
                view.findViewById(R.id.package_name),
                Regex("[a-zA-Z0-9._]")
            )
            targetEditText = setInputProperty(
                view.findViewById(R.id.target),
                Regex("[a-zA-Z0-9._]")
            )
            useIntentSwitch = view.findViewById(R.id.use_intent)
            val builder = AlertDialog.Builder(it)
                .setView(view)
                .setPositiveButton(if (editable) R.string.save else R.string.add, null)
                .setNegativeButton(R.string.cancel, null)
            if (editable) {
                builder.setNeutralButton(R.string.delete) { _, _ ->
                    listener.onDialogNeutralClick(this)
                }
            }
            builder.create()
        } ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        downloader?.let {
            nameEditText.setText(it.name)
            packageNameEditText.setText(it.packageName)
            targetEditText.setText(it.target)
            useIntentSwitch.isChecked = it.useIntent
        }
        val dialog = requireDialog() as AlertDialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setOnClickListener {
                val result = listener.onDialogPositiveClick(this@DownloaderDialogFragment)
                if (result) {
                    dialog.dismiss()
                }
            }
            isEnabled = false
        }
    }

    private fun setInputProperty(editText: EditText, inputRegex: Regex): EditText {
        return editText.apply {
            filters += InputFilter { source, _, _, _, _, _ ->
                val filteredStr = source.filter { inputRegex.matches(it.toString()) }
                if (filteredStr.isEmpty()) {
                    error = getString(R.string.msg_edit_text_error, inputRegex.pattern)
                }
                filteredStr
            }
            setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) error = null }
            doAfterTextChanged { setPositiveButtonAvailability() }
        }
    }

    private fun setPositiveButtonAvailability() {
        (requireDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
            !(TextUtils.isEmpty(nameEditText.text) || TextUtils.isEmpty(packageNameEditText.text)
                    || TextUtils.isEmpty(targetEditText.text))
    }

    fun fillInput(downloader: Downloader, index: Int) {
        if (editable) {
            this.downloader = downloader
            this.index = index
        }
    }

    fun extractInput() = Downloader(
        nameEditText.text.toString(),
        packageNameEditText.text.toString(),
        targetEditText.text.toString(),
        useIntentSwitch.isChecked
    )

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DownloaderDialogFragment): Boolean
        fun onDialogNeutralClick(dialog: DownloaderDialogFragment)
    }
}