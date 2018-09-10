package com.example.user.bidit.models;


public class Bid {
    private String bidId;
    private long bidDate;
    private int amount;
    private String userId;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String pBidId) {
        bidId = pBidId;
    }

    public long getBidDate() {
        return bidDate;
    }

    public void setBidDate(long bidDate) {
        this.bidDate = bidDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
