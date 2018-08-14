package com.example.user.bidit.fragments;

import android.os.Bundle;

import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildHotItemFragment extends BaseListItemFragment {

    private List<Item> mData = new ArrayList<>();

    public static ChildHotItemFragment newInstance() {
        ChildHotItemFragment childHotItemFragment = new ChildHotItemFragment();
        Bundle args = new Bundle();
        return childHotItemFragment;
    }

    @Override
    protected void getDataFromFB() {
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setItemTitle("title" + i);
            item.setStartTime(Calendar.getInstance().getTimeInMillis() + (i * 10000));
            item.setCurrentPrice(i * 1000f);

            mData.add(item);
        }
        setData(mData);
    }
}

