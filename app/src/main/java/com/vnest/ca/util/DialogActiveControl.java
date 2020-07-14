package com.vnest.ca.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.vnest.ca.R;
import com.vnest.ca.database.sharepreference.VnestSharePreference;

public class DialogActiveControl {
    private Context context;
    private View view;
    private AlertDialog alertDialog;
    private TextView btnAccept;
    private EditText editTextPhone;
    private EditText editTextActiveCode;
    private OnActiveListener onActiveListener;


    public DialogActiveControl(Context context, OnActiveListener onActiveListener) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_active_code, null, false);
        initView(view);
        initAction(view);
        alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .create();
        this.onActiveListener = onActiveListener;
    }

    private void initView(View view) {
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextActiveCode = view.findViewById(R.id.editTextActiveCode);
    }

    private void initAction(View view) {
        btnAccept = view.findViewById(R.id.acceptBtn);
        btnAccept.setOnClickListener(v -> {
            if (checkInputNull(editTextPhone)) return;
            if (checkInputNull(editTextActiveCode)) return;
            activeCode(editTextPhone.getText().toString(), editTextActiveCode.getText().toString());
            onActiveListener.onAccept();
        });
        editTextPhone.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    return btnAccept.performClick();
            }
            return false;
        });
    }

    public void show() {
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }


    public void activeCode(String phone, String activeCode) {
        String activationCode = "Active code";
        VnestSharePreference.getInstance(context).saveActiveCode(activationCode);
        onActiveListener.onSuccess(activeCode);

    }

    private boolean checkInputNull(EditText editText) {
        if (editText.getText() == null || editText.getText().length() == 0) {
            editText.requestFocus();
            return true;
        }
        return false;
    }

    public interface OnActiveListener {
        void onAccept();

        void onSuccess(String activeCode);

        void onFail();
    }
}
