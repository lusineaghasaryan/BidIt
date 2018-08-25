package com.example.user.bidit.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    //    recyclers and adapters
    private RecyclerView mRecyclerViewCategories, mRecyclerViewHotList, mRecyclerViewAllList;
    private CategoryAdapter mCategoryAdapter;
    private HotListAdapter mHotListAdapter;
    private AllListAdapter mAllListAdapter;
    //    data
    private List<String> mCategoryData;
    private List<Item> mHotItemData;
    private List<Item> mAllItemData;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar_home_activity);
        setSupportActionBar(mToolbar);

        tempMethod();
        init();
    }

    private void init() {
        mRecyclerViewCategories = findViewById(R.id.recycler_view_home_activity_categories);
        mRecyclerViewHotList = findViewById(R.id.recycler_view_home_activity_hot_list);
        mRecyclerViewAllList = findViewById(R.id.recycler_view_home_activity_list);

        mCategoryAdapter = new CategoryAdapter(mCategoryData);
        mHotListAdapter = new HotListAdapter(mHotItemData);
        mAllListAdapter = new AllListAdapter(mAllItemData);

        setRecyclerSittings();
    }

    private void setRecyclerSittings() {
        mRecyclerViewCategories.setHasFixedSize(true);
        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCategories.setAdapter(mCategoryAdapter);

        mRecyclerViewHotList.setHasFixedSize(true);
        mRecyclerViewHotList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewHotList.setAdapter(mHotListAdapter);

        mRecyclerViewAllList.setHasFixedSize(true);
        mRecyclerViewAllList.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerViewAllList.setAdapter(mAllListAdapter);
    }

    private void tempMethod() {
        Item item = new Item();
        item.setItemTitle("Title");
        item.setItemDescription("description");
        item.setStartDate(Calendar.getInstance().getTimeInMillis());
        item.setStartPrice(500f);
        item.setEndDate(Calendar.getInstance().getTimeInMillis() + 100000);
        item.setCurrentPrice(1000f);


        mCategoryData = new ArrayList<>();
        mCategoryData.add("AAAA");
        mCategoryData.add("BBBB");
        mCategoryData.add("CCCC");
        mCategoryData.add("DDDD");
        mCategoryData.add("EEEE");
        mCategoryData.add("FFFF");

        mHotItemData = new ArrayList<>();
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);
        mHotItemData.add(item);

        mAllItemData = new ArrayList<>();
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
        mAllItemData.add(item);
    }  // ??????????????????????

    private boolean isAuctionStarted(Item pItem) {
        Long currentDate = Calendar.getInstance().getTimeInMillis();
        return currentDate > pItem.getStartDate() && currentDate < pItem.getEndDate();
    }

    private boolean isAuctionFavorite(Item pItem) {
        // TODO is favorite
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_toolbar_items, menu);
        return true;
    }

    //    category items list recyclerView adapter and ViewHolder
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<String> mCategories;

        private CategoryViewHolder.OnCategoryItemClickListener mClickListener =
                new CategoryViewHolder.OnCategoryItemClickListener() {
            @Override
            public void OnCategoryClick() {

            }
        };

        CategoryAdapter(List<String> pCategories) {
            mCategories = pCategories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CategoryViewHolder categoryViewHolder = new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_category_item, parent, false));
            return categoryViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.getTxtCategory().setText(mCategories.get(position));
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }


    }

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtCategory;

        private OnCategoryItemClickListener mClickListener;


        public CategoryViewHolder(View itemView) {
            super(itemView);

            mTxtCategory = itemView.findViewById(R.id.txt_home_activity_holder_category);
        }

//        getter
        public TextView getTxtCategory() {
            return mTxtCategory;
        }

