package com.example.user.bidit.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;

public class MyAccountActivity extends AppCompatActivity {
    private EditText mEditTextName, mEditTextSurname, mEditTextPhone, mEditTextPassportSeries;
    private FancyButton changeButton;
    private ButtonAsyncTask mButtonAsyncTask;
    private User mUser;


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
        mEditTextName.setText(firstLetterToUpCase(mUser.getName()));
        mEditTextSurname.setText(firstLetterToUpCase(mUser.getSurname()));
        mEditTextPhone.setText(mUser.getPhoneNumber());
        mEditTextPassportSeries.setText(mUser.getPassportSeries());
    }

    private void init() {
        mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
        mEditTextName = findViewById(R.id.edit_text_account_name);
        mEditTextSurname = findViewById(R.id.edit_text_account_surname);
        mEditTextPhone = findViewById(R.id.edit_text_account_phone_number);
        mEditTextPassportSeries = findViewById(R.id.edit_text_account_passport_series);
        changeButton = findViewById(R.id.btn_change_info_account_activity);
    }

    private String  firstLetterToUpCase(String pName) {
        return pName.substring(0,1).toUpperCase() + pName.substring(1).toLowerCase();
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

