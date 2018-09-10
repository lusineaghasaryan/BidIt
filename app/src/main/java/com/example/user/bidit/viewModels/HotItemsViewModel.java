package com.example.user.bidit.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;

public class HotItemsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Item>> mHotItemsList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Item>> getHotItemsList() {
        if (mHotItemsList == null){
            mHotItemsList = new MutableLiveData<>();
        }
        return mHotItemsList;
    }

    public void setHotItemsList(MutableLiveData<ArrayList<Item>> pHotItemsList){
        mHotItemsList = pHotItemsList;
    }

    public void updateData() {
        FirebaseHelper.getHotItemsList("followersCount", new FirebaseHelper.Callback<ArrayList<Item>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Item> pValue) {
                if (pIsSuccess) {
                    mHotItemsList.setValue(pValue);
                }
            }
        });
    }
}