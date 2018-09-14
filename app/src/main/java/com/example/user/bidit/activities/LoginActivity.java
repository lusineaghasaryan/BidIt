package com.example.user.bidit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.widgets.MyKeyboard;
import com.example.user.bidit.widgets.ProgressDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        setListeners();
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
                            UserMessages.showToastShort(LoginActivity.this, getString(R.string.wrong_email_or_password_message));
                        } else {
                            finish();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
}

