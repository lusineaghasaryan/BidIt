package com.example.user.bidit.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

public class ItemsListViewModel extends ViewModel {

    private final MutableLiveData<Item> mItem =
            new MutableLiveData<>();

    public LiveData<Item> getItem(){
        return mItem;
    }

    public void setItem(Item pItem){
        mItem.setValue(pItem);
    }


    public void updateData() {
        FirebaseHelper.getItemsListFromDatabase(new FirebaseHelper.Callback<Item>() {
            @Override
            public void callback(boolean pIsSuccess, Item pValue) {
                if (pIsSuccess) {
                    mItem.setValue(pValue);
                }
            }
        });
    }
}
