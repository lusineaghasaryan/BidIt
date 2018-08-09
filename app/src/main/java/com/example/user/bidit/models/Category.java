package com.example.user.bidit.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {
    private long mCategoryId;
    private String mCategoryTitle;

    public Category(long mCategoryId, String mCategoryTitle) {
        this.mCategoryId = mCategoryId;
        this.mCategoryTitle = mCategoryTitle;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    public String getCategoryTitle() {
        return mCategoryTitle;
    }

    public void setCategoryTitle(String mCategoryTitle) {
        this.mCategoryTitle = mCategoryTitle;
    }
}
