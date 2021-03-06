package com.example.user.bidit.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.activities.LoginActivity;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.db.ItemRepository;
import com.example.user.bidit.db.ItemRoomDatabase;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.scheduler.NotificationWorkScheduler;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.utils.ZoomViewPager;
import com.example.user.bidit.viewHolders.AllListViewHolder;
import com.example.user.bidit.viewModels.HotItemsViewModel;
import com.example.user.bidit.viewModels.ItemsListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;
import com.example.user.bidit.viewModels.SearchListViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.example.user.bidit.utils.ItemStatus.isItemHaveBeenFinished;
import static com.example.user.bidit.utils.ItemStatus.isItemInProgress;

public class HomeListFragment extends Fragment {
    public static final String TAG = "asd";

    private RecyclerView mRecyclerViewAllList;
    private AllListAdapter mAllListAdapter;
    private ViewPager mViewPagerHotList;
    private HotItemsVPAdapter mHotListAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Item> mHotItemData;
    private List<Item> mAllItemData;

    private WorkManager mWorkManager;
    private ItemRepository mDatabase;

    public HomeListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        loadHomePage();
    }

    private void init(View pView) {
        mHotItemData = new ArrayList<>();
        mAllItemData = new ArrayList<>();

        mRecyclerViewAllList = pView.findViewById(R.id.recycler_view_home_fragment_list);
        mViewPagerHotList = pView.findViewById(R.id.view_pager_home_fragment_hot_list);
        mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mAllListAdapter = new AllListAdapter();
        mHotListAdapter = new HotItemsVPAdapter(getActivity());

        setRecyclerAndVPSittings();

        mWorkManager = WorkManager.getInstance();
        mDatabase = new ItemRepository(Objects.requireNonNull(getActivity()).getApplication());
    }

    private void setRecyclerAndVPSittings() {
        mRecyclerViewAllList.setHasFixedSize(true);
        mRecyclerViewAllList.setLayoutManager(mLayoutManager);
        mRecyclerViewAllList.setAdapter(mAllListAdapter);

        mViewPagerHotList.setAdapter(mHotListAdapter);
        mViewPagerHotList.setPageMargin(100);
        mViewPagerHotList.setPageTransformer(true,
                new ZoomViewPager(getActivity(), mViewPagerHotList));
        mViewPagerHotList.setClipToPadding(false);
        mViewPagerHotList.setPadding(220, 0, 220, 0);
    }

    private void setHotListVisible() {
        mViewPagerHotList.animate().alpha(1.0f).setDuration(700);
        mViewPagerHotList.setVisibility(View.VISIBLE);
    }

    private void setHotListGone() {
        mViewPagerHotList.animate().alpha(0.0f).setDuration(700);
        mViewPagerHotList.setVisibility(View.GONE);
    }

    private void clearHotList() {
        mHotItemData.clear();
        mHotListAdapter.notifyDataSetChanged();
    }

    private void clearAllList() {
        mAllListAdapter.clearTimers();
        mAllItemData.clear();
        mAllListAdapter.notifyDataSetChanged();
    }

    public void notifyRecyclerAndViewPager() {
        mAllListAdapter.clearTimers();
        mAllListAdapter.notifyDataSetChanged();
        mHotListAdapter.notifyDataSetChanged();
    }

    public void loadHomePage() {
        loadAllList();
        loadHotList();
    }

    private void loadAllList() {
        // clear last version list
        clearAllList();

        final ItemsListViewModel itemsListViewModel = ViewModelProviders
                .of(this).get(ItemsListViewModel.class);
        itemsListViewModel.setItemsList(null);
        itemsListViewModel.getItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mAllItemData.addAll(pItems);
                        mAllListAdapter.notifyDataSetChanged();
                        itemsListViewModel.getItemsList()
                                .removeObservers(HomeListFragment.this);
                    }
                });
        itemsListViewModel.setItems();
    }

    private void loadHotList() {
        setHotListVisible();
        // clear last version list
        clearHotList();

        final HotItemsViewModel hotItemsViewModel = ViewModelProviders
                .of(this)
                .get(HotItemsViewModel.class);
        hotItemsViewModel.setHotItemsList(null);
        hotItemsViewModel.getHotItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mHotItemData.addAll(pItems);
                        mHotListAdapter.notifyDataSetChanged();
                        hotItemsViewModel.getHotItemsList()
                                .removeObservers(HomeListFragment.this);
                    }
                });
        hotItemsViewModel.updateData();
    }

    public void loadSearchList(String pQuery, final String pCategoryId) {
        setHotListGone();
        // clear last version list
        clearAllList();

        final SearchListViewModel searchListViewModel = ViewModelProviders
                .of(getActivity())
                .get(SearchListViewModel.class);
        searchListViewModel.getItem().removeObservers(this);
        searchListViewModel.setItem(null);
        searchListViewModel.getItem()
                .observe(getActivity(), new Observer<Item>() {
                    @Override
                    public void onChanged(@Nullable Item pItem) {
                        mAllItemData.add(pItem);
                        mAllListAdapter.notifyDataSetChanged();
                        searchListViewModel.getItem().removeObservers(HomeListFragment.this);
                    }
                });
        searchListViewModel.updateData(pQuery, 1, pCategoryId);
    }

    public void loadItemsByCategory(String pCategoryId) {
        setHotListGone();
        // clear last version list
        clearAllList();

        final ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
                .of(this).get(ItemsSpecificListVViewModel.class);
        itemsSpecificListVViewModel.setItemsList(null);
        itemsSpecificListVViewModel.getItemsList().removeObservers(this);
        itemsSpecificListVViewModel.setItems("categoryId",
                pCategoryId, mAllItemData.size() + 1);
        itemsSpecificListVViewModel.getItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mAllItemData.addAll(pItems);
                        mAllListAdapter.notifyDataSetChanged();
                        itemsSpecificListVViewModel.getItemsList()
                                .removeObservers(HomeListFragment.this);
                    }
                });
    }

    private void addNotification(Item pItem, String pId) {
        Data data = new Data.Builder()
                .putString(NotificationWorkScheduler.DATA_KEY, pId)
                .build();

        int delay = (int) (pItem.getStartDate() - System.currentTimeMillis()) / 60000;
        OneTimeWorkRequest request = new OneTimeWorkRequest
                .Builder(NotificationWorkScheduler.class)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .setInputData(data)
                .build();

        mWorkManager.enqueue(request);
    }

    private void removeNotification() {
        // TODO
    }


    //    hot mHotItems list viewPager adapter
    private class HotItemsVPAdapter extends PagerAdapter {

        private Context mContext;

        HotItemsVPAdapter(Context pCtx) {
            mContext = pCtx;
        }

        @Override
        public int getCount() {
            return mHotItemData.size();
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
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            Item currentItem = mHotItemData.get(position);

            View itemView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.view_hot_item, container, false);

//            init
            ImageView imageIcon = itemView.findViewById(R.id.img_hot_item_view);
            final ImageView imageFavorite = itemView.findViewById(R.id.img_hot_item_view_favorite);
            TextView title = itemView.findViewById(R.id.txt_hot_item_view_title);
            TextView price = itemView.findViewById(R.id.txt_hot_item_view_price);
//            load image from firebase, and set fields
            Glide.with(mContext).
                    load(currentItem.getPhotoUrls().get(0))
                    .into(imageIcon);
            title.setText(currentItem.getItemTitle());
            price.setText(String.valueOf(currentItem.getStartPrice()));
            if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                imageFavorite.setImageResource(R.drawable.ic_nav_favorite);
            } else {
                if (FollowAndUnfollow.isFollowed(mHotItemData.get(position))) {
                    imageFavorite.setImageResource(R.drawable.star_filled);
                } else {
                    imageFavorite.setImageResource(R.drawable.ic_nav_favorite);
                }
            }

            container.addView(itemView);

