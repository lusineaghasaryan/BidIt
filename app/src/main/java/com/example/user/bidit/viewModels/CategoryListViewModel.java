package com.example.user.bidit.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListViewModel extends ViewModel {
     private final MutableLiveData<ArrayList<Category>> mCategoryList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Category>> getCategoryList() {
        return mCategoryList;
    }

    public void updateData() {
        FirebaseHelper.getCategoryListFromDatabase(new FirebaseHelper.Callback<ArrayList<Category>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Category> pValue) {
                if (pIsSuccess) {
                    mCategoryList.setValue(pValue);
                }
            }
        });
    }
}
