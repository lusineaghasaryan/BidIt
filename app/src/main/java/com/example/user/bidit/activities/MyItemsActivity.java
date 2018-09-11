package com.example.user.bidit.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.user.bidit.R;
import com.example.user.bidit.adapters.MyItemsAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.SearchListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyItemsActivity extends BaseActivity {
    private MyItemsAdapter mMyItemsAdapter;
    private List<Item> mMyItemsList;
    private ConstraintLayout mParentLayout;
    public static final int REQUEST_CODE = 1;
    private int mItemEditPosition = -1;

    private FloatingSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        init();
        initListeners();
        getItemsListByUserFromServer();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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

        mSearchView = findViewById(R.id.search_view_my_items_activity);
    }

    private void initListeners() {
        MyItemsAdapter.IOnItemClick mIOnItemClick = new MyItemsAdapter.IOnItemClick() {
            @Override
            public void onItemClick(Item pItem, int pPosition) {
                Intent intent;
                if (pItem.getStartDate() > System.currentTimeMillis()) {
                    mItemEditPosition = pPosition;
                    intent = new Intent(MyItemsActivity.this, AddItemActivity.class);
                    intent.putExtra(AddItemActivity.KEY_EDIT_ITEM, pItem);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    intent = new Intent(MyItemsActivity.this, ShowItemActivity.class);
                    intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_MY_ITEM, pItem);
                    startActivity(intent);
                }
            }
        };
        mMyItemsAdapter.setIOnItemClick(mIOnItemClick);

//        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, String newQuery) {
//                final SearchListViewModel searchListViewModel = ViewModelProviders
//                        .of(MyItemsActivity.this)
//                        .get(SearchListViewModel.class);
//
//                searchListViewModel.getItem().removeObservers(MyItemsActivity.this);
//                searchListViewModel.setItem(null);
//                searchListViewModel.getItem()
//                        .observe(MyItemsActivity.this, new Observer<Item>() {
//                            @Override
//                            public void onChanged(@Nullable Item pItem) {
//                                searchListViewModel.getItem().removeObservers(MyItemsActivity.this);
//                            }
//                        });
//            }
//        });
    }

    private void getItemsListByUserFromServer() {
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(MyItemsActivity.this).get(ItemsSpecificListVViewModel.class);
        //
        itemsSpecificListVViewModel.getItem().removeObservers(MyItemsActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                Item item = (Item) data.getSerializableExtra("Item");
                mMyItemsList.set(mItemEditPosition, item);
                mMyItemsAdapter.notifyItemChanged(mItemEditPosition);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

}
