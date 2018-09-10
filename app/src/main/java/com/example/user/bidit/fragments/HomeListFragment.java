package com.example.user.bidit.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.activities.LoginActivity;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.viewModels.CategorySearchListViewModel;
import com.example.user.bidit.viewModels.HotItemsViewModel;
import com.example.user.bidit.viewModels.ItemsListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    private boolean isLoggedInMode;

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
        setListeners();
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
    }

    private void setRecyclerAndVPSittings() {
        mRecyclerViewAllList.setHasFixedSize(true);
        mRecyclerViewAllList.setLayoutManager(mLayoutManager);
        mRecyclerViewAllList.setAdapter(mAllListAdapter);

        mViewPagerHotList.setAdapter(mHotListAdapter);
        mViewPagerHotList.setPageMargin(100);
        mViewPagerHotList.setPageTransformer(true, new ZoomViewPager(getActivity()));
        mViewPagerHotList.setClipToPadding(false);
        mViewPagerHotList.setPadding(220, 0, 220, 0);
    }

    private void setListeners() {
        mRecyclerViewAllList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
//                if (lastVisibleItemPosition == mAllListAdapter.getItemCount() - 1) {
//                    // TODO load next 10 mHotItems from fb
//                }
            }
        });
    }

    private void setHotListVisible() {
        mViewPagerHotList.animate().alpha(1.0f).setDuration(700);
        mViewPagerHotList.setVisibility(View.VISIBLE);
    }

    private void clearAndSetGoneHotList() {
        mViewPagerHotList.animate().alpha(0.0f).setDuration(700);
        mViewPagerHotList.setVisibility(View.GONE);

        mHotItemData.clear();
        mHotListAdapter.notifyDataSetChanged();
    }

    public void notifyRecyclerAndViewPager() {
        mAllListAdapter.clearTimers();
        mAllListAdapter.notifyDataSetChanged();
        mHotListAdapter.notifyDataSetChanged();
    }

    private void loadHomeList() {
        ItemsListViewModel itemsListViewModel = ViewModelProviders
                .of(this).get(ItemsListViewModel.class);

        // clear last version of list, and load new list, by category
        itemsListViewModel.getItemsList().removeObservers(this);
        itemsListViewModel.setItemsList(null);
        mAllListAdapter.clearTimers();
        mAllItemData.clear();
        mAllListAdapter.notifyDataSetChanged();

        // observe on ViewModel
        itemsListViewModel.getItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mAllItemData.addAll(pItems);
                        mAllListAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onChanged: home " + mAllItemData.size() + " " + mHotItemData.size());
                    }
                });
        itemsListViewModel.setItems();
    }

    private void loadHotList(){
        setHotListVisible();
        mHotItemData.clear();
        mHotListAdapter.notifyDataSetChanged();
        HotItemsViewModel hotItemsViewModel = ViewModelProviders.of(this).get(HotItemsViewModel.class);
        hotItemsViewModel.getHotItemsList().removeObservers(this);
        hotItemsViewModel.setHotItemsList(null);
        hotItemsViewModel.getHotItemsList().observe(this, new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Item> pItems) {
                mHotItemData.addAll(pItems);
                mHotListAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChanged: Hot " + pItems.size() + " " + mHotItemData.size());
            }
        });
        hotItemsViewModel.updateData();
    }

    public void loadHomePage(){
        loadHomeList();
        loadHotList();
    }

    public void loadSearchList(String pQuery, final String pCategoryId) {
        clearAndSetGoneHotList();
        CategorySearchListViewModel categorySearchListViewModel = ViewModelProviders
                .of(getActivity())
                .get(CategorySearchListViewModel.class);

        mAllItemData.clear();
        mAllListAdapter.clearTimers();

        categorySearchListViewModel.getItem().removeObservers(this);
        categorySearchListViewModel.setItem(null);
        categorySearchListViewModel.getItem()
                .observe(getActivity(), new Observer<Item>() {
                    @Override
                    public void onChanged(@Nullable Item pItem) {
                        mAllItemData.add(pItem);
                        mAllListAdapter.notifyDataSetChanged();
                    }
                });
        categorySearchListViewModel.updateData(pQuery, 1, pCategoryId);
        mAllListAdapter.notifyDataSetChanged();
    }

    public void loadNext10ItemsByCategoryFromFirebase(String pCategoryId) {
        clearAndSetGoneHotList();
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
                .of(this).get(ItemsSpecificListVViewModel.class);

        // clear last version of list, and load new list, by category
        itemsSpecificListVViewModel.setItemsList(null);
        itemsSpecificListVViewModel.getItemsList().removeObservers(this);
        mAllListAdapter.clearTimers();
        mAllItemData.clear();

        itemsSpecificListVViewModel.setItems("categoryId",
                pCategoryId, mAllItemData.size() + 1);

        // observe on ViewModel
        itemsSpecificListVViewModel.getItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mAllItemData.addAll(pItems);
                        mAllListAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onChanged: 10 " + mAllItemData.size() + " " + mHotItemData.size());
                    }
                });
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
                    imageFavorite.setImageResource(R.drawable.favorite_star_48dp);
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
                            imageFavorite.setImageResource(R.drawable.favorite_star_48dp);
                        } else {
                            FollowAndUnfollow.removeFromFavorite(mHotItemData.get(position));
                            imageFavorite.setImageResource(R.drawable.ic_nav_favorite);
                        }
                    }
                }
            });

            return itemView;
        }
    }

    private class ZoomViewPager implements ViewPager.PageTransformer {
        private int maxTranslateOffsetX;

        ZoomViewPager(Context context) {
            this.maxTranslateOffsetX = dp2px(context);
        }

        @Override
        public void transformPage(@NonNull View page, float position) {
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
            }
            ViewCompat.setElevation(page, scaleFactor);
        }

        private int dp2px(Context context) {
            float m = context.getResources().getDisplayMetrics().density;
            return (int) ((float) 180 * m + 0.5f);
        }
    }


    //    all mHotItems list recyclerView adapter and ViewHolder
    private class AllListAdapter extends RecyclerView.Adapter<AllListViewHolder> {

        private List<Handler> mTimers = new ArrayList<>();

        //        on recycler item click listeners
        private AllListViewHolder.OnAllItemClickListener mClickListener =
                new AllListViewHolder.OnAllItemClickListener() {
                    @Override
                    public void onAllItemClick(int pAdapterPosition) {
                        Intent intent = new Intent(getActivity(), ShowItemActivity.class);
                        intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT, mAllItemData.get(pAdapterPosition));
                        getActivity().startActivity(intent);
                        Log.d(TAG, "onAllItemClick: ");
                    }

                    @Override
                    public void onAllFavoriteClick(int pAdapterPosition, ImageView pFavoriteView) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            if (!FollowAndUnfollow.isFollowed(mAllItemData.get(pAdapterPosition))) {
                                FollowAndUnfollow.addToFavorite(mAllItemData.get(pAdapterPosition));
                                Log.d(TAG, "onAllFavoriteClick: " + mAllItemData.size());
                                pFavoriteView.setImageResource(R.drawable.favorite_star_48dp);
                            } else {
                                FollowAndUnfollow.removeFromFavorite(mAllItemData.get(pAdapterPosition));
                                pFavoriteView.setImageResource(R.drawable.ic_nav_favorite);
                            }
                        }
                    }
                };

        @NonNull
        @Override
        public AllListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recycler_item, parent, false);
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
                        holder.getTxtAuctionStartDate().setText(new SimpleDateFormat("HH:mm:ss")
                                .format(time));
                        holder.getTxtAuctionStartDate().setTextColor(Color.RED);
                    } else if (isItemHaveBeenFinished(item)) {
                        holder.getTxtAuctionStartDate().setText("finished");
                        holder.getTxtAuctionStartDate().setTextColor(Color.BLACK);
                        handler.removeCallbacksAndMessages(null);
                        // TODO auction finished (item status)
                    } else {
                        holder.getTxtAuctionStartDate().setText(new SimpleDateFormat("MM/dd - HH:mm")
                                .format(item.getStartDate()));
                        holder.getTxtAuctionStartDate().setTextColor(Color.GRAY);
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

        private void setFields(AllListViewHolder pHolder, Item pItem) {
            pHolder.getTxtAuctionTitle().setText(pItem.getItemTitle());
            pHolder.getTxtAuctionStartPrice().setText(String.valueOf((int) pItem.getStartPrice()) + "$");
            pHolder.getImgAuctionImage().setImageResource(R.drawable.recycler_view_item_def_img);
            Glide.with(getActivity())
                    .load(pItem.getPhotoUrls().get(0))
                    .into(pHolder.getImgAuctionImage());
            if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                if (FollowAndUnfollow.isFollowed(pItem)) {
                    pHolder.getImgFavorite().setImageResource(R.drawable.favorite_star_48dp);
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

    private static class AllListViewHolder extends RecyclerView.ViewHolder {
        //        view holder item fields
        private TextView mTxtAuctionTitle, mTxtAuctionStartDate, mTxtAuctionStartPrice;
        private ImageView mImgAuctionImage;
        private ImageView mImgFavorite;

        private AllListViewHolder.OnAllItemClickListener mClickListener;

        AllListViewHolder(View itemView) {
            super(itemView);

            //        initialize fields
            mTxtAuctionTitle = itemView.findViewById(R.id.txt_recycler_item_auction_title);
            mTxtAuctionStartPrice = itemView.findViewById(R.id.txt_recycler_item_auction_start_price);
            mTxtAuctionStartDate = itemView.findViewById(R.id.txt_recycler_item_auction_start_date);
            mImgAuctionImage = itemView.findViewById(R.id.img_recycler_item_auction_image);
            mImgFavorite = itemView.findViewById(R.id.img_recycler_item_auction_follow);

            setListeners(itemView);
        }

        public TextView getTxtAuctionTitle() {
            return mTxtAuctionTitle;
        }

        public TextView getTxtAuctionStartDate() {
            return mTxtAuctionStartDate;
        }

        public TextView getTxtAuctionStartPrice() {
            return mTxtAuctionStartPrice;
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
                    mClickListener.onAllFavoriteClick(getAdapterPosition(), mImgFavorite);
                }
            });
        }

        private interface OnAllItemClickListener {
            void onAllItemClick(int pAdapterPosition);

            void onAllFavoriteClick(int pAdapterPosition, ImageView pFavoriteView);
        }

        public void setClickListener(AllListViewHolder.OnAllItemClickListener pClickListener) {
            mClickListener = pClickListener;
        }
    }
}
