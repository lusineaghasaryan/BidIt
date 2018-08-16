package com.example.user.bidit.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationHelper;
import com.example.user.bidit.models.User;

public class RegistrationFragment extends Fragment {
    private EditText mEditTextName, mEditTextSurname, mEditTextEmail,
            mEditTextPhone, mEditTextPassportSeria, mEditTextPassword, mEditTextPasswordRetry;
    private Button mBtnAccept;
    private String mName, mSurname, mEmail, mPhone, mPassportSeria, mPassword, mPasswordRetry;
    private User mUser;
    private View mParentLayout;
    private OnRegistrationCompleted mOnRegistrationCompleted;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
        mBtnAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//    }


    private void fillUserFields() {
        mName = mEditTextName.getText().toString();
        mSurname = mEditTextSurname.getText().toString();
        mEmail = mEditTextEmail.getText().toString();
        mPhone = mEditTextPhone.getText().toString();
        mPassportSeria = mEditTextPassportSeria.getText().toString();
        mPassword = mEditTextPassword.getText().toString();
        mPasswordRetry = mEditTextPasswordRetry.getText().toString();
        mUser = new User.Builder()
                .setName(mName)
                .setSurname(mSurname)
                .setEmail(mEmail)
                .setPhoneNumber(mPhone)
                .setPassportSeria(mPassportSeria)
                .create();
    }

    private void initViews() {
        mEditTextName = getView().findViewById(R.id.edit_text_name_registration_fragment);
        mEditTextSurname = getView().findViewById(R.id.edit_text_surname_registration_fragment);
        mEditTextEmail = getView().findViewById(R.id.edit_text_email_registration_fragment);
        mEditTextPhone = getView().findViewById(R.id.edit_text_phone_registration_fragment);
        mEditTextPassportSeria = getView().findViewById(R.id.edit_text_passport_registration_fragment);
        mEditTextPassword = getView().findViewById(R.id.edit_text_password_registration_fragment);
        mEditTextPasswordRetry = getView().findViewById(R.id.edit_text_password_retry_registration_fragment);
        mBtnAccept = getView().findViewById(R.id.btn_create_account_registration_fragment);
        mParentLayout = getView().findViewById(R.id.reg_layout);
    }

    private boolean isValidateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(mEmail)) {
            mEditTextEmail.setError("Required.");
            valid = false;
        } else {
            mEditTextEmail.setError(null);
        }
        if (TextUtils.isEmpty(mPassword)) {
            mEditTextPassword.setError("Required.");
            valid = false;
        } else {
            mEditTextPassword.setError(null);
        }
        if (TextUtils.isEmpty(mPasswordRetry)) {
            mEditTextPasswordRetry.setError("Required.");
            valid = false;
        } else {
            mEditTextPasswordRetry.setError(null);
        }
        if (TextUtils.isEmpty(mName)) {
            mEditTextName.setError("Required.");
            valid = false;
        } else {
            mEditTextName.setError(null);
        }
        if (TextUtils.isEmpty(mSurname)) {
            mEditTextSurname.setError("Required.");
            valid = false;
        } else {
            mEditTextSurname.setError(null);
        }
        if (TextUtils.isEmpty(mPhone)) {
            mEditTextPhone.setError("Required.");
            valid = false;
        } else {
            mEditTextPhone.setError(null);
        }
        if (TextUtils.isEmpty(mPassportSeria)) {
            mEditTextPassportSeria.setError("Required.");
            valid = false;
        } else {
            mEditTextPassportSeria.setError(null);
        }
        return valid;
    }

    private boolean isPasswordsCoincide() {
        return mPassword.equals(mPasswordRetry);
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void createAccount() {
        fillUserFields();
        if (!isValidateForm()) {
            showSnackBar(getString(R.string.empty_fields_message));
            return;
        }
        if (!isValidEmailAddress(mEmail)) {
            showSnackBar(getString(R.string.invalid_email_message));
            return;
        }
        if (!isPasswordsCoincide()) {
            showSnackBar(getString(R.string.wrong_passwords_message));
            return;
        }
        FireBaseAuthenticationHelper.createAccount(mUser, mPassword);
        showSnackBar(getString(R.string.registration_complated_message));
        mOnRegistrationCompleted.onFragmentRemove();
    }

    private void showSnackBar(String text) {
        Snackbar snackbar = Snackbar.make(mParentLayout, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void setOnRegistrationCompleted(OnRegistrationCompleted onRegistrationCompleted) {
        mOnRegistrationCompleted = onRegistrationCompleted;
    }

    public interface OnRegistrationCompleted {
        void onFragmentRemove();
    }


}

