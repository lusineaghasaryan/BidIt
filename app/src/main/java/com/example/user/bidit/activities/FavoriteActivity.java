package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.FavoriteItemsAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    private FavoriteItemsAdapter mFavoriteItemsAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        init();
        getItemsListByFavoriteFromServer();
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_favorites);
        mFavoriteItemsAdapter = new FavoriteItemsAdapter(FavoriteActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mFavoriteItemsAdapter);

        mToolbar = findViewById(R.id.tool_bar_activity_favorite);
        mToolbar.setTitle(R.string.activity_name_favorites);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void getItemsListByFavoriteFromServer() {
     final ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(FavoriteActivity.this).get(ItemsSpecificListVViewModel.class);
     itemsSpecificListVViewModel.getMyFavoriteItemsList().observe(this, new Observer<ArrayList<Item>>() {
         @Override
         public void onChanged(@Nullable ArrayList<Item> items) {
             mFavoriteItemsAdapter.setFavoriteItemsList(items);
             mFavoriteItemsAdapter.notifyDataSetChanged();
             itemsSpecificListVViewModel.getMyFavoriteItemsList().removeObservers(FavoriteActivity.this);
         }
     });
     itemsSpecificListVViewModel.setMyFavoriteItems(FireBaseAuthenticationManager.getInstance().mAuth.getUid());
    }
}
