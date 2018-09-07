package com.example.user.bidit.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.ImageViewPagerAdapter;
import com.example.user.bidit.adapters.ZoomViewPagerAdapter;
import com.example.user.bidit.models.Item;

public class ImageDialog extends Dialog {

    private Context mContext;

    private ViewPager mViewPager;
    private ZoomViewPagerAdapter mZoomViewPagerAdapter;
    private int mImagePosition;

    private LinearLayout mLinearLayoutDots;
    private int mDotsCount;
    private ImageView[] mImgDots;

    private Item mItem;

    public ImageDialog(@NonNull Context context, Item pItem, int pImagePosition) {
        super(context);
        mContext = context;
        mItem = pItem;
        mImagePosition = pImagePosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);

        init();
        setListeners();
    }

    private void createViewPagerDots() {
        for (int i = 0; i < mDotsCount; i++) {
            mImgDots[i] = new ImageView(mContext);
            mImgDots[i].setImageResource(R.drawable.view_pager_dot);
            mLinearLayoutDots.addView(mImgDots[i]);
        }
        mImgDots[0].setImageResource(R.drawable.view_pager_dot_selected);
    }

    private void init(){
        mViewPager = findViewById(R.id.view_pager_image_dialog);

        mZoomViewPagerAdapter = new ZoomViewPagerAdapter(mContext, mItem);
        mViewPager.setAdapter(mZoomViewPagerAdapter);
        mViewPager.setCurrentItem(mImagePosition);

        mLinearLayoutDots = findViewById(R.id.linear_image_dialog_count_dots);
        mDotsCount = mZoomViewPagerAdapter.getCount();
        mImgDots = new ImageView[mDotsCount];
        createViewPagerDots();
    }

    private void setListeners(){
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
}
