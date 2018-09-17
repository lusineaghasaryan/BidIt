package com.example.user.bidit.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.user.bidit.models.Item;

import java.util.List;

public class ItemRepository {
    private ItemDao mItemDao;
    private LiveData<List<Item>> mItems;

    public ItemRepository(Application application) {
        ItemRoomDatabase db = ItemRoomDatabase.getDatabase(application);
        mItemDao = db.itemDao();
        mItems = mItemDao.getAllItems();
    }

    public LiveData<List<Item>> getAllItems() {
        return mItems;
    }

    public void insert (Item item) {
        new insertAsyncTask(mItemDao).execute(item);
    }

    public void delete(Item item){
        new deleteAsyncTask(mItemDao).execute(item);
    }

    private static class insertAsyncTask extends AsyncTask<Item, Void, Void> {

        private ItemDao mAsyncTaskDao;

        insertAsyncTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Item... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<Item, Void, Void>{
        private ItemDao mAsyncTaskDao;

        deleteAsyncTask(ItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Item... pItems) {
            mAsyncTaskDao.delete(pItems[0]);
            return null;
        }
    }
}
