package com.example.user.bidit.widgets;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

import com.example.user.bidit.R;

import static com.example.user.bidit.activities.MyAccountActivity.CAMERA_REQUEST_CODE;
import static com.example.user.bidit.activities.MyAccountActivity.GALLERY_REQUEST_CODE;

public class ChoosePhotoDialog implements View.OnClickListener {
    private Dialog photoDialog;
    private Activity mDialogActivity;

    public ChoosePhotoDialog(Activity pActivity) {
        mDialogActivity = pActivity;
        photoDialog = new Dialog(mDialogActivity);
        photoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        photoDialog.setContentView(R.layout.choose_photo_dialog);
        photoDialog.setCancelable(true);
        init();
        photoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public void show() {
        photoDialog.show();
    }
    public void dismiss() {
        photoDialog.dismiss();
    }


    private void init() {
        photoDialog.findViewById(R.id.btn_dialog_cancel).setOnClickListener(this);
        photoDialog.findViewById(R.id.img_gallery_dialog).setOnClickListener(this);
        photoDialog.findViewById(R.id.img_camera_dialog).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_cancel: {
                dismiss();
                break;
            }
            case R.id.img_camera_dialog: {
                mDialogActivity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
                break;
            }
            case R.id.img_gallery_dialog:{
                mDialogActivity.startActivityForResult(new Intent
                                (Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        , GALLERY_REQUEST_CODE);
                break;
            }
        }
    }
}
