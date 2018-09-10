package com.example.user.bidit.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemsListViewModel extends ViewModel {
   /* private MutableLiveData<List<Item>> mItemsList = new MutableLiveData<>();

    public MutableLiveData<List<Item>> getItemsList() {
        if (mItemsList == null) {
            mItemsList = new MutableLiveData<>();
            setItems();
        }
        return mItemsList;
    }

    public void setItemsList(MutableLiveData<List<Item>> pItemsList) {
        mItemsList = pItemsList;
    }

    public void setItems(){
       // mItemsList.setValue(pItem);
    }
*/

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

    public void setItems(){
        FirebaseHelper.getItemsListFromDatabase(new FirebaseHelper.Callback<ArrayList<Item>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Item> pValue) {
                mItemsList.setValue(pValue);
            }
        });

    }











    private final MutableLiveData<Item> mItem =
            new MutableLiveData<>();

    public LiveData<Item> getItem(){
        return mItem;
    }

    public void setItem(Item pItem){
        mItem.setValue(pItem);
    }

/*
    public void updateData() {
        FirebaseHelper.getItemsListFromDatabase(new FirebaseHelper.Callback<Item>() {
            @Override
            public void callback(boolean pIsSuccess, Item pValue) {
                if (pIsSuccess) {
                    mItem.setValue(pValue);
                }
            }
        });
    }*/
}
