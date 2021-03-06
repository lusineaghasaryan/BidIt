package com.example.user.bidit.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.user.bidit.R;

public class ProgressDialog {

    private AlertDialog mAlertDialog;

    public ProgressDialog(Context pContext) {
        View view = LayoutInflater.from(pContext).inflate(R.layout.progress_layout, null);
        mAlertDialog =
                new AlertDialog.Builder(pContext)
                        .setView(view)
                        .create();
        mAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void show() {
        mAlertDialog.show();
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }
}
