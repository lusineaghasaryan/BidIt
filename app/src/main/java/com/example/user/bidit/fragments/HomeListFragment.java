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
import com.example.user.bidit.activities.HomeActivity;
import com.example.user.bidit.activities.LoginActivity;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.viewModels.CategorySearchListViewModel;
import com.example.user.bidit.viewModels.ItemsListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.bidit.utils.ItemStatus.isItemHaveBeenFinished;
import static com.example.user.bidit.utils.ItemStatus.isItemInProgress;

public class HomeListFragment extends Fragment {

    public static final String TAG = "asd";

    private OnFragmentInteractionListener mClickListener;

    private RecyclerView mRecyclerViewAllList;
    private AllListAdapter mAllListAdapter;
    private ViewPager mViewPagerHotList;
    private HotItemsVPAdapter mHotListAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Item> mHotItemData;
    private List<Item> mAllItemData;
    private int mCurrentListSize;

    public HomeListFragment() {
        // Required empty public constructor
    }

    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setListeners();
        tempLoad();
    }

    private void init(View pView) {
        mHotItemData = new ArrayList<>();
        mAllItemData = new ArrayList<>();

        mRecyclerViewAllList = pView.findViewById(R.id.recycler_view_home_fragment_list);
        mViewPagerHotList = pView.findViewById(R.id.view_pager_home_fragment_hot_list);
        mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        mAllListAdapter = new AllListAdapter();
        mHotListAdapter = new HotItemsVPAdapter(mHotItemData, getActivity());

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
//
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

    private void setHotListGone() {
        mViewPagerHotList.animate().alpha(0.0f).setDuration(700);
        mViewPagerHotList.setVisibility(View.GONE);
    }

    public void tempLoad() {
        setHotListVisible();
        //        clear list and timers(handlers)
        ItemsListViewModel itemsListViewModel = ViewModelProviders.of(this).get(ItemsListViewModel.class);
        itemsListViewModel.getItem().removeObservers(this);
        itemsListViewModel.setItem(null);
        mAllListAdapter.clearTimers();
        mAllItemData.clear();
        mHotItemData.clear();
        mHotListAdapter.notifyDataSetChanged();

//        load list
        itemsListViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                if (pItem != null) {
                    mAllItemData.add(pItem);
                    mAllListAdapter.notifyDataSetChanged();

                    mHotItemData.add(pItem);
                    mHotListAdapter.notifyDataSetChanged();
                    mViewPagerHotList.setCurrentItem(0);
                }
            }
        });
        itemsListViewModel.updateData();
    }

    public void loadSearchList(String pQuery, final String pCategoryId) {
        setHotListGone();
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
        categorySearchListViewModel.updateData(pQuery, pCategoryId, 1);
        mAllListAdapter.notifyDataSetChanged();
    }

    public void loadNext10ItemsByCategoryFromFirebase(String pCategoryName) {
        setHotListGone();
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
                .of(this).get(ItemsSpecificListVViewModel.class);

        // clear last version of list, and load new list, by category
        itemsSpecificListVViewModel.setItemsList(null);
        itemsSpecificListVViewModel.getItemsList().removeObservers(this);
        mAllListAdapter.clearTimers();
        mAllItemData.clear();

        itemsSpecificListVViewModel.setItems("categoryId",
                pCategoryName, mAllItemData.size() + 1);

        // observe on ViewModel
        itemsSpecificListVViewModel.getItemsList().observe(this,
                new Observer<ArrayList<Item>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Item> pItems) {
                        mAllItemData = pItems;
                        mAllListAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void loadNext10AllItemsFromFirebase() {
////        clear list and timers(handlers)
//        ItemsListViewModel itemsListViewModel = ViewModelProviders.of(this).get(ItemsListViewModel.class);
//        itemsListViewModel.getItem().removeObservers(this);
//        itemsListViewModel.setItem(null);
//        mAllListAdapter.clearTimers();
//        mAllItemData.clear();
//        mHotItemData.clear();
//        mAllListAdapter.notifyDataSetChanged();
//        mHotListAdapter.notifyDataSetChanged();
//
////        load list
//        itemsListViewModel.getItem().observe(this, new Observer<Item>() {
//            @Override
//            public void onChanged(@Nullable Item pItem) {
//                if (pItem != null) {
//                    mAllItemData.add(pItem);
//                    mAllListAdapter.notifyDataSetChanged();
//
//                    mHotItemData.add(pItem);
//                    mHotListAdapter.notifyDataSetChanged();
//                    mViewPagerHotList.setCurrentItem(0);
//                }
//            }
//        });
//        itemsListViewModel.updateData();


//        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders
//                .of(HomeActivity.this).get(ItemsSpecificListVViewModel.class);
//
//        // clear last version of list, and load new list, by category
//        itemsSpecificListVViewModel.setItemsList(null);
//        itemsSpecificListVViewModel.getItemsList().removeObservers(HomeActivity.this);;
//        mAllListAdapter.clearTimers();
//        mAllItemData.clear();
//        mHotItemData.clear();
//        mAllListAdapter.notifyDataSetChanged();
//        mHotListAdapter.notifyDataSetChanged();
//
//        // observe on ViewModel
//        itemsSpecificListVViewModel.getItemsList().observe(HomeActivity.this,
//                new Observer<ArrayList<Item>>() {
//                    @Override
//                    public void onChanged(@Nullable ArrayList<Item> pItems) {
//                        mAllItemData.addAll(pItems);
//                        mAllListAdapter.notifyDataSetChanged();
//                        Log.d(TAG, "onChanged: " + mAllItemData.size());
//
//                        mHotItemData.addAll(pItems);
//                        mHotListAdapter.notifyDataSetChanged();
//                        mViewPagerHotList.setCurrentItem(0);
//
//                        mCurrentListSize = mAllItemData.size();
//                    }
//                });
//        itemsSpecificListVViewModel.setItems("approved",
//                "false", mAllItemData.size() + 1);
    }

    public void notifyRecyclerAndViewPager() {
        mAllListAdapter.clearTimers();
        mAllListAdapter.notifyDataSetChanged();
        mHotListAdapter.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        void onListItemClick(Item pItem);
    }

    public void setClickListener(OnFragmentInteractionListener pClickListener) {
        mClickListener = pClickListener;
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

//            init
            ImageView imageIcon = item_view.findViewById(R.id.img_hot_item_view);
            ImageView imageFavorite = item_view.findViewById(R.id.img_hot_item_view_favorite);
            TextView title = item_view.findViewById(R.id.txt_hot_item_view_title);
            TextView price = item_view.findViewById(R.id.txt_hot_item_view_price);
//            load image from firebase, and set fields
            Glide.with(mContext).
                    load(currentItem.getPhotoUrls().get(0))
                    .into(imageIcon);
            title.setText(currentItem.getItemTitle());
            price.setText(String.valueOf(currentItem.getStartPrice()));

            container.addView(item_view);

            setListeners(item_view, position);

            return item_view;
        }

        private void follow(Item pItem, ImageView pFavoriteView) {
            if (!FollowAndUnfollow.isFollowed(pItem)) {
                FollowAndUnfollow.addToFavorite(pItem);
                pFavoriteView.setImageResource(R.drawable.favorite_star_48dp);
            } else {
                FollowAndUnfollow.removeFromFavorite(pItem);
                pFavoriteView.setImageResource(R.drawable.favorite_star_border_48dp);
            }
        }

        private void setListeners(View pView, final int position) {
            pView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    ImageView mFavorite = pView.findViewById(R.id.img_hot_item_view_favorite);
                    switch (pView.getId()) {
                        case R.id.img_hot_item_view_favorite: {
                            Log.d(TAG, "onClick: favorite");
                            if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                follow(mHotItems.get(position), mFavorite);
                            }
                            break;
                        }
                        default: {
                            Log.d(TAG, "onClick: default");
                            Intent intent = new Intent(getActivity(), ShowItemActivity.class);
                            intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_DEFAULT, mHotItems.get(position));
                            getActivity().startActivity(intent);
                            Log.d(TAG, "onClick: default");
                            break;
                        }
                    }
                }
            });
        }
    }

    private class ZoomViewPager implements ViewPager.PageTransformer {
        private int maxTranslateOffsetX;

        ZoomViewPager(Context context) {
            this.maxTranslateOffsetX = dp2px(context, 180);
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

        private int dp2px(Context context, float dipValue) {
            float m = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * m + 0.5f);
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
                    }

                    @Override
                    public void onAllFavoriteClick(int pAdapterPosition, ImageView pFavoriteView) {
                        if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            if (!FollowAndUnfollow.isFollowed(mAllItemData.get(pAdapterPosition))) {
                                FollowAndUnfollow.addToFavorite(mAllItemData.get(pAdapterPosition));
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
            AllListViewHolder allListViewHolder = new AllListViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recycler_item, parent, false));

            setListener(allListViewHolder);

            return allListViewHolder;
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
                        // TODO auction finished
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

//            set item fields into holder
            setFields(holder, item);
        }

        @Override
        public int getItemCount() {
            return mAllItemData.size();
        }

        public void clearTimers() {
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

        public AllListViewHolder(View itemView) {
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
