package com.example.user.bidit.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.User;

public class MyAccountFragment extends Fragment {
    private EditText mEditTextName, mEditTextSurname,
            mEditTextPhone, mEditTextPassport, mEditTextEmail;
    private Button btnChange, btnEdit;
    private User mUser;
    private boolean mEditable = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        showUserOptions();
        changeFieldsVisibility(mEditable);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditable = false;
                changeFieldsVisibility(false);
                acceptChanges();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditable = true;
                changeFieldsVisibility(true);
            }
        });

    }

    private void acceptChanges() {
        setUserOptions();
        FireBaseAuthenticationManager.getInstance().updateUserInServer(mUser);
    }

    private void showUserOptions() {
        mEditTextName.setText(mUser.getName());
        mEditTextSurname.setText(mUser.getSurname());
        mEditTextEmail.setText(mUser.getEmail());
        mEditTextPhone.setText(mUser.getPhoneNumber());
        mEditTextPassport.setText(mUser.getPassportSeria());

    }

    private void setUserOptions() {
        Log.d("MYTAG", "setUserOptions: ");
        mUser = new User.Builder()
                .setName(mEditTextName.getText().toString())
                .setSurname(mEditTextSurname.getText().toString())
                .setEmail(mEditTextEmail.getText().toString())
                .setPhoneNumber(mEditTextPhone.getText().toString())
                .setPassportSeries(mEditTextPassport.getText().toString())
                .create();
    }

    private void changeFieldsVisibility(boolean bool) {
        mEditTextName.setEnabled(bool);
        mEditTextSurname.setEnabled(bool);
        mEditTextEmail.setEnabled(bool);
        mEditTextPhone.setEnabled(bool);
        mEditTextPassport.setEnabled(bool);
        if (bool) {
            btnEdit.setVisibility(View.GONE);
            btnChange.setVisibility(View.VISIBLE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
            btnChange.setVisibility(View.GONE);
        }

    }

    private void init() {
        mEditTextName = getView().findViewById(R.id.text_view_name_my_account);
        mEditTextSurname = getView().findViewById(R.id.text_view_surname_my_account);
        mEditTextEmail = getView().findViewById(R.id.text_view_email_my_account);
        mEditTextPhone = getView().findViewById(R.id.text_view_phone_my_account);
        mEditTextPassport = getView().findViewById(R.id.text_view_passport_my_account);
        btnChange = getView().findViewById(R.id.change);
        btnEdit = getView().findViewById(R.id.edit);
        mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
    }
}
