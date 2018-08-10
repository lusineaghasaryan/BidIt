package com.example.user.bidit.models;

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
    private boolean isApproved;
    private int mFollowersCount;

    public Item() {
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

    public Long getItemId() {
        return mItemId;
    }

    public void setItemId(Long itemId) {
        mItemId = itemId;
    }

    public ArrayList<String> getPhotoUrls() {
        return mPhotoUrls;
    }

    public void setPhotoUrls(ArrayList<String> photoUrls) {
        mPhotoUrls = photoUrls;
    }

    public String getItemTitle() {
        return mItemTitle;
    }

    public void setItemTitle(String itemTitle) {
        mItemTitle = itemTitle;
    }

    public String getItemDescription() {
        return mItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        mItemDescription = itemDescription;
    }

    public float getStartPrice() {
        return mStartPrice;
    }

    public void setStartPrice(float startPrice) {
        mStartPrice = startPrice;
    }

    public float getBuyNowPrice() {
        return mBuyNowPrice;
    }

    public void setBuyNowPrice(float buyNowPrice) {
        mBuyNowPrice = buyNowPrice;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long categoryId) {
        mCategoryId = categoryId;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public void setStartDate(long startDate) {
        mStartDate = startDate;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public float getCurrentPrice() {
        return mCurrentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        mCurrentPrice = currentPrice;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public int getFollowersCount() {
        return mFollowersCount;
    }

    public void setFollowersCount(int followersCount) {
        mFollowersCount = followersCount;
    }
}
