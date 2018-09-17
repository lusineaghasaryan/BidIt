package com.example.user.bidit.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.adapters.ImageViewPagerAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Bid;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.models.User;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.utils.ItemStatus;
import com.example.user.bidit.viewModels.BidsListViewModel;
import com.example.user.bidit.viewModels.CurrentPriceViewModel;
import com.example.user.bidit.viewModels.DatabaseViewModel;
import com.example.user.bidit.widgets.ImageDialog;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowItemActivity extends AppCompatActivity {
    public static final String TAG = "asd";

    public static final String PUT_EXTRA_KEY_MODE_MY_ITEM = "my item";
    public static final String PUT_EXTRA_KEY_MODE_HISTORY = "history";
    public static final String PUT_EXTRA_KEY_MODE_DEFAULT = "default";

    //    field to now how show this activity
    private static String mMode;
    private static boolean mIsLoggedInMode;

    private List<Bid> mBidsList;

    //    field to set scroll
    private AppBarLayout mAppBarLayout;

    //    show item images
    private ViewPager mViewPager;

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    //    zoom viewpager images
    private ImageViewPagerAdapter.OnImageClickListener mOnImageClickListener =
            new ImageViewPagerAdapter.OnImageClickListener() {
                @Override
                public void onImageClick(int pPosition) {
                    ImageDialog imageDialog = new ImageDialog(ShowItemActivity.this, mItem, pPosition);
                    imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    imageDialog.show();
                }
            };

    //    dots below view pager
    private LinearLayout mLinearLayoutDots;
    private int mDotsCount;
    private ImageView[] mImgDots;

    private TextView mTxtAuctionTitle, mTxtAuctionDescription, mTxtAuctionDuration, mTxtAuctionStartDate,
            mTxtAuctionStartPrice, mTxtAuctionCurrentPrice;
    private ImageView mImgFavorite;

    //    show chat, messages
    private RecyclerView mRecyclerView;
    private RecyclerViewMessageAdapter mMessageAdapter;

    //    bid  allowing step
    private int mBidStep;

    private LinearLayout mLinearLayout;
    private EditText mInputMessage;
    private Button mBtnEnterMessage;

    //    data (temp)
    private Item mItem;

    //    time
    private Handler mTimer;
    private Runnable mRunnable;

    public DatabaseViewModel mDatabaseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        loadBidsList();
        loadCurrentPrice();
        setLoggedInMode();
        startTimer();
        setFields();
        setLoggedInOptions();
        checkMode();
        setListeners();
        mDatabaseViewModel = ViewModelProviders.of(this).get(DatabaseViewModel.class);
    }

    //    init views and fields
    private void init() {
//        load item from intent
        loadExtra();
//        init bid step
        mBidStep = (int) mItem.getStartPrice() * 10 / 100;
//        find parent layout/
        mAppBarLayout = findViewById(R.id.app_bar_show_item_activity);

//        find and set viewPager
        mViewPager = findViewById(R.id.view_pager_show_item_activity);
        ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(this, mItem);
        mViewPager.setAdapter(imageViewPagerAdapter);
        imageViewPagerAdapter.setOnImageClickListener(mOnImageClickListener);

//        view pager dots init
        mLinearLayoutDots = findViewById(R.id.linear_show_item_activity_dots);
        mDotsCount = imageViewPagerAdapter.getCount();
        mImgDots = new ImageView[mDotsCount];
        createViewPagerDots();

//        find and set recyclerView and components
        mRecyclerView = findViewById(R.id.recycler_view_show_item_activity_messages);
        mMessageAdapter = new RecyclerViewMessageAdapter();
        setRecyclerSittings();

//        enter message layout and buttons
        mLinearLayout = findViewById(R.id.linear_show_item_activity_enter_message_layout);
        mInputMessage = findViewById(R.id.input_show_item_activity_message);
        mBtnEnterMessage = findViewById(R.id.btn_show_item_activity_enter_message);

//        find views
        mTxtAuctionTitle = findViewById(R.id.txt_show_item_activity_auction_title);
        mTxtAuctionDescription = findViewById(R.id.txt_show_item_activity_auction_description);
        mTxtAuctionStartDate = findViewById(R.id.txt_show_item_activity_auction_start_date);
        mTxtAuctionDuration = findViewById(R.id.txt_show_item_activity_auction_duration);
        mTxtAuctionStartPrice = findViewById(R.id.txt_show_item_activity_auction_start_price);
        mTxtAuctionCurrentPrice = findViewById(R.id.txt_show_item_activity_auction_current_price);
        mImgFavorite = findViewById(R.id.img_show_item_activity_favorite_btn);


        mTimer = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (ItemStatus.isItemInProgress(mItem)) {
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mTxtAuctionDuration.setText(new SimpleDateFormat("HHH:mm:ss")
                            .format(mItem.getEndDate() - System.currentTimeMillis()));
                } else {
                    mLinearLayout.setVisibility(View.GONE);
                }
                mTimer.postDelayed(this, 1000);
            }
        };
    }

    private void loadCurrentPrice() {
        CurrentPriceViewModel currentPriceViewModel = ViewModelProviders.of(ShowItemActivity.this)
                .get(CurrentPriceViewModel.class);
        currentPriceViewModel.getCurrentPrice().observe(ShowItemActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mItem.setCurrentPrice(integer);
            }
        });
    }

    private void loadBidsList() {
        mBidsList = new ArrayList<>();
        BidsListViewModel bidsListViewModel = ViewModelProviders.of(ShowItemActivity.this)
                .get(BidsListViewModel.class);

        bidsListViewModel.getBidsList().observe(ShowItemActivity.this, new Observer<ArrayList<Bid>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Bid> bids) {
                mBidsList.clear();
                mBidsList.addAll(bids);
                if (mBidsList.size() > 0) {
                    mItem.setCurrentPrice(mBidsList.get(mBidsList.size() - 1).getAmount());
//                    mTxtAuctionCurrentPrice.setText(mBidsList.get(mBidsList.size() - 1).getAmount());
                    mRecyclerView.smoothScrollToPosition(mBidsList.size() - 1);
                }
                mMessageAdapter.notifyDataSetChanged();
            }
        });
        bidsListViewModel.updateList(mItem.getItemId());
    }

    //    load item from intent
    private void loadExtra() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.containsKey(PUT_EXTRA_KEY_MODE_DEFAULT)) {
                mMode = PUT_EXTRA_KEY_MODE_DEFAULT;
            } else if (extra.containsKey(PUT_EXTRA_KEY_MODE_MY_ITEM)) {
                mMode = PUT_EXTRA_KEY_MODE_MY_ITEM;
            } else if (extra.containsKey(PUT_EXTRA_KEY_MODE_HISTORY)) {
                mMode = PUT_EXTRA_KEY_MODE_HISTORY;
            }
            mItem = (Item) extra.getSerializable(mMode);
        }
    }

    //    set show item fields from item
    private void setFields() {
        mTxtAuctionTitle.setText(mItem.getItemTitle());
        mTxtAuctionDescription.setText(mItem.getItemDescription());
        mTxtAuctionStartDate.setText(new SimpleDateFormat("MM/dd HH:mm")
                .format(mItem.getStartDate()));
        if (!ItemStatus.isItemInProgress(mItem)) {
            mTxtAuctionDuration.setText(new SimpleDateFormat("dd 'day' HH:mm")
                    .format(mItem.getEndDate() - mItem.getStartDate()));
        } else {
            mTxtAuctionDuration.setTextColor(Color.RED);
        }
        mTxtAuctionStartPrice.setText(String.valueOf(mItem.getStartPrice()));
        mTxtAuctionCurrentPrice.setText(String.valueOf(mItem.getCurrentPrice()));

        if (mIsLoggedInMode) {
            if (FollowAndUnfollow.isFollowed(mItem)) {
                mImgFavorite.setImageResource(R.drawable.star_filled);
            } else {
                mImgFavorite.setImageResource(R.drawable.star_stroke);
            }
        }

        if (mItem.getStartDate() > System.currentTimeMillis()) {
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        }
    }

    private void setLoggedInOptions() {
        if (mIsLoggedInMode) {
            mImgFavorite.setEnabled(true);
            mInputMessage.setEnabled(true);
            mBtnEnterMessage.setEnabled(true);
        } else {
            mImgFavorite.setEnabled(false);
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        }
    }

    private void setRecyclerSittings() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        );
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        mImgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                    if (!FollowAndUnfollow.isFollowed(mItem)) {
                        FollowAndUnfollow.addToFavorite(mItem);
                        mImgFavorite.setImageResource(R.drawable.star_filled);
                        //mDatabaseViewModel.insert(mItem);
                    } else {
                        FollowAndUnfollow.removeFromFavorite(mItem);
                        mImgFavorite.setImageResource(R.drawable.star_stroke);
//                        mDatabaseViewModel.getAllItems().observe(ShowItemActivity.this, new Observer<List<Item>>() {
//                            @Override
//                            public void onChanged(@Nullable List<Item> pItems) {
//                                Log.v(TAG, "Items count = " + pItems.size());
//                            }
//                        });
                    }
                } else {
                    Intent intent = new Intent(ShowItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        mBtnEnterMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                    enterMessageIntoChat();
                } else {
                    Intent intent = new Intent(ShowItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        mInputMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView pTextView, int pI, KeyEvent pKeyEvent) {
                if (pI == EditorInfo.IME_ACTION_DONE) {
                    enterMessageIntoChat();
                }
                return false;
            }
        });

        mInputMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                if (!FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                    Intent intent = new Intent(ShowItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        mInputMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View pView, MotionEvent pMotionEvent) {
                mAppBarLayout.setExpanded(false);
                mAppBarLayout.setExpanded(false);
                if (mBidsList.size() > 0) {
                    mRecyclerView.smoothScrollToPosition(mBidsList.size() - 1);
                }
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (int i = 0; i < mDotsCount; i++) {
                    mImgDots[i].setImageResource(R.drawable.point_small);
                }
                    mImgDots[position].setImageResource(R.drawable.point_stroke);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //    check valid bid price and send to fb
    private void enterMessageIntoChat() {
        final String currentMessage = mInputMessage.getText().toString();
        if (!TextUtils.isEmpty(currentMessage)) {
            if (NumberUtils.isNumeric(currentMessage)) {
                final int currentBid = Integer.parseInt(currentMessage);
                if (currentBid >= mBidStep + mItem.getCurrentPrice()) {
                    Bid bid = new Bid();
                    bid.setBidId(String.valueOf(System.currentTimeMillis()));
                    bid.setAmount(currentBid);
                    bid.setBidDate(System.currentTimeMillis());
                    bid.setUserId(FireBaseAuthenticationManager.getInstance().mAuth.getUid());
                    mTxtAuctionCurrentPrice.setText(currentMessage);
                    FirebaseHelper.addBid(mItem, bid);
                    mInputMessage.setText("");
                }
            }
        }

//        makeBid(mItem.getItemId(), currentBid, FireBaseAuthenticationManager.getInstance()
//                .mAuth.getUid())
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> pTask) {
//                        if (pTask.isSuccessful()) {
//                            mMessages.add(currentMessage);
//                            mInputMessage.setText("");
//                            mMessageAdapter.notifyDataSetChanged();
//                            mRecyclerView.smoothScrollToPosition(mMessages.size() - 1); // ???
//                            mRecyclerView.scrollToPosition(mMessages.size() - 1);
//                        } else {
//                            Log.v(TAG, "Failed: ");
//                            Exception e = pTask.getException();
//                            if (e instanceof FirebaseFunctionsException) {
//                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
//                                FirebaseFunctionsException.Code code = ffe.getCode();
//                                Object details = ffe.getDetails();
//                                Log.v(TAG, "Failed: " + details);
//                            }
//                        }
//                    }
//                });
    }

    //    check for user logged in
    private void setLoggedInMode() {
        mIsLoggedInMode = FireBaseAuthenticationManager.getInstance().isLoggedIn();
    }

    //    check show item showing mode
    private void checkMode() {
        switch (mMode) {
            case PUT_EXTRA_KEY_MODE_DEFAULT: {
                mImgFavorite.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.VISIBLE);
                mLinearLayout.setEnabled(true);
                mInputMessage.setEnabled(true);
                mBtnEnterMessage.setEnabled(true);
                break;
            }
            case PUT_EXTRA_KEY_MODE_MY_ITEM: {
                mImgFavorite.setVisibility(View.GONE);
                mLinearLayout.setEnabled(false);
                mInputMessage.setEnabled(false);
                mBtnEnterMessage.setEnabled(false);
                break;
            }
            case PUT_EXTRA_KEY_MODE_HISTORY: {
                mImgFavorite.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void createViewPagerDots() {
        for (int i = 0; i < mDotsCount; i++) {
            mImgDots[i] = new ImageView(this);
            mImgDots[i].setImageResource(R.drawable.point_small);
            mLinearLayoutDots.addView(mImgDots[i]);
        }
        mImgDots[0].setImageResource(R.drawable.point_stroke);
    }

    private void startTimer() {
        mTimer.postDelayed(mRunnable, 0);
    }

    // Recycler view adapter
    private class RecyclerViewMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int MESSAGE_TYPE_CURRENT_USER = 1;
        private static final int MESSAGE_TYPE_ANOTHER_USER = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MESSAGE_TYPE_ANOTHER_USER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_any_user_message_item, parent, false);
                return new AnotherUserMessageViewHolder(view);
            } else if (viewType == MESSAGE_TYPE_CURRENT_USER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_current_user_message_item, parent, false);
                return new CurrentUserMessageViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder pHolder, int position) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:MM");
            final Bid currentBid = mBidsList.get(position);
            if (getItemViewType(position) == MESSAGE_TYPE_ANOTHER_USER) {
                ((AnotherUserMessageViewHolder) pHolder).getTxtBidDate().setText(format.format(currentBid.getBidDate()));
                ((AnotherUserMessageViewHolder) pHolder).getTxtBidAmount().setText(String.valueOf(currentBid.getAmount()));
                FireBaseAuthenticationManager.getInstance().getUserById(currentBid.getUserId(), new FireBaseAuthenticationManager.LoginListener() {
                    @Override
                    public void onResponse(boolean pSuccess, User pUser) {
                        Glide.with(ShowItemActivity.this)
                                .load(pUser.getPhotoUrl())
                                .into(((AnotherUserMessageViewHolder) pHolder).getUserAvatar());
                        ((AnotherUserMessageViewHolder) pHolder).getTxtUserName().setText(String.valueOf(pUser.getName()));
                    }
                });
            } else if (getItemViewType(position) == MESSAGE_TYPE_CURRENT_USER) {
                ((CurrentUserMessageViewHolder) pHolder).getTxtBidDate().setText(format.format(currentBid.getBidDate()));
                ((CurrentUserMessageViewHolder) pHolder).getTxtBidAmount().setText(String.valueOf(currentBid.getAmount()));
                FireBaseAuthenticationManager.getInstance().getUserById(currentBid.getUserId(), new FireBaseAuthenticationManager.LoginListener() {
                    @Override
                    public void onResponse(boolean pSuccess, User pUser) {
                        Glide.with(ShowItemActivity.this)
                                .load(pUser.getPhotoUrl())
                                .into(((CurrentUserMessageViewHolder) pHolder).getUserAvatar());
                        ((CurrentUserMessageViewHolder) pHolder).getTxtUserName().setText(String.valueOf(pUser.getName()));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mBidsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mBidsList.get(position).getUserId().equals(FireBaseAuthenticationManager.getInstance().mAuth.getUid())) {
                return MESSAGE_TYPE_CURRENT_USER;
            } else {
                return MESSAGE_TYPE_ANOTHER_USER;
            }
        }
    }

    // Message view holder model
    private class AnotherUserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtBidAmount, mTxtUserName, mTxtBidDate;
        private ImageView mUserAvatar;

        AnotherUserMessageViewHolder(View itemView) {
            super(itemView);

            mTxtBidAmount = itemView.findViewById(R.id.txt_bid_amount_any_user_message_view);
            mTxtUserName = itemView.findViewById(R.id.txt_username_any_user_message_view);
            mTxtBidDate = itemView.findViewById(R.id.txt_bid_date_any_user_message_view);
            mUserAvatar = itemView.findViewById(R.id.img_any_user_avatar_bid_message);

        }

        public TextView getTxtBidAmount() {
            return mTxtBidAmount;
        }

        public TextView getTxtUserName() {
            return mTxtUserName;
        }

        public TextView getTxtBidDate() {
            return mTxtBidDate;
        }

        public ImageView getUserAvatar() {
            return mUserAvatar;
        }
    }

    private class CurrentUserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtBidAmount, mTxtUserName, mTxtBidDate;
        private ImageView mUserAvatar;

        CurrentUserMessageViewHolder(View itemView) {
            super(itemView);

            mTxtBidAmount = itemView.findViewById(R.id.txt_bid_amount_current_user_message_view);
            mTxtUserName = itemView.findViewById(R.id.txt_username_current_user_message_view);
            mTxtBidDate = itemView.findViewById(R.id.txt_bid_date_current_user_message_view);
            mUserAvatar = itemView.findViewById(R.id.img_current_user_avatar_bid_message);

        }

        public TextView getTxtBidAmount() {
            return mTxtBidAmount;
        }

        public TextView getTxtUserName() {
            return mTxtUserName;
        }

        public TextView getTxtBidDate() {
            return mTxtBidDate;
        }

        public ImageView getUserAvatar() {
            return mUserAvatar;
        }
    }


    public Task<String> makeBid(String itemId, int amount, String userId) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("itemId", itemId);
        data.put("amount", amount);

        return mFunctions
                .getHttpsCallable("makeBid")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}