//        delegation
        public interface OnCategoryItemClickListener{
            void OnCategoryClick();
        }

        public void setClickListener(OnCategoryItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }


    //    hot items list recyclerView adapter and ViewHolder
    private class HotListAdapter extends RecyclerView.Adapter<HotListViewHolder> {

        private List<Item> mHotItems;

        HotListAdapter(List<Item> pHotItems) {
            mHotItems = pHotItems;
        }

        @NonNull
        @Override
        public HotListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            HotListViewHolder hotListViewHolder = new HotListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_recycler_hot_item, parent, false));
            return hotListViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HotListViewHolder holder, int position) {
            holder.getTxtItemTitle().setText(mHotItems.get(position).getItemTitle());
            holder.getTxtItemCurrentPrice().setText(String.valueOf(mHotItems.get(position).getCurrentPrice()));
            holder.getImgBtnIsItemFavorite();//TODO is favorite
            holder.getImgItemImage();//TODO image load

            if (isAuctionStarted(mHotItemData.get(position))) {
                holder.getImgItemStatus().setImageResource(R.drawable.status_point_active_12dp);
            } else {
                holder.getImgItemStatus().setImageResource(R.drawable.status_point_inactive_12dp);
            }
        }

        @Override
        public int getItemCount() {
            return mHotItems.size();
        }
    }

    private class HotListViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtItemTitle, mTxtItemCurrentPrice;
        private ImageView mImgItemImage, mImgItemStatus;
        private ImageButton mImgBtnIsItemFavorite;

        public HotListViewHolder(View itemView) {
            super(itemView);

            mTxtItemTitle = itemView.findViewById(R.id.txt_home_activity_holder_item_title);
            mTxtItemCurrentPrice = itemView.findViewById(R.id.txt_home_activity_holder_item_current_price);
            mImgItemImage = itemView.findViewById(R.id.img_home_activity_holder_item_image);
            mImgItemStatus = itemView.findViewById(R.id.img_home_activity_holder_item_status);
            mImgBtnIsItemFavorite = itemView.findViewById(R.id.img_btn_home_activity_holder_is_item_favorite);
        }

        public TextView getTxtItemTitle() {
            return mTxtItemTitle;
        }

        public TextView getTxtItemCurrentPrice() {
            return mTxtItemCurrentPrice;
        }

        public ImageView getImgItemImage() {
            return mImgItemImage;
        }

        public ImageView getImgItemStatus() {
            return mImgItemStatus;
        }

        public ImageButton getImgBtnIsItemFavorite() {
            return mImgBtnIsItemFavorite;
        }

        private void setListeners() {

        }
    }


    //    all items list recyclerView adapter and ViewHolder
    private class AllListAdapter extends RecyclerView.Adapter<AllListViewHolder> {

        private List<Item> mAllItems;

        AllListAdapter(List<Item> pAllItems) {
            mAllItems = pAllItems;
        }

        @NonNull
        @Override
        public AllListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AllListViewHolder allListViewHolder = new AllListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_recycler_item, parent, false));
            return allListViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AllListViewHolder holder, int position) {
            //        get current item
            Item item = mAllItems.get(position);

//      set item fields into holder
            holder.getTxtAuctionTitle().setText(item.getItemTitle());
        /*holder.getTxtAuctionDate().setText(new SimpleDateFormat("MM/dd - HH:mm:ss")
                .format(item.getStartTime()));*/
            holder.getTxtAuctionCurrentPrice().setText(String.valueOf(item.getCurrentPrice()) + " AMD");
            holder.getImgAuctionImage().setImageResource(R.drawable.favorite_star_24dp);
            if (isAuctionStarted(item)) {
                holder.getImgAuctionStatus().setImageResource(R.drawable.status_point_active_12dp);
            } else {
                holder.getImgAuctionStatus().setImageResource(R.drawable.status_point_inactive_12dp);
            }
        }

        @Override
        public int getItemCount() {
            return mAllItems.size();
        }
    }

    private class AllListViewHolder extends RecyclerView.ViewHolder {

        //        view holder item fields
        private TextView mTxtAuctionTitle, mTxtAuctionDate, mTxtAuctionCurrentPrice;
        private ImageView mImgAuctionImage, mImgAuctionStatus;
        private ImageButton mImgBtnFavorite;

        public AllListViewHolder(View itemView) {
            super(itemView);

//            initialize fields
            mTxtAuctionTitle = itemView.findViewById(R.id.txt_view_holder_auction_title);
            mTxtAuctionDate = itemView.findViewById(R.id.txt_view_holder_auction_date);
            mTxtAuctionCurrentPrice = itemView.findViewById(R.id.txt_view_holder_auction_current_price);
            mImgAuctionImage = itemView.findViewById(R.id.img_view_holder_auction_image);
            mImgAuctionStatus = itemView.findViewById(R.id.img_view_holder_auction_status);
            mImgBtnFavorite = itemView.findViewById(R.id.img_btn_view_holder_favorite_btn);
        }

        public TextView getTxtAuctionTitle() {
            return mTxtAuctionTitle;
        }

        public TextView getTxtAuctionDate() {
            return mTxtAuctionDate;
        }

        public TextView getTxtAuctionCurrentPrice() {
            return mTxtAuctionCurrentPrice;
        }

        public ImageView getImgAuctionImage() {
            return mImgAuctionImage;
        }

        public ImageView getImgAuctionStatus() {
            return mImgAuctionStatus;
        }

        public ImageButton getImgBtnFavorite() {
            return mImgBtnFavorite;
        }
    }
}
