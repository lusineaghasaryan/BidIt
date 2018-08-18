package com.example.user.bidit.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.bidit.R;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ShowItemFragment extends Fragment {

    //    show auction images, fields
    private ViewPager mViewPager;
    private ImageVPAdapter mImageVPAdapter;

    private TextView mTxtAuctionTitle, mTxtAuctionDescription, mTxtAuctionDuration;
    private ImageButton mImgBtnFavorite;

    //    show chat, messages
    private ListView mListView;
    private ListViewMessageAdapter mMessageAdapter;
    private ArrayList<String> mMessages;
    private EditText mInputMessage;
    private Button mBtnEnterMessage;

    //    data
    private Item mItem = new Item();

    public ShowItemFragment() {
        // temp
        mItem.setItemTitle("Title");
        mItem.setItemDescription("Description\nDescription\nDescription");
        mItem.setCurrentPrice(500);
    }

    public static ShowItemFragment newInstance(Item pItem) {
        ShowItemFragment showItemFragment = new ShowItemFragment();
        Bundle args = new Bundle();

        showItemFragment.mItem = pItem;

        return showItemFragment;
    } // ???

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setFields();
        setListeners();
    }

    private void init(View pView) {
//        find and set viewPager
        mViewPager = pView.findViewById(R.id.view_pager_show_item_fragment);
        mImageVPAdapter = new ImageVPAdapter(mItem);
        mViewPager.setAdapter(mImageVPAdapter);

//        find and set listView and components
        mListView = pView.findViewById(R.id.list_view_show_item_fragment_messages);
        mListView.setTextFilterEnabled(true);
        mMessages = new ArrayList<>();
        mMessages.add(String.valueOf(mItem.getCurrentPrice()));
        mMessageAdapter = new ListViewMessageAdapter(getContext(), mMessages);
        mInputMessage = pView.findViewById(R.id.input_show_item_fragment_message);
        mBtnEnterMessage = pView.findViewById(R.id.btn_show_item_fragment_enter_message);

//        find views
        mTxtAuctionTitle = pView.findViewById(R.id.txt_show_item_fragment_auction_title);
        mTxtAuctionDescription = pView.findViewById(R.id.txt_show_item_fragment_auction_description);
        mImgBtnFavorite = pView.findViewById(R.id.img_btn_show_item_fragment_favorite_btn);

    }

    private void setFields() {
        mTxtAuctionTitle.setText(mItem.getItemTitle());
        mTxtAuctionDescription.setText(mItem.getItemDescription());

        if (isFavorite()) {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_48dp);
        } else {
            mImgBtnFavorite.setImageResource(R.drawable.favorite_star_border_48dp);
        }// ???

        mListView.setAdapter(mMessageAdapter);
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
                }
            }
        });
    }

    private boolean isFavorite = true;
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
    private class ListViewMessageAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private ArrayList<String> mStrings;

        ListViewMessageAdapter(Context pContext, ArrayList<String> pValues) {
            super(pContext, R.layout.view_bid_it_message_item, pValues);
            mContext = pContext;
            mStrings = pValues;
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            mListView.setSelection(mMessageAdapter.getCount() - 1);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.view_bid_it_message_item, parent, false);

            TextView textView = view.findViewById(R.id.txt_bid_it_message);
            textView.setText(mStrings.get(position));

            return view;
        }
    }

    // ViewPager adapter
    private class ImageVPAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;
        private List<String> mImages;

        ImageVPAdapter(Item pItem) {
            mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
