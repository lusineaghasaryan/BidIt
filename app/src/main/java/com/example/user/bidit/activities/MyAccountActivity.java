package com.example.user.bidit.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MyAccountActivity extends AppCompatActivity {
    private EditText mEditTextName, mEditTextSurname, mEditTextPhone, mEditTextPassportSeries;
    private FancyButton changeButton;
    private ButtonAsyncTask mButtonAsyncTask;
    private ImageView btnOK;
    private User mUser;
    private Bitmap mBitmap;
    private Drawable d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        init();
        d = getResources().getDrawable(R.drawable.ic_history_black_24dp);
        btnOK = findViewById(R.id.btn_ok);
        mButtonAsyncTask = new ButtonAsyncTask();
        showUserOptions();
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FancyButton) v).collapse();
                mButtonAsyncTask.execute();
                acceptChanges();
            }
        });
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
        mEditTextName.setText(mUser.getName());
        mEditTextSurname.setText(mUser.getSurname());
        mEditTextPhone.setText(mUser.getPhoneNumber());
        mEditTextPassportSeries.setText(mUser.getPassportSeria());
    }

    private void init() {
        mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
        mEditTextName = findViewById(R.id.edit_text_account_name);
        mEditTextSurname = findViewById(R.id.edit_text_account_surname);
        mEditTextPhone = findViewById(R.id.edit_text_account_phone_number);
        mEditTextPassportSeries = findViewById(R.id.edit_text_account_passport_series);
        changeButton = findViewById(R.id.btn_change_info_account_activity);
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stars_black_24dp);
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
            view.setVisibility(View.GONE);
            btnOK.setVisibility(View.VISIBLE);


        }
    }
}

