package com.example.user.bidit.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.widgets.ProgressDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditTextEmail, mEditTextPassword;
    private ProgressDialog mProgressDialog;
    private ConstraintLayout mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        mEditTextEmail = findViewById(R.id.edit_text_email_login_activity);
        mEditTextPassword = findViewById(R.id.edit_text_password_login_activity);
        mParentLayout = findViewById(R.id.login_layout);
        mProgressDialog = new ProgressDialog(this);
        findViewById(R.id.btn_log_in_login_activity).setOnClickListener(this);
        findViewById(R.id.btn_registration_login_activity).setOnClickListener(this);
        findViewById(R.id.btn_facebook_login_activity).setOnClickListener(this);
    }

    private void signIn() {
        if (!ValidateForm.setErrorIfEmpty(mEditTextEmail) || !ValidateForm.setErrorIfEmpty(mEditTextPassword)) {
            return;
        }
        if (!ValidateForm.isValidEmailAddress(mEditTextEmail.getText().toString())) {
            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.invalid_email_message));
            return;
        }
        mProgressDialog.show();
        FireBaseAuthenticationManager.getInstance().signIn(
                mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString(),
                new FireBaseAuthenticationManager.LoginListener() {
                    @Override
                    public void onResponse(boolean pSuccess, User user) {
                        if (user == null) {
                            UserMessages.showToastShort(LoginActivity.this, getString(R.string.wrong_email_or_password_message));
                        } else {
                            finish();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_log_in_login_activity: {
                signIn();
                break;
            }
            case R.id.btn_registration_login_activity: {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;
            }
            case R.id.btn_facebook_login_activity: {
                startActivity(new Intent(this, Main2Activity.class));
                break;
            }
        }
    }
}

