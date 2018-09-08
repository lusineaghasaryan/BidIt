package com.example.user.bidit.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

public class CategorySearchListViewModel extends ViewModel {
    private static final String SEARCH_BY_TYPE = "itemTitle";

    private MutableLiveData<Item> mItem = new MutableLiveData<>();

    public MutableLiveData<Item> getItem() {
        if (mItem == null) {
            mItem = new MutableLiveData<>();
        }
        return mItem;
    }

    public void setItem(MutableLiveData<Item> pItem) {
        mItem = pItem;
    }

    public void updateData(String pTypeValue, final String pSearchCategoryId, int pPageNumber) {
        FirebaseHelper.getItemsSpecificList(SEARCH_BY_TYPE, pTypeValue, pPageNumber, new FirebaseHelper.Callback<Item>() {
            @Override
            public void callback(boolean pIsSuccess, Item pValue) {
                if (pValue.getCategoryId().equals(pSearchCategoryId)) {
                    mItem.setValue(pValue);
                }
            }
        });
    }
}
