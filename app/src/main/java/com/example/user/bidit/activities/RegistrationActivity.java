package com.example.user.bidit.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.ekalips.fancybuttonproj.FancyButton;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;

public class RegistrationActivity extends AppCompatActivity {
    private EditText mEditTextName, mEditTextSurname, mEditTextEmail,
            mEditTextPhone, mEditTextPassportSeries, mEditTextPassword, mEditTextPasswordRetry;
    private Button mBtnAccept;
    private String mName, mSurname, mEmail, mPhone, mPassportSeries, mPassword, mPasswordRetry;
    private User mUser;
    private View mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void getTextFromFilledField() {
        mName = mEditTextName.getText().toString();
        mSurname = mEditTextSurname.getText().toString();
        mEmail = mEditTextEmail.getText().toString();
        mPhone = mEditTextPhone.getText().toString();
        mPassportSeries = mEditTextPassportSeries.getText().toString();
        mPassword = mEditTextPassword.getText().toString();
        mPasswordRetry = mEditTextPasswordRetry.getText().toString();
    }

    private void initUser() {
        mUser = new User.Builder()
                .setName(mName)
                .setSurname(mSurname)
                .setEmail(mEmail)
                .setPhoneNumber(mPhone)
                .setPassportSeries(mPassportSeries)
                .create();
    }

    private void init() {
        mEditTextName = findViewById(R.id.edit_text_name_registration_activity);
        mEditTextSurname = findViewById(R.id.edit_text_surname_registration_activity);
        mEditTextEmail = findViewById(R.id.edit_text_email_registration_activity);
        mEditTextPhone = findViewById(R.id.edit_text_phone_registration_activity);
        mEditTextPassportSeries = findViewById(R.id.edit_text_passport_registration_activity);
        mEditTextPassword = findViewById(R.id.edit_text_password_registration_activity);
        mEditTextPasswordRetry = findViewById(R.id.edit_text_password_retry_registration_activity);
        mBtnAccept = findViewById(R.id.btn_create_account_registration_activity);
        mParentLayout = findViewById(R.id.reg_layout);
    }

    private EditText[] createEditTextsArray() {
        return new EditText[]{mEditTextName, mEditTextSurname,
                mEditTextPhone, mEditTextPassword, mEditTextPasswordRetry,
                mEditTextPassportSeries};
    }

    private void createAccount() {
        getTextFromFilledField();
        EditText[] allFields = createEditTextsArray();
        if (!ValidateForm.setErrorIfEmpty(allFields)) {
            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.empty_fields_message));
            return;
        }
        if (!ValidateForm.isValidEmailAddress(mEmail)) {
            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.invalid_email_message));
            return;
        }
        if (!ValidateForm.isPasswordsCoincide(mPassword, mPasswordRetry)) {
            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.wrong_passwords_message));
            return;
        }
        initUser();
        FireBaseAuthenticationManager.getInstance().createAccount(mUser, mPassword);
        UserMessages.showSnackBarShort(mParentLayout, getString(R.string.registration_complated_message));
        finish();
    }
}
