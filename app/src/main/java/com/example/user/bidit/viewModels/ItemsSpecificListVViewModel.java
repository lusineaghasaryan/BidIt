package com.example.user.bidit.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;

public class ItemsSpecificListVViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Item>> mItemsList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Item>> getItemsList() {
        if (mItemsList == null) {
            mItemsList = new MutableLiveData<>();
            //setItems();
        }
        return mItemsList;
    }

    public void setItemsList(MutableLiveData<ArrayList<Item>> pItemsList) {
        mItemsList = pItemsList;
    }

    public void setItems(String pType, String pTypeValue, int pPageNumber) {
        FirebaseHelper.getItemsSpecific(pType, pTypeValue, pPageNumber, new FirebaseHelper.Callback<ArrayList<Item>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Item> pValue) {
                mItemsList.setValue(pValue);
            }
        });

    }


    /* for my favorite list */
    private MutableLiveData<ArrayList<Item>> mMyFavoriteItemsList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Item>> getMyFavoriteItemsList() {
        if (mMyFavoriteItemsList == null) {
            mMyFavoriteItemsList = new MutableLiveData<>();
            //setItems();
        }
        return mMyFavoriteItemsList;
    }

    public void setMyFavoriteItemsList(MutableLiveData<ArrayList<Item>> pItemsList) {
        mMyFavoriteItemsList = pItemsList;
    }

    public void setMyFavoriteItems(String pUserId) {
        FirebaseHelper.getMyFavoriteList(pUserId, new FirebaseHelper.Callback<ArrayList<Item>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Item> pValue) {
                mMyFavoriteItemsList.setValue(pValue);
                Log.d("MYTAG", "callback: " + pValue.size());
            }
        });
    }


    /* set & get only one item */
    private final MutableLiveData<Item> mItem =
            new MutableLiveData<>();

    public LiveData<Item> getItem() {
        return mItem;
    }

    public void setItem(Item pItem) {
        mItem.setValue(pItem);
    }

    public void updateData(String pType, String pTypeValue) {
        FirebaseHelper.getItemsSpecificList(pType, pTypeValue, 3, new FirebaseHelper.Callback<Item>() {
            @Override
            public void callback(boolean pIsSuccess, Item pValue) {
                if (pIsSuccess) {
                    mItem.setValue(pValue);
                }
            }
        });
    }
}
