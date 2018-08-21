package com.example.user.bidit.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationHelper;
import com.example.user.bidit.models.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowItemActivity extends AppCompatActivity {

    //    firebase auth helper
    private FireBaseAuthenticationHelper mAuthenticationHelper;

    //    show auction images, fields
    private ViewPager mViewPager;
    private ImageVPAdapter mImageVPAdapter;

    private TextView mTxtAuctionTitle, mTxtAuctionDescription, mTxtAuctionDuration, mTxtAuctionStartDate,
            mTxtAuctionStartPrice, mTxtAuctionCurrentPrice;
    private ImageButton mImgBtnFavorite;

    //    show chat, messages
    private RecyclerView mRecyclerView;
    private RecyclerViewMessageAdapter mMessageAdapter;
    private ArrayList<String> mMessages;
    private EditText mInputMessage;
    private Button mBtnEnterMessage;

    //    data (temp)
    private Item mItem = new Item();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        tempMethod();
        init();
        setFields();
        setListeners();
    }

    //    temp
    private void tempMethod() {
        ArrayList<String> mImages = new ArrayList<>();
        mImages.add("https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fwww.noao.edu%2Fimage_gallery%2Fim" +
                "ages%2Fd4%2FJ1337-29_crop1-1000.jpg&f=1");
        mImages.add("https://images.duckduckgo.com/iu/?u=https%3A%2F%2Fi2.wp.com%2Fbeebom.com%2Fwp-cont" +
                "ent%2Fuploads%2F2016%2F01%2FReverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg%3Fr" +
                "esize%3D640%252C426&f=1");
        mImages.add("https://images.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2016%2" +
                "F06%2F18%2F17%2F42%2Fimage-1465348_960_720.jpg&f=1");
        mImages.add("https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fmedia.springernature.com%2Ffull%2F" +
                "springer-static%2Fimage%2Fart%253A10.1186%252Fs12898-017-0138-8%2FMediaObjects%2F12898_2" +
                "017_138_Fig1_HTML.jpg&f=1");

        mItem.setItemTitle("Title");
        mItem.setItemDescription("Description\nDescription");
        mItem.setCurrentPrice(500);
        mItem.setStartDate(Calendar.getInstance().getTimeInMillis());
        mItem.setDuration(600000);
        mItem.setStartPrice(500f);
        mItem.setPhotoUrls(mImages);
    }

    private void init() {
//        init fire base helper
        mAuthenticationHelper = new FireBaseAuthenticationHelper();

//        find and set viewPager
        mViewPager = findViewById(R.id.view_pager_show_item_activity);
        mImageVPAdapter = new ImageVPAdapter(mItem);
        mViewPager.setAdapter(mImageVPAdapter);

//        find and set recyclerView and components
        mRecyclerView = findViewById(R.id.list_view_show_item_activity_messages);
        mMessages = new ArrayList<>();
        mMessages.add(String.valueOf(mItem.getCurrentPrice()));
        mMessageAdapter = new RecyclerViewMessageAdapter(mMessages);
        setRecyclerSittings();
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

//        check for logged in to disable buttons
        checkForLoggedIn();
    }

    private void setFields() {
        mTxtAuctionTitle.setText(mItem.getItemTitle());
        mTxtAuctionDescription.setText(mItem.getItemDescription());
        mTxtAuctionStartDate.setText(new SimpleDateFormat("MMM/dd 'at' HH:mm")
                .format(mItem.getStartTime()));
        mTxtAuctionDuration.setText(new SimpleDateFormat("dd 'day' HH:mm")
                .format(mItem.getStartTime()));
        mTxtAuctionStartPrice.setText(String.valueOf(mItem.getStartPrice()));
        mTxtAuctionCurrentPrice.setText(String.valueOf(mItem.getCurrentPrice()));

        if (isFavorite()) {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_48dp);
        } else {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
        }// ???


    }

    private void setRecyclerSittings() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMessageAdapter);
    }

    private void setListeners() {
        mImgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToFavorite();
            }
        });

        mBtnEnterMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                // TODO add change mCurrentPrice on firebase, and add check for correct number logic
                String currentMessage = mInputMessage.getText().toString();
                if (!currentMessage.isEmpty()) {
                    mMessages.add(currentMessage);
                    mInputMessage.setText("");
                    mMessageAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mMessages.size() - 1); // ???
                    mRecyclerView.scrollToPosition(mMessages.size() - 1);
                }
            }
        });
    }

    private void checkForLoggedIn() {
        if (FireBaseAuthenticationHelper.isLoggedIn()) {
            mImgBtnFavorite.setEnabled(false);
            mInputMessage.setEnabled(false);
            mBtnEnterMessage.setEnabled(false);
        } else {
            mImgBtnFavorite.setClickable(true);
            mInputMessage.setEnabled(true);
            mBtnEnterMessage.setEnabled(true);
        }
    }


    private boolean isFavorite = true;// temp

    private boolean isFavorite() {
        isFavorite = !isFavorite;
        return isFavorite; // TODO is favorite logic
    }

    private void addItemToFavorite() {
        if (isFavorite()) {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
        } else {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_48dp);
        }
        // TODO add favorite logic
    }


    // ListView adapter
    private class RecyclerViewMessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
        private ArrayList<String> mMessages;

        RecyclerViewMessageAdapter(ArrayList<String> pMessages) {
            mMessages = pMessages;
        }


        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MessageViewHolder messageViewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_bid_it_message_item, parent, false));
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

            mTxtBidItMessage = itemView.findViewById(R.id.txt_bid_it_message);
        }

        public TextView getTxtBidItMessage() {
            return mTxtBidItMessage;
        }
    }

    // ViewPager adapter
    private class ImageVPAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;
        private List<String> mImages;

        ImageVPAdapter(Item pItem) {
            mLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImages = pItem.getPhotoUrls();
        }


        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.view_bid_it_image_item, container, false);

            ImageView imageView = itemView.findViewById(R.id.image_show_item_fragment_pager);
            imageView.setImageURI(Uri.parse(mImages.get(position)));

            container.addView(itemView);

            return itemView;
        }
    }
}
