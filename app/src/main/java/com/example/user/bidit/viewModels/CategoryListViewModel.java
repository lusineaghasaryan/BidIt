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
    private final MutableLiveData<Category> mCategory =
            new MutableLiveData<>();

    public LiveData<Category> getCategory(){
        return mCategory;
    }

    public void setCategory(Category category){
        mCategory.setValue(category);
    }


    public void updateData() {
        FirebaseHelper.getCategoryListFromDatabase(new FirebaseHelper.Callback<Category>() {
            @Override
            public void callback(boolean pIsSuccess, Category pValue) {
                if (pIsSuccess) {
                    mCategory.setValue(pValue);
                }
            }
        });
    }
}
