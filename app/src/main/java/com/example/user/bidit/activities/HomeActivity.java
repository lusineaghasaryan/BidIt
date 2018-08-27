package com.example.user.bidit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String PUT_EXTRA_KEY = "put extra ok";

    //    recyclers and adapters
    private RecyclerView mRecyclerViewCategories, mRecyclerViewHotList, mRecyclerViewAllList;
    private CategoryAdapter mCategoryAdapter;
    private HotListAdapter mHotListAdapter;
    private AllListAdapter mAllListAdapter;

    //    hot recycler prev and next buttons
    private ImageButton mImgBtnPrevHotItem, mImgBtnNextHotItem; // TODO next and prev logic

    //    data
    private List<Category> mCategoryData;
    private List<Item> mHotItemData;
    private List<Item> mAllItemData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadCategoryListFromFirebase();
        tempMethod();
        init();
    }

    private void init() {
        mRecyclerViewCategories = findViewById(R.id.recycler_view_home_activity_categories);
        mRecyclerViewHotList = findViewById(R.id.recycler_view_home_activity_hot_list);
        mRecyclerViewAllList = findViewById(R.id.recycler_view_home_activity_list);

        mImgBtnNextHotItem = findViewById(R.id.img_btn_home_activity_next_hot_item);
        mImgBtnPrevHotItem = findViewById(R.id.img_btn_home_activity_prev_hot_item);

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

    private void loadCategoryListFromFirebase() {
        mCategoryData = new ArrayList<>();

        // TODO load category list

//        CategoryListViewModel categoryListViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
//        categoryListViewModel.getCategory().observe(this, new Observer<Category>() {
//            @Override
//            public void onChanged(@Nullable Category category) {
//                mCategoryData.add(category);
//            }
//        });
//        categoryListViewModel.updateData();

        Category category = new Category();
        category.setCategoryTitle("AAAA");
        mCategoryData.add(category);
        category = new Category();
        category.setCategoryTitle("BBBB");
        mCategoryData.add(category);
        category = new Category();
        category.setCategoryTitle("CCCC");
        mCategoryData.add(category);
        category = new Category();
        category.setCategoryTitle("DDDD");
        mCategoryData.add(category);
        category = new Category();
        category.setCategoryTitle("EEEE");
        mCategoryData.add(category);
    }

    private void setNewHotItemsList(List<Item> pHotItemData) {
        mHotItemData = pHotItemData;
        mHotListAdapter.notifyDataSetChanged();
    }

    private void setNewAllItemsList(List<Item> pAllItemData) {
        mAllItemData = pAllItemData;
        mAllListAdapter.notifyDataSetChanged();
    }

    private void tempMethod() {
        Item item = new Item();
        item.setItemTitle("Title");
        item.setItemDescription("description");
        item.setStartDate(Calendar.getInstance().getTimeInMillis());
        item.setStartPrice(500f);
        item.setEndDate(Calendar.getInstance().getTimeInMillis() + 100000);
        item.setCurrentPrice(1000f);
        ArrayList<String> photos = new ArrayList<>();
        photos.add("");
        photos.add("");
        photos.add("");
        photos.add("");
        item.setPhotoUrls(photos);

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
    }  // TODO delete this method

    private boolean isAuctionStarted(Item pItem) {
        Long currentDate = Calendar.getInstance().getTimeInMillis();
        return currentDate > pItem.getStartDate() && currentDate < pItem.getEndDate();
    }

    private boolean isAuctionFavorite(Item pItem) {
        // TODO is favorite
        return true;
    }


    //    category items list recyclerView adapter and ViewHolder
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<Category> mCategories;

        private CategoryViewHolder.OnCategoryItemClickListener mClickListener =
                new CategoryViewHolder.OnCategoryItemClickListener() {
                    @Override
                    public void OnCategoryClick(int pAdapterPosition) {
                        // TODO load data by category

                        setNewAllItemsList(null);
                        setNewHotItemsList(null);
                    }
                };

        CategoryAdapter(List<Category> pCategories) {
            mCategories = pCategories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CategoryViewHolder categoryViewHolder = new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_category_item, parent, false));

            setListener(categoryViewHolder);

            return categoryViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.getTxtCategory().setText(mCategories.get(position).getCategoryTitle());
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        private void setListener(CategoryViewHolder pCategoryViewHolder) {
            pCategoryViewHolder.setClickListener(mClickListener);
        }
    }

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtCategory;

        private OnCategoryItemClickListener mClickListener;


        public CategoryViewHolder(View itemView) {
            super(itemView);

            mTxtCategory = itemView.findViewById(R.id.txt_home_activity_holder_category);

            setListeners(itemView);
        }

        //        getter
        public TextView getTxtCategory() {
            return mTxtCategory;
        }

        private void setListeners(View pV) {
            pV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.OnCategoryClick(getAdapterPosition());
                }
            });
        }


        //        delegation
        public interface OnCategoryItemClickListener {
            void OnCategoryClick(int pAdapterPosition);
        }

        public void setClickListener(OnCategoryItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }


    //    hot items list recyclerView adapter and ViewHolder
    private class HotListAdapter extends RecyclerView.Adapter<HotListViewHolder> {

        private List<Item> mHotItems;

        private HotListViewHolder.OnHotItemClickListener mClickListener =
                new HotListViewHolder.OnHotItemClickListener() {
                    @Override
                    public void onHotItemCLick(int pAdapterPosition) {
                        Intent intent = new Intent(HomeActivity.this, ShowItemActivity.class);
                        intent.putExtra(PUT_EXTRA_KEY, mHotItems.get(pAdapterPosition));
                        HomeActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onFavoriteClick(int pAdapterPosition) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            //TODO add to favorite
                        }
                    }
                };

        HotListAdapter(List<Item> pHotItems) {
            mHotItems = pHotItems;
        }

        @NonNull
        @Override
        public HotListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            HotListViewHolder hotListViewHolder = new HotListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_recycler_hot_item, parent, false));

            setListeners(hotListViewHolder);

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

        private void setListeners(final HotListViewHolder pHotListViewHolder) {
            pHotListViewHolder.setClickListener(mClickListener);
        }
    }

    private static class HotListViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtItemTitle, mTxtItemCurrentPrice;
        private ImageView mImgItemImage, mImgItemStatus;
        private ImageButton mImgBtnIsItemFavorite;

        private OnHotItemClickListener mClickListener;

        public HotListViewHolder(View itemView) {
            super(itemView);

            mTxtItemTitle = itemView.findViewById(R.id.txt_home_activity_holder_item_title);
            mTxtItemCurrentPrice = itemView.findViewById(R.id.txt_home_activity_holder_item_current_price);
            mImgItemImage = itemView.findViewById(R.id.img_home_activity_holder_item_image);
            mImgItemStatus = itemView.findViewById(R.id.img_home_activity_holder_item_status);
            mImgBtnIsItemFavorite = itemView.findViewById(R.id.img_btn_home_activity_holder_is_item_favorite);

            setListeners(itemView);
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

        private void setListeners(View pV) {
            pV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.onHotItemCLick(getAdapterPosition());
                }
            });

            mImgBtnIsItemFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.onFavoriteClick(getAdapterPosition());
                }
            });
        }


        //        delegation
        private interface OnHotItemClickListener {
            void onHotItemCLick(int pAdapterPosition);

            void onFavoriteClick(int pAdapterPosition);
        }

        public void setClickListener(OnHotItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }


    //    all items list recyclerView adapter and ViewHolder
    private class AllListAdapter extends RecyclerView.Adapter<AllListViewHolder> {

        private List<Item> mAllItems;

        private AllListViewHolder.OnAllItemClickListener mClickListener =
                new AllListViewHolder.OnAllItemClickListener() {
                    @Override
                    public void onAllItemClick(int pAdapterPosition) {
                        Intent intent = new Intent(HomeActivity.this, ShowItemActivity.class);
                        intent.putExtra(PUT_EXTRA_KEY, mAllItems.get(pAdapterPosition));
                        HomeActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onAllFavoriteClick(int pAdapterPosition) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            //TODO add to favorite
                        }
                    }
                };

        AllListAdapter(List<Item> pAllItems) {
            mAllItems = pAllItems;
        }

        @NonNull
        @Override
        public AllListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AllListViewHolder allListViewHolder = new AllListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_recycler_item, parent, false));

            setListener(allListViewHolder);

            return allListViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AllListViewHolder holder, int position) {
            //        get current item
            Item item = mAllItems.get(position);

//      set item fields into holder
            holder.getTxtAuctionTitle().setText(item.getItemTitle());
            holder.getTxtAuctionDate().setText(new SimpleDateFormat("MM/dd - HH:mm:ss")
                    .format(item.getStartDate()));
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

        private void setListener(AllListViewHolder pAllListViewHolder) {
            pAllListViewHolder.setClickListener(mClickListener);
        }
    }

    private static class AllListViewHolder extends RecyclerView.ViewHolder {

        //        view holder item fields
        private TextView mTxtAuctionTitle, mTxtAuctionDate, mTxtAuctionCurrentPrice;
        private ImageView mImgAuctionImage, mImgAuctionStatus;
        private ImageButton mImgBtnFavorite;

        private OnAllItemClickListener mClickListener;

        public AllListViewHolder(View itemView) {
            super(itemView);

//            initialize fields
            mTxtAuctionTitle = itemView.findViewById(R.id.txt_view_holder_auction_title);
            mTxtAuctionDate = itemView.findViewById(R.id.txt_view_holder_auction_date);
            mTxtAuctionCurrentPrice = itemView.findViewById(R.id.txt_view_holder_auction_current_price);
            mImgAuctionImage = itemView.findViewById(R.id.img_view_holder_auction_image);
            mImgAuctionStatus = itemView.findViewById(R.id.img_view_holder_auction_status);
            mImgBtnFavorite = itemView.findViewById(R.id.img_btn_view_holder_favorite_btn);

            setListeners(itemView);
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

        private void setListeners(View pV) {
            pV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.onAllItemClick(getAdapterPosition());
                }
            });

            mImgBtnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.onAllFavoriteClick(getAdapterPosition());
                }
            });
        }


        private interface OnAllItemClickListener {
            void onAllItemClick(int pAdapterPosition);

            void onAllFavoriteClick(int pAdapterPosition);
        }

        public void setClickListener(OnAllItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }
}