//            on click listeners
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    Intent intent = new Intent(getActivity(), ShowItemActivity.class);
                    intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT, mHotItemData.get(position));
                    getActivity().startActivity(intent);
                }
            });

            imageFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        if (!FollowAndUnfollow.isFollowed(mHotItemData.get(position))) {
                            FollowAndUnfollow.addToFavorite(mHotItemData.get(position));
                            imageFavorite.setImageResource(R.drawable.star_filled);
                            //mRoomDb.daoAccess().insertItem(mHotItemData.get(position));
                        } else {
                            FollowAndUnfollow.removeFromFavorite(mHotItemData.get(position));
                            imageFavorite.setImageResource(R.drawable.ic_nav_favorite);
                            //mRoomDb.daoAccess().deleteItem(mHotItemData.get(position));
                        }
                    }
                }
            });

            return itemView;
        }
    }


    //    all mHotItems list recyclerView adapter
    private class AllListAdapter extends RecyclerView.Adapter<AllListViewHolder> {
        private List<Handler> mTimers = new ArrayList<>();

        //        on recycler item click listeners
        private AllListViewHolder.OnAllItemClickListener mClickListener =
                new AllListViewHolder.OnAllItemClickListener() {
                    @Override
                    public void onAllItemClick(int pAdapterPosition) {
                        Intent intent = new Intent(getActivity(), ShowItemActivity.class);
                        intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT,
                                mAllItemData.get(pAdapterPosition));
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onAllFavoriteClick(int pAdapterPosition, ImageView pFavoriteView) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Item currentItem = mAllItemData.get(pAdapterPosition);
                            if (!FollowAndUnfollow.isFollowed(currentItem)) {
                                FollowAndUnfollow.addToFavorite(currentItem);
                                pFavoriteView.setImageResource(R.drawable.star_filled);

                                addNotification(currentItem, currentItem.getItemId());

                                mDatabase.insert(currentItem);
                            } else {
                                FollowAndUnfollow.removeFromFavorite(currentItem);
                                pFavoriteView.setImageResource(R.drawable.ic_nav_favorite);

                                removeNotification();

                                mDatabase.delete(currentItem);
                            }
                        }
                    }
                };

        @NonNull
        @Override
        public AllListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view, parent, false);
            return new AllListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final AllListViewHolder holder, final int position) {
//            get current item
            final Item item = mAllItemData.get(position);

//            timer
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long time = item.getEndDate() - System.currentTimeMillis();
                    if (isItemInProgress(item)) {
                        holder.getTxtAuctionStartDate()
                                .setText(new SimpleDateFormat("HH:mm:ss")
                                        .format(time));
                    } else if (isItemHaveBeenFinished(item)) {
                        holder.getTxtAuctionStartDate().setText("finished");
                        handler.removeCallbacksAndMessages(null);
                        // TODO auction finished (item status)
                    } else {
                        holder.getTxtAuctionStartDate()
                                .setText(new SimpleDateFormat("dd/MMM \n HH:mm")
                                        .format(item.getStartDate()));
                    }
                    handler.postDelayed(this, 1000);
                }
            };

            mTimers.add(handler);
            runnable.run();

