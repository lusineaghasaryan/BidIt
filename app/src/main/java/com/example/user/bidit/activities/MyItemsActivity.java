package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.MyItemsAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyItemsActivity extends BaseActivity {
    private MyItemsAdapter mMyItemsAdapter;
    private List<Item> mMyItemsList;
    private LinearLayout mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        init();
        getItemsListByUserFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternet(mParentLayout);
    }

    private void init() {
        mParentLayout = findViewById(R.id.my_items_layout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_my_items);
        mMyItemsList = new ArrayList<>();
        mMyItemsAdapter = new MyItemsAdapter(mMyItemsList, MyItemsActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mMyItemsAdapter);
    }

    private void getItemsListByUserFromServer() {
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(MyItemsActivity.this).get(ItemsSpecificListVViewModel.class);
        itemsSpecificListVViewModel.updateData("userId", FireBaseAuthenticationManager.getInstance().mAuth.getUid());

        itemsSpecificListVViewModel.getItem().observe(MyItemsActivity.this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                mMyItemsList.add(pItem);
                mMyItemsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }
}
