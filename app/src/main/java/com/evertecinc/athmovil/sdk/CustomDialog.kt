package com.evertecinc.athmovil.sdk

import android.app.Dialog
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

class CustomDialog : DialogFragment() {

    private var etEnterData: EditText? = null
    private var listener: DialogResponseListener? = null
    private var id: Constants.RequestId? = null

    companion object {
        @JvmStatic
        fun show(
            activity: AppCompatActivity, title: String, hint: String,
            message: CharSequence, id: Constants.RequestId
        ) {
            val newFragment = newInstance(title, hint, message, id)
            newFragment.show(activity.supportFragmentManager, "customDialog")
        }

        private fun newInstance(
            title: String, hint: String,
            message: CharSequence,
            id: Constants.RequestId
        ): CustomDialog {
            val frag = CustomDialog()
            val args = Bundle()
            args.putString("title", title)
            args.putString("hint", hint)
            args.putCharSequence("message", message)
            args.putInt("id", id.ordinal)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @Suppress("DEPRECATION")
        retainInstance = true
        val activity = requireActivity()
        val arg = requireArguments()

        val title = arg.getString("title")
        val hint = arg.getString("hint")
        val message = arg.getCharSequence("message")
        id = Constants.RequestId.entries[arg.getInt("id")]

        if (activity is DialogResponseListener) {
            listener = activity
        }

        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom, null)
        dialogBuilder.setView(dialogView)

        etEnterData = dialogView.findViewById(R.id.etEnterData)
        etEnterData?.hint = hint

        when (id) {
            Constants.RequestId.PHONE_NUMBER, Constants.RequestId.TIMEOUT -> {
                etEnterData?.inputType = TYPE_CLASS_NUMBER
            }
            Constants.RequestId.PAYMENT_AMOUNT, Constants.RequestId.TAX, Constants.RequestId.SUBTOTAL -> {
                etEnterData?.setRawInputType(TYPE_CLASS_NUMBER)
                etEnterData?.addTextChangedListener(Utils.CurrencyTextWatcher())
            }
            else -> {
                etEnterData?.inputType = TYPE_CLASS_TEXT
            }
        }

        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton(getString(R.string.ok), null)
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            etEnterData?.setText("")
            dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }

        dialog.setOnShowListener { dialogInterface ->
            if (dialogInterface is AlertDialog) {
                val button: Button? = dialogInterface.getButton(AlertDialog.BUTTON_POSITIVE)
                button?.setOnClickListener {
                    listener?.onDialogResponse(etEnterData?.text.toString(), id!!)
                    dismiss()
                }
            }
        }
        return dialog
    }

    interface DialogResponseListener {
        fun onDialogResponse(data: String, id: Constants.RequestId)
    }
}
