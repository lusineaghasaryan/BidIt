package com.example.user.bidit.fragments;

import android.os.Bundle;

import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildAllItemFragment extends BaseListItemFragment {

    private List<Item> mData = new ArrayList<>();

    public static ChildAllItemFragment newInstance() {
        ChildAllItemFragment childAllItemFragment = new ChildAllItemFragment();
        Bundle args = new Bundle();
        return childAllItemFragment;
    }

    @Override
    protected void getDataFromFB() {
        ArrayList<String> list = new ArrayList<>();
        list.add("https://images.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.photographylife.com%2Fwp" +
                "-content%2Fuploads%2F2014%2F06%2FNikon-D810-Image-Sample-7.jpg&f=1");
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setItemTitle("title" + i);
            item.setStartTime(Calendar.getInstance().getTimeInMillis() + (i * 10000));
            item.setCurrentPrice(i * 1000f);
            item.setPhotoUrls(list);

            mData.add(item);
        }
        setData(mData);
    }
}
