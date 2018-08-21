package com.example.user.bidit.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;

public class RegistrationFragment extends Fragment {
    private EditText mEditTextName, mEditTextSurname, mEditTextEmail,
            mEditTextPhone, mEditTextPassportSeries, mEditTextPassword, mEditTextPasswordRetry;
    private Button mBtnAccept;
    private String mName, mSurname, mEmail, mPhone, mPassportSeries, mPassword, mPasswordRetry;
    private User mUser;
    private View mParentLayout;
    private OnRegistrationCompleted mOnRegistrationCompleted;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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

    private void initViews() {
        mEditTextName = getView().findViewById(R.id.edit_text_name_registration_fragment);
        mEditTextSurname = getView().findViewById(R.id.edit_text_surname_registration_fragment);
        mEditTextEmail = getView().findViewById(R.id.edit_text_email_registration_fragment);
        mEditTextPhone = getView().findViewById(R.id.edit_text_phone_registration_fragment);
        mEditTextPassportSeries = getView().findViewById(R.id.edit_text_passport_registration_fragment);
        mEditTextPassword = getView().findViewById(R.id.edit_text_password_registration_fragment);
        mEditTextPasswordRetry = getView().findViewById(R.id.edit_text_password_retry_registration_fragment);
        mBtnAccept = getView().findViewById(R.id.btn_create_account_registration_fragment);
        mParentLayout = getView().findViewById(R.id.reg_layout);
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
            UserMessages.showSnackBarShort(mParentLayout,getString(R.string.invalid_email_message));
            return;
        }
        if (!ValidateForm.isPasswordsCoincide(mPassword, mPasswordRetry)) {
            UserMessages.showSnackBarShort(mParentLayout,getString(R.string.wrong_passwords_message));
            return;
        }
        initUser();
        FireBaseAuthenticationManager.getInstance().createAccount(mUser, mPassword);
        UserMessages.showSnackBarShort(mParentLayout, getString(R.string.registration_complated_message));
        mOnRegistrationCompleted.onFragmentRemove();
    }

    public void setOnRegistrationCompleted(OnRegistrationCompleted onRegistrationCompleted) {
        mOnRegistrationCompleted = onRegistrationCompleted;
    }

    public interface OnRegistrationCompleted {
        void onFragmentRemove();
    }


}

