package com.example.user.bidit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.ekalips.fancybuttonproj.FancyButton;
import com.ekalips.fancybuttonproj.Utils;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.PhotoUtil;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.widgets.ChoosePhotoDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountActivity extends AppCompatActivity {
    private CircleImageView mAccountImage;
    private EditText mEditTextName, mEditTextSurname, mEditTextPhone, mEditTextPassportSeries;
    private FancyButton changeButton;
    private ButtonAsyncTask mButtonAsyncTask;
    private User mUser;
    private Bitmap mBitmap = null;
    private String avatarUrl;
    private ChoosePhotoDialog mChoosePhotoDialog;
    public static final int CAMERA_REQUEST_CODE = 88;
    public static final int GALLERY_REQUEST_CODE = 77;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (ContextCompat.checkSelfPermission(MyAccountActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyAccountActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        init();
        showUserOptions();
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonAsyncTask = new ButtonAsyncTask();
                ((FancyButton) v).collapse();
                mButtonAsyncTask.execute();
                acceptChanges();
            }
        });
        mAccountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChoosePhotoDialog = new ChoosePhotoDialog(MyAccountActivity.this);
                mChoosePhotoDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case GALLERY_REQUEST_CODE: {
                Uri selectedImage = data.getData();
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case CAMERA_REQUEST_CODE: {
                mBitmap = (Bitmap) data.getExtras().get("data");
            }
        }
        avatarUrl = PhotoUtil.saveImage(MyAccountActivity.this, mBitmap);
        FirebaseHelper.sendAvatarToStorage(avatarUrl);
        mUser.setPhotoUrl(avatarUrl);
        mChoosePhotoDialog.dismiss();
        mAccountImage.setImageBitmap(mBitmap);
    }

    private void acceptChanges() {
        if (!ValidateForm.setErrorIfEmpty(createEditTextsArray())) {
            return;
        }
        setUserOptions();
        FireBaseAuthenticationManager.getInstance().updateUserInServer(mUser);
    }

    private void setUserOptions() {
        mUser.setName(mEditTextName.getText().toString());
        mUser.setSurname(mEditTextSurname.getText().toString());
        mUser.setPhoneNumber(mEditTextPhone.getText().toString());
        mUser.setPassportSeries(mEditTextPassportSeries.getText().toString());
    }

    private void showUserOptions() {
        mEditTextName.setText(firstLetterToUpCase(mUser.getName()));
        mEditTextSurname.setText(firstLetterToUpCase(mUser.getSurname()));
        mEditTextPhone.setText(mUser.getPhoneNumber());
        mEditTextPassportSeries.setText(mUser.getPassportSeries());
        if (mUser.getPhotoUrl() != null) {
            Glide.with(MyAccountActivity.this)
                    .load(mUser.getPhotoUrl())
                    .into(mAccountImage);
        }
    }

    private void init() {
        mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
        mEditTextName = findViewById(R.id.edit_text_account_name);
        mEditTextName.clearFocus();
        mEditTextSurname = findViewById(R.id.edit_text_account_surname);
        mEditTextPhone = findViewById(R.id.edit_text_account_phone_number);
        mEditTextPassportSeries = findViewById(R.id.edit_text_account_passport_series);
        changeButton = findViewById(R.id.btn_change_info_account_activity);
        mAccountImage = findViewById(R.id.account_image_my_account_activity);
    }


    private String firstLetterToUpCase(String pName) {
        return pName.substring(0, 1).toUpperCase() + pName.substring(1).toLowerCase();
    }

    private EditText[] createEditTextsArray() {
        return new EditText[]{mEditTextName, mEditTextSurname, mEditTextPhone, mEditTextPassportSeries};
    }

    class ButtonAsyncTask extends AsyncTask<Void, Void, View> {

        @Override
        protected View doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return changeButton;
        }

        @Override
        protected void onPostExecute(View view) {
            ((FancyButton) view).expand();
        }
    }
}

