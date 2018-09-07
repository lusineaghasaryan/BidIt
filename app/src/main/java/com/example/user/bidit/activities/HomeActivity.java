package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.CategoryListViewModel;
import com.example.user.bidit.viewModels.ItemsListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.bidit.utils.ItemStatus.isItemHaveBeenFinished;
import static com.example.user.bidit.utils.ItemStatus.isItemInProgress;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //    recyclers, viewPager and adapters
    private RecyclerView mRecyclerViewCategories, mRecyclerViewAllList;
    private LinearLayoutManager mLayoutManager;
    private CategoryAdapter mCategoryAdapter;
    private AllListAdapter mAllListAdapter;
    private ViewPager mViewPagerHotList;
    private HotItemsVPAdapter mHotListAdapter;

    //    data
    private List<Category> mCategoryData;
    private List<Item> mHotItemData;
    private List<Item> mAllItemData;

    //    search view in toolbar
    private FloatingSearchView mSearchView;

    //navDrawer
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;

    private boolean isInHome = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        setNavigationDrawer();
        loadCategoryListFromFirebase();
        loadAllItemListFromFirebase();
        setListeners();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mDrawer.openDrawer(GravityCompat.START);
//                Log.d("MYTAG", "onClick: navbar");
//                updateNavigationDrawer();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn())
            FireBaseAuthenticationManager.getInstance().initCurrentUser();
    }

    //    initialize fields
    private void init() {
        mCategoryData = new ArrayList<>();
        mHotItemData = new ArrayList<>();
        mAllItemData = new ArrayList<>();

        mRecyclerViewCategories = findViewById(R.id.recycler_view_home_activity_categories);
        mRecyclerViewAllList = findViewById(R.id.recycler_view_home_activity_list);
        mViewPagerHotList = findViewById(R.id.view_pager_home_activity_hot_list);
        mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mCategoryAdapter = new CategoryAdapter(mCategoryData);
        mAllListAdapter = new AllListAdapter(mAllItemData);
        mHotListAdapter = new HotItemsVPAdapter(mHotItemData, this);

        mSearchView = findViewById(R.id.search_view_home_activity);

        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        setRecyclerAndVPSittings();

        mSearchView.attachNavigationDrawerToMenuButton(mDrawer);
    }

    private void setRecyclerAndVPSittings() {
        mRecyclerViewCategories.setHasFixedSize(true);
        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCategories.setAdapter(mCategoryAdapter);

        mRecyclerViewAllList.setHasFixedSize(true);
        mRecyclerViewAllList.setLayoutManager(mLayoutManager);
        mRecyclerViewAllList.setAdapter(mAllListAdapter);

        mViewPagerHotList.setAdapter(mHotListAdapter);
        mViewPagerHotList.setPageMargin(100);
        mViewPagerHotList.setPageTransformer(true, new ZoomViewPager(this));
        mViewPagerHotList.setClipToPadding(false);
        mViewPagerHotList.setPadding(220, 0, 220, 0);
    }

    private void loadCategoryListFromFirebase() {
        CategoryListViewModel categoryListViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        categoryListViewModel.getCategoryList().observe(this, new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Category> pCategories) {
                mCategoryData.addAll(pCategories);
                mCategoryAdapter.notifyDataSetChanged();
            }
        });
        categoryListViewModel.updateData();
    }

    private void loadAllItemListFromFirebase() {
//        clear list and timers(handlers)
        mAllItemData.clear();
        mHotItemData.clear();
        mAllListAdapter.clearTimers();

//        load list
        ItemsListViewModel itemsListViewModel = ViewModelProviders.of(this).get(ItemsListViewModel.class);
        itemsListViewModel.getItem().removeObservers(this);
        itemsListViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                mAllItemData.add(pItem);
                mAllListAdapter.notifyDataSetChanged();

                mHotItemData.add(pItem);
                mHotListAdapter.notifyDataSetChanged();
                mViewPagerHotList.setCurrentItem(0);
            }
        });
        itemsListViewModel.updateData();
    }

    private void loadItemListByCategoryFromFirebase(String pCategoryName) {
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
                .of(HomeActivity.this).get(ItemsSpecificListVViewModel.class);

        // clear last version of list, and load new list, by category
        mAllListAdapter.clearTimers();
        mAllItemData.clear();
        itemsSpecificListVViewModel.setItem(null);
        itemsSpecificListVViewModel.getItem().removeObservers(HomeActivity.this);

        itemsSpecificListVViewModel.updateData("categoryId",
                pCategoryName);

        // observe on ViewModel
        itemsSpecificListVViewModel.getItem().observe(HomeActivity.this,
                new Observer<Item>() {
                    @Override
                    public void onChanged(@Nullable Item pItem) {
                        if (pItem != null) {
                            mAllItemData.add(pItem);
                            mAllListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private boolean isItemFavorite(Item pItem) {
        // TODO is favorite
        return true;
    }

    private void addItemToFavorite() {
        //TODO add to favorite
    }

    private void setListeners() {
        mRecyclerViewAllList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == mAllListAdapter.getItemCount() - 1) {
                    // TODO load next 10 mHotItems from fb
                }
            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

                mAllListAdapter.clearTimers();

                ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
                        .of(HomeActivity.this)
                        .get(ItemsSpecificListVViewModel.class);

                itemsSpecificListVViewModel.getItemsList()
                        .observe(HomeActivity.this, new Observer<ArrayList<Item>>() {
                            @Override
                            public void onChanged(@Nullable ArrayList<Item> pItems) {
                                mAllItemData = pItems;

                                mAllListAdapter.notifyDataSetChanged();
                            }
                        });
                itemsSpecificListVViewModel.setItems("itemTitle", newQuery, 1);
            }
        });

        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                updateNavigationDrawer();
            }

            @Override
            public void onMenuClosed() {
                if (!isInHome) {
                    mViewPagerHotList.setVisibility(View.VISIBLE);
                    loadAllItemListFromFirebase();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        if (!isInHome) {
            mViewPagerHotList.setVisibility(View.VISIBLE);
            mSearchView.setLeftMenuOpen(false);

            loadAllItemListFromFirebase();

            isInHome = true;
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        updateNavigationDrawer();
        switch (item.getItemId()) {
            case R.id.nav_item_my_account: {
                startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
                break;
            }
            case R.id.nav_item_balance: {
                break;
            }
            case R.id.nav_item_history: {
                break;
            }
            case R.id.nav_item_favorite: {
                startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                break;
            }
            case R.id.nav_item_my_items: {
                startActivity(new Intent(HomeActivity.this, MyItemsActivity.class));
                break;
            }
            case R.id.nav_item_log_out: {
                FireBaseAuthenticationManager.getInstance().signOut();
                break;
            }
            case R.id.nav_item_log_in: {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                break;
            }
            case R.id.nav_item_help: {
                break;
            }
            case R.id.nav_item_about_us:
                Intent addItemIntent = new Intent(HomeActivity.this, AddItemActivity.class);
                startActivity(addItemIntent);
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationDrawer() {
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mToggle.setDrawerIndicatorEnabled(false);
    }

    public void updateNavigationDrawer() {
        Log.d("MYTAG", "updateNavigationDrawer: ");
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, true);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(false);
        } else {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, false);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(true);
        }
    }


    //    category mHotItems list recyclerView adapter and ViewHolder
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<Category> mCategories;

        // category item click listener
        private CategoryViewHolder.OnCategoryItemClickListener mClickListener =
                new CategoryViewHolder.OnCategoryItemClickListener() {
                    @Override
                    public void OnCategoryClick(int pAdapterPosition) {
                        // hide hot items when click on category
                        mViewPagerHotList.setVisibility(View.GONE);
                        isInHome = false;
                        mSearchView.setLeftMenuOpen(true);

                        loadItemListByCategoryFromFirebase(mCategories.get(pAdapterPosition).getCategoryId());
                        mAllListAdapter.notifyDataSetChanged();
                    }
                };


        CategoryAdapter(List<Category> pCategories) {
            mCategories = pCategories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CategoryViewHolder categoryViewHolder = new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_category_item, parent, false));

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

            mTxtCategory = itemView.findViewById(R.id.txt_category_view_name);

            setListeners(itemView);
        }

        // getter
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


        // delegation
        public interface OnCategoryItemClickListener {
            void OnCategoryClick(int pAdapterPosition);
        }

        public void setClickListener(OnCategoryItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }


    //    hot mHotItems list viewPager adapter
    private class HotItemsVPAdapter extends PagerAdapter {

        private List<Item> mHotItems;

        private Context mContext;


        HotItemsVPAdapter(List<Item> pMHotItems, Context pCtx) {
            mHotItems = pMHotItems;
            mContext = pCtx;
        }

        @Override
        public int getCount() {
            return mHotItems.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((CardView) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            Item currentItem = mHotItems.get(position);

            LayoutInflater layoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View item_view = layoutInflater.inflate(R.layout.view_hot_item, container, false);

            ImageView imageView = item_view.findViewById(R.id.img_hot_item_view);
            TextView title = item_view.findViewById(R.id.txt_hot_item_view_title);
            TextView price = item_view.findViewById(R.id.txt_hot_item_view_price);

            Glide.with(mContext).
                    load(currentItem.getPhotoUrls().get(0))
                    .into(imageView);

            title.setText(currentItem.getItemTitle());
            price.setText(String.valueOf(currentItem.getStartPrice()));

            container.addView(item_view);

            setListeners(item_view, position);

            return item_view;
        }

        private void setListeners(View pView, final int position) {
            pView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    switch (pView.getId()) {
                        case R.id.img_hot_item_view_favorite: {
                            if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                addItemToFavorite();
                            }
                            break;
                        }
                        default: {
                            Intent intent = new Intent(HomeActivity.this, ShowItemActivity.class);
                            intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT, mHotItems.get(position));
                            HomeActivity.this.startActivity(intent);
                            break;
                        }
                    }
                }
            });
        }
    }

    private class ZoomViewPager implements ViewPager.PageTransformer {
//        static final float MAX_SCALE = 1.0f;
//        static final float MIN_SCALE = 0.8f;

        private int maxTranslateOffsetX;

        ZoomViewPager(Context context) {
            this.maxTranslateOffsetX = dp2px(context, 180);
        }

        @Override
        public void transformPage(@NonNull View page, float position) {

//            position = position < -1 ? -1 : position;
//            position = position > 1 ? 1 : position;
//
//            float tempScale = position < 0 ? 1 + position : 1 - position;
//
//            float slope = (MAX_SCALE - MIN_SCALE) / 1;
//            float scaleValue = MIN_SCALE + tempScale * slope;
//            page.setScaleX(scaleValue);
//            page.setScaleY(scaleValue);

            if (mViewPagerHotList == null) {
                mViewPagerHotList = (ViewPager) page.getParent();
            }

            int leftInScreen = page.getLeft() - mViewPagerHotList.getScrollX();
            int centerXInViewPager = leftInScreen + page.getMeasuredWidth() / 2;
            int offsetX = centerXInViewPager - mViewPagerHotList.getMeasuredWidth() / 2;
            float offsetRate = (float) offsetX * 0.38f / mViewPagerHotList.getMeasuredWidth();
            float scaleFactor = 1 - Math.abs(offsetRate);

            if (scaleFactor > 0) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setTranslationX(-maxTranslateOffsetX * offsetRate);
                //ViewCompat.setElevation(view, 0.0f);
            }
            ViewCompat.setElevation(page, scaleFactor);
        }

        private int dp2px(Context context, float dipValue) {
            float m = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * m + 0.5f);
        }
    }


    //    all mHotItems list recyclerView adapter and ViewHolder
    private class AllListAdapter extends RecyclerView.Adapter<AllListViewHolder> {

        private List<Item> mAllItems;

        private List<Handler> mTimers = new ArrayList<>();

        //        on recycler item click listeners
        private AllListViewHolder.OnAllItemClickListener mClickListener =
                new AllListViewHolder.OnAllItemClickListener() {
                    @Override
                    public void onAllItemClick(int pAdapterPosition) {
                        Intent intent = new Intent(HomeActivity.this, ShowItemActivity.class);
                        intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT, mAllItems.get(pAdapterPosition));
                        HomeActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onAllFavoriteClick(int pAdapterPosition) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            addItemToFavorite();
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
                    .inflate(R.layout.item_view, parent, false));

            setListener(allListViewHolder);

            return allListViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final AllListViewHolder holder, final int position) {
//            get current item
            final Item item = mAllItems.get(position);

//            timer
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long time = item.getEndDate() - System.currentTimeMillis();

                    if (isItemInProgress(item)) {
                        holder.getTxtAuctionDate().setText(new SimpleDateFormat("HH:mm:ss")
                                .format(time));
                        holder.getTxtAuctionDate().setTextColor(Color.RED);
                    } else if (isItemHaveBeenFinished(item)) {
                        holder.getTxtAuctionDate().setText("finished");
                        holder.getTxtAuctionDate().setTextColor(Color.BLACK);
                        handler.removeCallbacksAndMessages(null);
                        // TODO auction finished
                    } else {
                        holder.getTxtAuctionDate().setText(new SimpleDateFormat("MM/dd - HH:mm")
                                .format(item.getStartDate()));
                        holder.getTxtAuctionDate().setTextColor(Color.GRAY);
                    }

                    handler.postDelayed(this, 1000);
                }
            };

            mTimers.add(handler);

            runnable.run();

