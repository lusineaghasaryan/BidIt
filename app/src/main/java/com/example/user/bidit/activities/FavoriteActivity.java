package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.FavoriteItemsAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private FavoriteItemsAdapter mFavoriteItemsAdapter;
    private List<Item> mFavoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        init();
        getItemsListByFavoriteFromServer();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_favorites);
        mFavoritesList = new ArrayList<>();
        mFavoriteItemsAdapter = new FavoriteItemsAdapter(mFavoritesList, FavoriteActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mFavoriteItemsAdapter);
    }

    private void getItemsListByFavoriteFromServer() {
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(FavoriteActivity.this).get(ItemsSpecificListVViewModel.class);
        itemsSpecificListVViewModel.updateData("userId", FireBaseAuthenticationManager.getInstance().mAuth.getUid());

        itemsSpecificListVViewModel.getItem().observe(FavoriteActivity.this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                mFavoritesList.add(pItem);
                mFavoriteItemsAdapter.notifyDataSetChanged();
            }
        });
    }
}
