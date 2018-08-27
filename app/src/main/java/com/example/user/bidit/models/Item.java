package com.example.user.bidit.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Item implements Serializable{


    private String userId;
    private String itemId;
    private ArrayList<String> photoUrls;
    private String itemTitle;
    private String itemDescription;
    private float startPrice;
    private float buyNowPrice;
    private String categoryId;
    private long startDate;
    private long endDate;
    private float currentPrice;
    private boolean isAproved;
    private int followersCount;
    private ArrayList<String> followersIds;
    private String buyerId;

    public Item() {
    }

    private Item(String pUserId, String pItemId, ArrayList<String> pPhotoUrls, String pItemTitle,
                 String pItemDescription, float pStartPrice, float pBuyNowPrice, String pCategoryId,
                 long pStartDate, long pEndDate, float pCurrentPrice, boolean pIsAproved, int pFollowersCount,
                 ArrayList<String> pFollowersIds, String pBuyerId) {
        userId = pUserId;
        itemId = pItemId;
        photoUrls = pPhotoUrls;
        itemTitle = pItemTitle;
        itemDescription = pItemDescription;
        startPrice = pStartPrice;
        buyNowPrice = pBuyNowPrice;
        categoryId = pCategoryId;
        startDate = pStartDate;
        endDate = pEndDate;
        currentPrice = pCurrentPrice;
        isAproved = pIsAproved;
        followersCount = pFollowersCount;
        followersIds = pFollowersIds;
        buyerId = pBuyerId;
    }

    private Item(final ItemBuilder builder) {
        userId = builder.userId;
        itemId = builder.itemId;
        photoUrls = builder.photoUrls;
        itemTitle = builder.itemTitle;
        itemDescription = builder.itemDescription;
        startPrice = builder.startPrice;
        buyNowPrice = builder.buyNowPrice;
        categoryId = builder.categoryId;
        startDate = builder.startDate;
        endDate = builder.endDate;
        currentPrice = builder.currentPrice;
        isAproved = builder.isAproved;
        followersCount = builder.followersCount;
        followersIds = builder.followersIds;
        buyerId = builder.buyerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String pUserId) {
        userId = pUserId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String pItemId) {
        itemId = pItemId;
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(ArrayList<String> pPhotoUrls) {
        photoUrls = pPhotoUrls;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String pItemTitle) {
        itemTitle = pItemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String pItemDescription) {
        itemDescription = pItemDescription;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float pStartPrice) {
        startPrice = pStartPrice;
    }

    public float getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(float pBuyNowPrice) {
        buyNowPrice = pBuyNowPrice;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String pCategoryId) {
        categoryId = pCategoryId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long pStartDate) {
        startDate = pStartDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long pEndDate) {
        endDate = pEndDate;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float pCurrentPrice) {
        currentPrice = pCurrentPrice;
    }

    public boolean isAproved() {
        return isAproved;
    }

    public void setAproved(boolean pAproved) {
        isAproved = pAproved;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int pFollowersCount) {
        followersCount = pFollowersCount;
    }

    public ArrayList<String> getFollowersIds() {
        return followersIds;
    }

    public void setFollowersIds(ArrayList<String> pFollowersIds) {
        followersIds = pFollowersIds;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String pBuyerId) {
        buyerId = pBuyerId;
    }

    public static class ItemBuilder {
        private String userId;
        private String itemId;
        private ArrayList<String> photoUrls;
        private String itemTitle;
        private String itemDescription;
        private float startPrice;
        private float buyNowPrice;
        private String categoryId;
        private long startDate;
        private long endDate;
        private float currentPrice;
        private boolean isAproved;
        private int followersCount;
        private ArrayList<String> followersIds;
        private String buyerId;


        public ItemBuilder setUserId(String pUserId) {
            userId = pUserId;
            return this;
        }

        public ItemBuilder setItemId(String pItemId) {
            itemId = pItemId;
            return this;
        }

        public ItemBuilder setPhotoUrls(ArrayList<String> pPhotoUrls) {
            photoUrls = pPhotoUrls;
            return this;
        }

        public ItemBuilder setItemTitle(String pItemTitle) {
            itemTitle = pItemTitle;
            return this;
        }

        public ItemBuilder setItemDescription(String pItemDescription) {
            itemDescription = pItemDescription;
            return this;
        }

        public ItemBuilder setStartPrice(float pStartPrice) {
            startPrice = pStartPrice;
            return this;
        }

        public ItemBuilder setBuyNowPrice(float pBuyNowPrice) {
            buyNowPrice = pBuyNowPrice;
            return this;
        }

        public ItemBuilder setCategoryId(String pCategoryId) {
            categoryId = pCategoryId;
            return this;
        }

        public ItemBuilder setStartDate(long pStartDate) {
            startDate = pStartDate;
            return this;
        }

        public ItemBuilder setEndDate(long pEndDate) {
            endDate = pEndDate;
            return this;
        }

        public ItemBuilder setCurrentPrice(float pCurrentPrice) {
            currentPrice = pCurrentPrice;
            return this;
        }

        public ItemBuilder setAproved(boolean pAproved) {
            isAproved = pAproved;
            return this;
        }

        public ItemBuilder setFollowersCount(int pFollowersCount) {
            followersCount = pFollowersCount;
            return this;
        }

        public ItemBuilder setFollowersIds(ArrayList<String> pFollowersIds) {
            followersIds = pFollowersIds;
            return this;
        }

        public ItemBuilder setBuyerId(String pBuyerId) {
            buyerId = pBuyerId;
            return this;
        }

        public Item build(){
            return new Item(this);
        }
    }
}