//            set item fields into holder
            setFields(holder, item);
        }

        @Override
        public int getItemCount() {
            return mAllItems.size();
        }

        public void clearTimers() {
            for (int i = 0; i < mTimers.size(); i++) {
                mTimers.get(i).removeCallbacksAndMessages(null);
            }

            mTimers.clear();
        }

        private void setFields(AllListViewHolder pHolder, Item pItem) {
            pHolder.getTxtAuctionTitle().setText(pItem.getItemTitle());
            pHolder.getTxtAuctionCurrentPrice().setText(String.valueOf((int) pItem.getCurrentPrice()) + "$");
            pHolder.getImgAuctionImage().setImageResource(R.drawable.recycler_view_item_def_img);

            Glide.with(HomeActivity.this)
                    .load(pItem.getPhotoUrls().get(0))
                    .into(pHolder.getImgAuctionImage());
        }

        private void setListener(AllListViewHolder pAllListViewHolder) {
            pAllListViewHolder.setClickListener(mClickListener);
        }
    }

    private static class AllListViewHolder extends RecyclerView.ViewHolder {

        //        view holder item fields
        private TextView mTxtAuctionTitle, mTxtAuctionDate, mTxtAuctionCurrentPrice;
        private ImageView mImgAuctionImage;
        private ImageView mImgFavorite;

        private OnAllItemClickListener mClickListener;

        public AllListViewHolder(View itemView) {
            super(itemView);

//            initialize fields
            mTxtAuctionTitle = itemView.findViewById(R.id.text_view_title_item_view);
            mTxtAuctionDate = itemView.findViewById(R.id.text_view_start_date_item_view);
            mTxtAuctionCurrentPrice = itemView.findViewById(R.id.text_view_start_price_item_view);
            mImgAuctionImage = itemView.findViewById(R.id.image_item_image_item_view);
            mImgFavorite = itemView.findViewById(R.id.image_view_follow_item_view);

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

        public ImageView getImgFavorite() {
            return mImgFavorite;
        }

        private void setListeners(View pV) {
            pV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    mClickListener.onAllItemClick(getAdapterPosition());
                }
            });

            mImgFavorite.setOnClickListener(new View.OnClickListener() {
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
