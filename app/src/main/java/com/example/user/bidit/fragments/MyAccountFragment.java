package com.example.user.bidit.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationHelper;
import com.example.user.bidit.models.User;

public class MyAccountFragment extends Fragment {
    private TextView mTextViewName, mTextViewSurname,
            mTextViewPhone, mTextViewPassport, mTextViewEmail;
    User mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init();
        setUserOptions();
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

    private void setUserOptions() {
        mTextViewName.setText(mCurrentUser.getName());
        mTextViewSurname.setText(mCurrentUser.getSurname());
        mTextViewEmail.setText(mCurrentUser.getEmail());
        mTextViewPhone.setText(mCurrentUser.getPhoneNumber());
        mTextViewPassport.setText(mCurrentUser.getPassportSeria());
    }

    private void init() {
        mTextViewName = getView().findViewById(R.id.text_view_name_my_account);
        mTextViewSurname = getView().findViewById(R.id.text_view_surname_my_account);
        mTextViewEmail = getView().findViewById(R.id.text_view_email_my_account);
        mTextViewPhone = getView().findViewById(R.id.text_view_phone_my_account);
        mTextViewPassport= getView().findViewById(R.id.text_view_passport_my_account);
        mCurrentUser = FireBaseAuthenticationHelper.getmCurrentUser();
    }
}
