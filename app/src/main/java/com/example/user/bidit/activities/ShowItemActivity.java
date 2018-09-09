package com.example.user.bidit.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.ImageViewPagerAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.utils.ItemStatus;
import com.example.user.bidit.widgets.ImageDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowItemActivity extends AppCompatActivity {
    public static final String TAG = "tag";

    public static final String PUT_EXTRA_KEY_MODE_MY_ITEM = "my_item";
    public static final String PUT_EXTRA_KEY_MODE_HISTORY = "history";
    public static final String PUT_EXTRA_KEY_MODE_DEFAULT = "default";

    //    field to now how show this activity
    private static String mMode;

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
    private ArrayList<String> mMessages;

    //    bid  allowing step
    private int mBidStep;

    private LinearLayout mLinearLayout;
    private TextInputEditText mInputMessage;
    private Button mBtnEnterMessage;

    //    data (temp)
    private Item mItem;

    //time
    private Handler mTimer;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        startTimer();
        setFields();
        checkMode();
        setListeners();
    }

    //    init views and fields
    private void init() {
//        load item from intent
        loadExtra();

//        find parent layout
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
        mMessages = new ArrayList<>();
        mMessages.add(String.valueOf(mItem.getCurrentPrice()));
        mMessageAdapter = new RecyclerViewMessageAdapter(mMessages);
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

        mBidStep = (int) mItem.getStartPrice() + (int) mItem.getStartPrice() / 10;

        mTimer = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (ItemStatus.isItemInProgress(mItem))
                    mTxtAuctionDuration.setText(new SimpleDateFormat("HHH:mm:ss")
                            .format(mItem.getEndDate() - System.currentTimeMillis()));
                mTimer.postDelayed(this, 1000);
            }
        };

//        check for logged in, to disable buttons
        checkForLoggedIn();
    }

    //    load item from intent
    private void loadExtra() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.containsKey(PUT_EXTRA_KEY_MODE_DEFAULT)) {
                mItem = (Item) extra.getSerializable(PUT_EXTRA_KEY_MODE_DEFAULT);
                mMode = PUT_EXTRA_KEY_MODE_DEFAULT;
            } else if (extra.containsKey(PUT_EXTRA_KEY_MODE_MY_ITEM)) {
                mItem = (Item) extra.getSerializable(PUT_EXTRA_KEY_MODE_MY_ITEM);
                mMode = PUT_EXTRA_KEY_MODE_MY_ITEM;
            } else if (extra.containsKey(PUT_EXTRA_KEY_MODE_HISTORY)) {
                mItem = (Item) extra.getSerializable(PUT_EXTRA_KEY_MODE_HISTORY);
                mMode = PUT_EXTRA_KEY_MODE_HISTORY;
            }
        }
    }

    //    set show item fields from item
    private void setFields() {
        mTxtAuctionTitle.setText(mItem.getItemTitle());
        mTxtAuctionDescription.setText(mItem.getItemDescription());
        mTxtAuctionStartDate.setText(new SimpleDateFormat("MMM/dd 'at' HH:mm")
                .format(mItem.getStartDate()));
        if (!ItemStatus.isItemInProgress(mItem)) {
            mTxtAuctionDuration.setText(new SimpleDateFormat("dd 'day' HH:mm")
                    .format(mItem.getEndDate() - mItem.getStartDate()));
        } else {
            mTxtAuctionDuration.setTextColor(Color.RED);
        }
        mTxtAuctionStartPrice.setText(String.valueOf(mItem.getStartPrice()));
        mTxtAuctionCurrentPrice.setText(String.valueOf(mItem.getCurrentPrice()));

        if (FollowAndUnfollow.isFollowed(mItem)) {
            mImgFavorite.setImageResource(R.drawable.favorite_star_48dp);
        } else {
            mImgFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
        }

        if (mItem.getStartDate() > System.currentTimeMillis()) {
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        }
    }

    private void setRecyclerSittings() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                        mImgFavorite.setImageResource(R.drawable.favorite_star_48dp);
                    } else {
                        FollowAndUnfollow.removeFromFavorite(mItem);
                        mImgFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
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
//                    pKeyEvent.startTracking();
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
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (int i = 0; i < mDotsCount; i++) {
                    mImgDots[i].setImageResource(R.drawable.view_pager_dot);
                }
                mImgDots[position].setImageResource(R.drawable.view_pager_dot_selected);
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
        final int currentNumber = Integer.parseInt(currentMessage);
        makeBid(mItem.getItemId(), currentNumber, FireBaseAuthenticationManager.getInstance()
                .mAuth.getUid())
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> pTask) {
                        if (pTask.isSuccessful()) {
                            mMessages.add(currentMessage);
                            mInputMessage.setText("");
                            mMessageAdapter.notifyDataSetChanged();
                            mRecyclerView.smoothScrollToPosition(mMessages.size() - 1); // ???
                            mRecyclerView.scrollToPosition(mMessages.size() - 1);
                        } else {
                            Log.v(TAG, "Failed: ");
                            Exception e = pTask.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.v(TAG, "Failed: " + details);
                            }
                        }
                    }
                });
    }

    //    check for user logged in
    private void checkForLoggedIn() {
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
            mImgFavorite.setEnabled(true);
            mInputMessage.setEnabled(true);
            mBtnEnterMessage.setEnabled(true);
        } else {
            mImgFavorite.setClickable(false);
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        }
    }

    //    check show item showing mode
    private void checkMode() {
        switch (mMode) {
            case PUT_EXTRA_KEY_MODE_DEFAULT: {
                mImgFavorite.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.VISIBLE);
                mLinearLayout.setEnabled(true);
                mLinearLayout.setClickable(true);
                break;
            }
            case PUT_EXTRA_KEY_MODE_MY_ITEM: {
                mImgFavorite.setVisibility(View.GONE);
                mLinearLayout.setEnabled(false);
                mLinearLayout.setClickable(false);
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
            mImgDots[i].setImageResource(R.drawable.view_pager_dot);
            mLinearLayoutDots.addView(mImgDots[i]);
        }
        mImgDots[0].setImageResource(R.drawable.view_pager_dot_selected);
    }

    private void startTimer() {
        mTimer.postDelayed(mRunnable, 0);
    }

    // Recycler view adapter
    private class RecyclerViewMessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
        private ArrayList<String> mMessages;

        RecyclerViewMessageAdapter(ArrayList<String> pMessages) {
            mMessages = pMessages;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MessageViewHolder messageViewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_message_item, parent, false));
            return messageViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            holder.getTxtBidItMessage().setText(mMessages.get(position));
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }

    // Message view holder model
    private class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView mTxtBidItMessage;

        MessageViewHolder(View itemView) {
            super(itemView);

            mTxtBidItMessage = itemView.findViewById(R.id.txt_message_view);
        }

        public TextView getTxtBidItMessage() {
            return mTxtBidItMessage;
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
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}
