package com.example.user.bidit.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.widgets.ChoosePhotoDialog;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountActivity extends AppCompatActivity {
    private CircleImageView mAccountImage;
    private EditText mEditTextName, mEditTextSurname, mEditTextPhone, mEditTextPassportSeries;
    private FancyButton changeButton;
    private ButtonAsyncTask mButtonAsyncTask;
    private User mUser;
    ChoosePhotoDialog mChoosePhotoDialog;
    public static final int CAMERA_REQUEST_CODE = 88;
    public static final int GALLERY_REQUEST_CODE = 77;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
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
        Bitmap bitmap = null;
        switch (requestCode) {
            case GALLERY_REQUEST_CODE: {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                break;
            }
            case CAMERA_REQUEST_CODE:{
                    bitmap = (Bitmap) data.getExtras().get("data");
            }
        }
        mChoosePhotoDialog.dismiss();
        mAccountImage.setImageBitmap(bitmap);
    }

    private void acceptChanges() {
        setUserOptions();
        FireBaseAuthenticationManager.getInstance().updateUserInServer(mUser);
    }

    private void setUserOptions() {
        mUser = new User.Builder()
                .setName(mEditTextName.getText().toString())
                .setSurname(mEditTextSurname.getText().toString())
                .setPhoneNumber(mEditTextPhone.getText().toString())
                .setPassportSeries(mEditTextPassportSeries.getText().toString())
                .create();
    }

    private void showUserOptions() {
        mEditTextName.setHint(firstLetterToUpCase(mUser.getName()));
        mEditTextSurname.setHint(firstLetterToUpCase(mUser.getSurname()));
        mEditTextPhone.setHint(mUser.getPhoneNumber());
        mEditTextPassportSeries.setHint(mUser.getPassportSeries());
    }

    private void init() {
        mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
        mEditTextName = findViewById(R.id.edit_text_account_name);
        mEditTextSurname = findViewById(R.id.edit_text_account_surname);
        mEditTextPhone = findViewById(R.id.edit_text_account_phone_number);
        mEditTextPassportSeries = findViewById(R.id.edit_text_account_passport_series);
        changeButton = findViewById(R.id.btn_change_info_account_activity);
        mAccountImage = findViewById(R.id.account_image_my_account_activity);
    }

    private String firstLetterToUpCase(String pName) {
        return pName.substring(0, 1).toUpperCase() + pName.substring(1).toLowerCase();
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

