package com.example.user.bidit.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.List;

public class HotItemsViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Item>> mHotItemsList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Item>> getHotItemsList() {
        return mHotItemsList;
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