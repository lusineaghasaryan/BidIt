package com.example.user.bidit.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;

public class CurrentPriceViewModel extends ViewModel {

    private MutableLiveData<Integer> mCurrentPrice;

    public MutableLiveData<Integer> getCurrentPrice() {
        if (mCurrentPrice == null) {
            mCurrentPrice = new MutableLiveData<>();
        }
        return mCurrentPrice;
    }

    public void setCurrentPrice(MutableLiveData<Integer> currentPrice) {
        mCurrentPrice = currentPrice;
    }

    public void updateData(String pItemId) {
        FirebaseHelper.loadCurrentItemPrice(pItemId, new FirebaseHelper.Callback<Integer>() {
            @Override
            public void callback(boolean pIsSuccess, Integer pValue) {
                mCurrentPrice.setValue(pValue);
            }
        });
    }
}
