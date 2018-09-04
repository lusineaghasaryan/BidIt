package com.example.user.bidit.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.ImageViewPagerAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.widgets.ImageDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShowItemActivity extends AppCompatActivity {

    public static final String PUT_EXTRA_KEY_MODE_MY_ITEM = "my_item";
    public static final String PUT_EXTRA_KEY_MODE_HISTORY = "history";
    public static final String PUT_EXTRA_KEY_MODE_DEFAULT = "default";

    //    field to now how show this activity
    private static String mMode;

    //    field to set scroll
    private AppBarLayout mAppBarLayout;

    //    show auction images, fields
    private ViewPager mViewPager;
    private ImageViewPagerAdapter mImageViewPagerAdapter;
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
    private ImageButton mImgBtnFavorite;

    //    show chat, messages
    private RecyclerView mRecyclerView;
    private RecyclerViewMessageAdapter mMessageAdapter;
    private ArrayList<String> mMessages;

    private LinearLayout mLinearLayout;
    private TextInputEditText mInputMessage;
    private Button mBtnEnterMessage;

    //    toolbar timer text view
    private TextView mTxtToolbarDuration;

    //    data (temp)
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        startToolbarTimer();
        setFields();
        checkForMode();
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
        mImageViewPagerAdapter = new ImageViewPagerAdapter(this, mItem);
        mViewPager.setAdapter(mImageViewPagerAdapter);
        mImageViewPagerAdapter.setOnImageClickListener(mOnImageClickListener);

//        view pager dots init
        mLinearLayoutDots = findViewById(R.id.linear_show_item_activity_count_dots);
        mDotsCount = mImageViewPagerAdapter.getCount();
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
        mImgBtnFavorite = findViewById(R.id.img_btn_show_item_activity_favorite_btn);

        mTxtToolbarDuration = findViewById(R.id.txt_toolbar_show_item_activity_duration);

//        check for logged in to disable buttons
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
        mTxtAuctionDuration.setText(new SimpleDateFormat("dd 'day' HH:mm")
                .format(mItem.getEndDate() - mItem.getStartDate()));
        mTxtAuctionStartPrice.setText(String.valueOf(mItem.getStartPrice()));
        mTxtAuctionCurrentPrice.setText(String.valueOf(mItem.getCurrentPrice()));

        if (isFavorite()) {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_48dp);
        } else {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
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
        mImgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
                    addItemToFavorite();
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
                    pKeyEvent.startTracking();
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
        // TODO add change mCurrentPrice method on firebase, and add check for correct number logic
        String currentMessage = mInputMessage.getText().toString();
        if (!currentMessage.isEmpty()) {
            mMessages.add(currentMessage);
            mInputMessage.setText("");
            mMessageAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mMessages.size() - 1); // ???
            mRecyclerView.scrollToPosition(mMessages.size() - 1);
        }
    }

    //    check for user logged in
    private void checkForLoggedIn() {
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
            mImgBtnFavorite.setEnabled(true);
            mInputMessage.setEnabled(true);
            mBtnEnterMessage.setEnabled(true);
        } else {
            mImgBtnFavorite.setClickable(false);
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        }
    }

    //    check show item showing mode
    private void checkForMode() {
        switch (mMode) {
            case PUT_EXTRA_KEY_MODE_DEFAULT: {
                mImgBtnFavorite.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.VISIBLE);
                mLinearLayout.setEnabled(true);
                mLinearLayout.setClickable(true);
                break;
            }
            case PUT_EXTRA_KEY_MODE_MY_ITEM: {
                mImgBtnFavorite.setVisibility(View.GONE);
                mLinearLayout.setEnabled(false);
                mLinearLayout.setClickable(false);
                break;
            }
            case PUT_EXTRA_KEY_MODE_HISTORY: {
                mImgBtnFavorite.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);
                break;
            }
            default: {
                break;
            }
        }
    }

    //    start tool bar timer if auction have benn started,or show start date
    private void startToolbarTimer() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();

                if (mItem.getEndDate() > time && mItem.getStartDate() < time) {
                    mTxtToolbarDuration.setText(new SimpleDateFormat("HH:mm:ss")
                            .format(mItem.getEndDate() - time));
                } else if (mItem.getStartDate() > System.currentTimeMillis()) {
                    mTxtToolbarDuration.setText(new SimpleDateFormat("MMM/dd 'at' HH:mm")
                            .format(mItem.getStartDate()));
                }

                handler.postDelayed(this, 500);
            }
        };

        runnable.run();
    }

    private boolean isFavorite = true;// temp

    //    check is this item favorite for current user
    private boolean isFavorite() {
        isFavorite = !isFavorite;
        return isFavorite; // TODO is favorite logic
    }

    //    add item to current user's favorite list
    private void addItemToFavorite() {
        FollowAndUnfollow.addRemoveFavorite(mItem);
//        if (isFavorite()) {
//            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
//        } else {
//            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_48dp);
//        }
//        // TODO add favorite logic
    }

    private void createViewPagerDots() {
        for (int i = 0; i < mDotsCount; i++) {
            mImgDots[i] = new ImageView(this);
            mImgDots[i].setImageResource(R.drawable.view_pager_dot);
            mLinearLayoutDots.addView(mImgDots[i]);
        }
        mImgDots[0].setImageResource(R.drawable.view_pager_dot_selected);
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
}
