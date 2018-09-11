package com.example.user.bidit.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Bid;

import java.util.ArrayList;

public class BidsListViewModel extends ViewModel{

    private MutableLiveData<ArrayList<Bid>> mBidsList;

    public MutableLiveData<ArrayList<Bid>> getBidsList() {
        if (mBidsList == null) {
            mBidsList = new MutableLiveData<>();
        }
        return mBidsList;
    }

    public void setBidsList(MutableLiveData<ArrayList<Bid>> bidsList) {
        mBidsList = bidsList;
    }

    public void updateList(String pItemId) {
        FirebaseHelper.getBidListFromServer(pItemId, new FirebaseHelper.Callback<ArrayList<Bid>>() {
            @Override
            public void callback(boolean pIsSuccess, ArrayList<Bid> pValue) {
                mBidsList.setValue(pValue);
            }
        });
    }
}
