package com.example.user.bidit.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.user.bidit.models.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * from items")
    LiveData<List<Item>> getAllItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Query("DELETE FROM items WHERE id = :id")
    void deleteItemById(String id);

    @Query("SELECT * FROM items WHERE id = :id")
    Item getById(String id);

    @Delete
    void delete(Item item);
}
