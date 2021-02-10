package dev.xposed.downloadredirection.ui.view.subscreen.downloader

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
import dev.xposed.downloadredirection.ui.model.downloader.Downloader

class DownloaderDialogFragment(private val editable: Boolean = false) : DialogFragment() {

    companion object {
        val TAG = DownloaderDialogFragment::class.simpleName!!
    }

    var downloader: Downloader? = null
        set(value) {
            if (editable) {
                field = value
            }
        }
    private var listener: ButtonClickListener? = null
    private lateinit var nameEditText: EditText
    private lateinit var packageNameEditText: EditText
    private lateinit var targetEditText: EditText
    private lateinit var useIntentSwitch: SwitchCompat

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val view = it.layoutInflater.inflate(R.layout.dialog_downloader, null)
            nameEditText = view.findViewById<EditText>(R.id.name).apply {
                setInputProperty(this, Regex("[^\\s]"))
            }
            packageNameEditText = view.findViewById<EditText>(R.id.package_name).apply {
                setInputProperty(this, Regex("[a-zA-Z0-9._]"))
            }
            targetEditText = view.findViewById<EditText>(R.id.target).apply {
                setInputProperty(this, Regex("[a-zA-Z0-9._]"))
            }
            useIntentSwitch = view.findViewById<SwitchCompat>(R.id.use_intent).apply {
                setOnCheckedChangeListener { _, _ -> setPositiveButtonAvailability() }
            }
            val builder = AlertDialog.Builder(it)
                .setView(view)
                .setPositiveButton(if (editable) R.string.save else R.string.add, null)
                .setNegativeButton(R.string.cancel, null)
            if (editable) {
                builder.setNeutralButton(R.string.delete) { _, _ ->
                    listener?.onNeutralClick(this)
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
        (requireDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).let {
            it.setOnClickListener {
                if (listener?.onPositiveClick(this) == true) {
                    dismiss()
                }
            }
            it.isEnabled = false
        }
    }

    private fun setInputProperty(editText: EditText, inputRegex: Regex) {
        editText.apply {
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

    fun extractInput() = Downloader(
        id = downloader?.id ?: 0,
        name = nameEditText.text.toString(),
        packageName = packageNameEditText.text.toString(),
        target = targetEditText.text.toString(),
        useIntent = useIntentSwitch.isChecked,
    )

    fun setButtonClickListener(listener: ButtonClickListener) {
        this.listener = listener
    }

    interface ButtonClickListener {
        fun onPositiveClick(dialog: DownloaderDialogFragment): Boolean
        fun onNeutralClick(dialog: DownloaderDialogFragment)
    }
}