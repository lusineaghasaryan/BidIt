package com.example.user.bidit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.widgets.ProgressDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "asd";

    private EditText mEditTextEmail, mEditTextPassword;
    private ProgressDialog mProgressDialog;
    private ViewGroup mParentLayout;
    private Button mBtnSignIn, mBtnReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        setListeners();

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternet(mParentLayout);
    }

    private void init() {
        mEditTextEmail = findViewById(R.id.edit_text_email_login_activity);
        mEditTextPassword = findViewById(R.id.edit_text_password_login_activity);
        mParentLayout = findViewById(R.id.login_layout);
        mProgressDialog = new ProgressDialog(this);
        mBtnSignIn = findViewById(R.id.btn_log_in_login_activity);
        mBtnReg = findViewById(R.id.btn_registration_login_activity);
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
                            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.wrong_email_or_password_message));
                        } else if(!FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().isEmailVerified()) {
                            FireBaseAuthenticationManager.getInstance().signOut();
                            UserMessages.showSnackBarShort(mParentLayout, getString(R.string.verification_message));
                        }else
                            finish();
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
        }
    }

    private void setListeners() {
        mBtnReg.setOnClickListener(this);
        mBtnSignIn.setOnClickListener(this);

        mEditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView pTextView, int pI, KeyEvent pKeyEvent) {
                if (pI == EditorInfo.IME_ACTION_DONE) {
                    signIn();
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

}

