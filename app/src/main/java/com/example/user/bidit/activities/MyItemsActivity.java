package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.adapters.MyItemsAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyItemsActivity extends AppCompatActivity {
    private MyItemsAdapter mMyItemsAdapter;
    private List<Item> mMyItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        init();
        getItemsListByUserFromServer();
    }

    private void init() {
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
}
