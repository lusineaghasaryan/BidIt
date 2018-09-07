package com.example.user.bidit.utils;

import com.example.user.bidit.models.Item;

public class ItemStatus {
    public static boolean isItemInProgress(Item pItem) {
        Long currentDate = System.currentTimeMillis();
        return currentDate > pItem.getStartDate() && currentDate < pItem.getEndDate();
    }

    public static boolean isItemHaveBeenFinished(Item pItem) {
        Long currentDate = System.currentTimeMillis();
        return currentDate > pItem.getEndDate();
    }
}
