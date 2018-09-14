package com.example.user.bidit.viewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.user.bidit.RoomDB.RoomDB;
import com.example.user.bidit.models.Item;

public class DatabaseViewModel extends ViewModel {

    private MutableLiveData<Item> mRoomDBMutableLiveData;

    private RoomDB mRoomDB;

    public DatabaseViewModel(RoomDB roomDB) {
        mRoomDB = roomDB;
    }

    public MutableLiveData<Item> getRoomDBMutableLiveData() {
        if (mRoomDBMutableLiveData == null) {
            mRoomDBMutableLiveData = new MutableLiveData<>();
        }
        return mRoomDBMutableLiveData;
    }

    public void setRoomDBMutableLiveData(MutableLiveData<Item> roomDBMutableLiveData) {
        mRoomDBMutableLiveData = roomDBMutableLiveData;
    }

    public void setItem(Item pItem){
        mRoomDBMutableLiveData.setValue(pItem);
    }
}
