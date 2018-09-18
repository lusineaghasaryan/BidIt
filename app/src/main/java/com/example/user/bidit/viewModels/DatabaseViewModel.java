package com.example.user.bidit.viewModels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.user.bidit.db.ItemRepository;
import com.example.user.bidit.models.Item;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {

    private ItemRepository mRepository;

    private LiveData<List<Item>> mAllItems;

    public DatabaseViewModel (@NonNull Application application) {
        super(application);
        mRepository = new ItemRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    public LiveData<List<Item>> getAllItems() { return mAllItems; }

    public void insert(Item item) { mRepository.insert(item); }

    public void delete(Item item){
        mRepository.delete(item);
    }

}
