package com.evertecinc.athmovil.sdk;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

public class CustomDialog extends DialogFragment {

    private EditText etEnterData;
    private DialogResponseListener listener;
    private Constants.RequestId id;

    public static void show(AppCompatActivity activity, String title, String hint,
                            CharSequence message, Constants.RequestId id) {

        final DialogFragment newFragment = CustomDialog.newInstance(title, hint, message, id);
        newFragment.show(activity.getSupportFragmentManager(), "customDialog");
    }

    private static CustomDialog newInstance(final String title, final String hint,
                                            final CharSequence message,
                                            final Constants.RequestId id) {

        final CustomDialog frag = new CustomDialog();
        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("hint", hint);
        args.putCharSequence("message", message);
        args.putInt("id", id.ordinal());
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        Activity activity = getActivity();
        Bundle arg = getArguments();

        if (activity == null || arg == null) {
            dismiss();
        }

        String title = arg.getString("title");
        String hint = arg.getString("hint");
        CharSequence message = arg.getCharSequence("message");
        id = Constants.RequestId.values()[arg.getInt("id")];


        listener = (DialogResponseListener) activity;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_custom, null);
        dialogBuilder.setView(dialogView);

        etEnterData = dialogView.findViewById(R.id.etEnterData);
        etEnterData.setHint(hint);

        switch (id) {
            case PUBLIC_TOKEN:
                etEnterData.setInputType(TYPE_CLASS_TEXT);
                break;
            case TIMEOUT:
                etEnterData.setInputType(TYPE_CLASS_NUMBER);
                break;
            case PAYMENT_AMOUNT:
                etEnterData.setRawInputType(TYPE_CLASS_NUMBER);
                etEnterData.addTextChangedListener((new Utils.CurrencyTextWatcher()));
                break;
            case TAX:
                etEnterData.setRawInputType(TYPE_CLASS_NUMBER);
                etEnterData.addTextChangedListener((new Utils.CurrencyTextWatcher()));
                break;
            case SUBTOTAL:
                etEnterData.setRawInputType(TYPE_CLASS_NUMBER);
                etEnterData.addTextChangedListener((new Utils.CurrencyTextWatcher()));
                break;
            default:
                etEnterData.setInputType(TYPE_CLASS_TEXT);
                break;
        }

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(getString(R.string.ok), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
            etEnterData.setText("");
            dismiss();
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((dialog12, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);

        dialog.setOnShowListener(dialog1 -> {
            Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                listener.onDialogResponse(etEnterData.getText().toString(), id);
                dismiss();
            });
        });
        return dialog;
    }

    public interface DialogResponseListener {
        void onDialogResponse(String data, Constants.RequestId id);
    }

}