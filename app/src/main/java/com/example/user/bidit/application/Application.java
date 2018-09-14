package com.example.user.bidit.application;


import android.arch.persistence.room.Room;

import com.example.user.bidit.RoomDB.RoomDB;

public class Application extends android.app.Application{

    public static Application mAppInstrance;
    public RoomDB mRooomDatebase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstrance = this;
        mRooomDatebase = Room.databaseBuilder(this, RoomDB.class, "database").build();
    }

    public static Application getmAppInstrance() {
        return mAppInstrance;
    }

    public RoomDB getRooomDatebase() {
        return mRooomDatebase;
    }
}
