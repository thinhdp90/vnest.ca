package com.vnest.ca.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.vnest.ca.R;

public class DialogUtils {
    static final String CANCEL = "Cancel";
    static final String CONFIRM = "OK";

    public static ProgressDialog showProgressDialog(Context context) {
        ProgressDialog waitingDialog = new ProgressDialog(context);
        waitingDialog.setCancelable(false);
        waitingDialog.setMessage(context.getString(R.string.checking_for_update));
        waitingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return waitingDialog;
    }

    public static AlertDialog getConfirmDialog(Context context, String title, String message, boolean isShowCancelButton, OnConfirmListener onConfirmListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);
        if (isShowCancelButton) {
            alertDialogBuilder.setNegativeButton(CANCEL, (dialog, which) -> {
                dialog.dismiss();
            }).setPositiveButton(CONFIRM, (dialog, which) -> {
                onConfirmListener.onConfirm(dialog);
            });
        } else {
            alertDialogBuilder.setPositiveButton(CONFIRM, (dialog, which) -> {
                dialog.dismiss();
            });
        }
        return alertDialogBuilder.create();
    }

    public static AlertDialog getConfirmDialog(Context context, String title, String message, OnConfirmListener onConfirmListener) {
        return getConfirmDialog(context, title, message, false, onConfirmListener);
    }

    public static AlertDialog getConfirmDialog(Context context, String title, String message) {
        return getConfirmDialog(context, title, message, null);
    }

    public interface OnConfirmListener {
        void onConfirm(DialogInterface dialogInterface);
    }
}
