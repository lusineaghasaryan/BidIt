package com.example.user.bidit.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationHelper;
import com.example.user.bidit.models.User;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mEditTextEmail, mEditTextPassword;
    private OnFragmentChange mOnFragmentChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews();
    }

////    @Override
////    public void onStart() {
////        super.onStart();
////        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
////    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//    }

    private void initViews() {
        mEditTextEmail = getView().findViewById(R.id.edit_text_email_login_fragment);
        mEditTextPassword = getView().findViewById(R.id.edit_text_password_login_fragment);
        getView().findViewById(R.id.btn_log_in_login_fragment).setOnClickListener(this);
        getView().findViewById(R.id.btn_registration_login_fragment).setOnClickListener(this);
        getView().findViewById(R.id.btn_show).setOnClickListener(this);
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = mEditTextEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEditTextEmail.setError("Required.");
            valid = false;
        } else {
            mEditTextEmail.setError(null);
        }
        String password = mEditTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mEditTextPassword.setError("Required.");
            valid = false;
        } else {
            mEditTextPassword.setError(null);
        }
        return valid;
    }

    public void setOnFragmentChange(OnFragmentChange onLoginFragmentRemove) {
        mOnFragmentChange = onLoginFragmentRemove;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_log_in_login_fragment: {
                if (!validateForm()) {
                    return;
                }
//                showProgress
                FireBaseAuthenticationHelper.signIn(
                        mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString(),
                        new FireBaseAuthenticationHelper.LoginListener() {
                            @Override
                            public void onResponse(boolean pSuccess, User pUser) {
//                                hideProgress
                                if (pUser == null) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                                } else {
                                    mOnFragmentChange.onRemove();
                                }
                            }
                        });

                break;
            }
            case R.id.btn_registration_login_fragment: {

                mOnFragmentChange.onAdd();
                break;
            }
            case R.id.btn_show: {
                break;
            }
        }
    }

    public interface OnFragmentChange {
        void onRemove();
        void onAdd();
    }
}
