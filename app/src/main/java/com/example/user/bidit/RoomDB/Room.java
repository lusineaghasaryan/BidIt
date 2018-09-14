package com.example.user.bidit.RoomDB;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.user.bidit.models.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)

public abstract class Room extends RoomDatabase{

    public abstract DaoAccess daoAccess();
}