//            set item fields, and listeners
            setFields(holder, item);
            setListener(holder);
        }

        @Override
        public int getItemCount() {
            return mAllItemData.size();
        }

        void clearTimers() {
            for (int i = 0; i < mTimers.size(); i++) {
                mTimers.get(i).removeCallbacksAndMessages(null);
            }
            mTimers.clear();
        }

        @SuppressLint("SetTextI18n")
        private void setFields(AllListViewHolder pHolder, Item pItem) {
            pHolder.getTxtAuctionTitle().setText(pItem.getItemTitle());
            pHolder.getTxtAuctionStartPrice()
                    .setText(String.valueOf(pItem.getStartPrice()));
            pHolder.getImgAuctionImage().setImageResource(R.drawable.recycler_view_item_def_img);
            Glide.with(getActivity())
                    .load(pItem.getPhotoUrls().get(0))
                    .into(pHolder.getImgAuctionImage());
            if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                if (FollowAndUnfollow.isFollowed(pItem)) {
                    pHolder.getImgFavorite().setImageResource(R.drawable.star_filled);
                } else {
                    pHolder.getImgFavorite().setImageResource(R.drawable.ic_nav_favorite);
                }
            } else {
                pHolder.getImgFavorite().setImageResource(R.drawable.ic_nav_favorite);
            }
        }

        private void setListener(AllListViewHolder pAllListViewHolder) {
            pAllListViewHolder.setClickListener(mClickListener);
        }
    }
}
