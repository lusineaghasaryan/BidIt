package com.example.user.bidit.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomViewPager implements ViewPager.PageTransformer {
    private int maxTranslateOffsetX;
    private ViewPager mViewPagerHotList;

    public ZoomViewPager(Context context, ViewPager pViewPagerHotList) {
        this.maxTranslateOffsetX = dp2px(context);
        mViewPagerHotList = pViewPagerHotList;
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
