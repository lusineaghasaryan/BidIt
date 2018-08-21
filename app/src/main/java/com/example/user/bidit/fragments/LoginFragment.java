package com.example.user.bidit.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.bidit.R;
import com.example.user.bidit.activities.Main2Activity;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.widgets.ProgressDialog;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mEditTextEmail, mEditTextPassword;
    private OnLoginFragmentChange mOnLoginFragmentChange;
    private ProgressDialog mProgressDialog;
    private ConstraintLayout mParentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
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

    private void init() {
        mEditTextEmail = getView().findViewById(R.id.edit_text_email_login_fragment);
        mEditTextPassword = getView().findViewById(R.id.edit_text_password_login_fragment);
        mParentLayout = getView().findViewById(R.id.login_layout);
        mProgressDialog = new ProgressDialog(getContext());
        getView().findViewById(R.id.btn_log_in_login_fragment).setOnClickListener(this);
        getView().findViewById(R.id.btn_registration_login_fragment).setOnClickListener(this);
        getView().findViewById(R.id.btn_facebook_login_fragment).setOnClickListener(this);
    }

    public void setOnLoginFragmentChange(OnLoginFragmentChange onLoginFragmentChange) {
        mOnLoginFragmentChange = onLoginFragmentChange;
    }

    private void signIn() {
        mProgressDialog.show();
        FireBaseAuthenticationManager.getInstance().signIn(
                mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString(),
                new FireBaseAuthenticationManager.LoginListener() {
                    @Override
                    public void onResponse(boolean pSuccess, User user) {
                        if (user == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                        } else {
                            mOnLoginFragmentChange.onRemove();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_log_in_login_fragment: {
                if (!ValidateForm.setErrorIfEmpty(mEditTextEmail) || !ValidateForm.setErrorIfEmpty(mEditTextPassword)) {
                    return;
                }
                if (!ValidateForm.isValidEmailAddress(mEditTextEmail.getText().toString())) {
                    UserMessages.showSnackBarShort(mParentLayout, getString(R.string.invalid_email_message));
                    return;
                }
                signIn();
                break;
            }
            case R.id.btn_registration_login_fragment: {
                mOnLoginFragmentChange.onAdd();
                break;
            }
            case R.id.btn_facebook_login_fragment: {
                startActivity(new Intent(getActivity(), Main2Activity.class));
                break;
            }
        }
    }



    public interface OnLoginFragmentChange {
        void onRemove();
        void onAdd();
    }
}
