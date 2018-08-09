package com.example.user.bidit.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Item {

    private Long mUserId;
    private Long mItemId;
    private ArrayList<String> mPhotoUrls;
    private String mItemTitle;
    private String mItemDescription;
    private float mStartPrice;
    private float mBuyNowPrice;
    private long mCategoryId;
    private long mStartDate;
    private long mStartTime;
    private long mDuration;
    private float mCurrentPrice;
    private boolean isAproved;
    private int mFollowersCount;

    public Item() {
    }

}
