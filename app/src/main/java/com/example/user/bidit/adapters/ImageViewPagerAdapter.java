package com.example.user.bidit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.models.Item;

import java.util.List;

public class ImageViewPagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    private List<String> mImages;

    private Context mContext;

    private OnImageClickListener mOnImageClickListener;

    public ImageViewPagerAdapter(Context pContext, Item pItem) {
        mContext = pContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_vp_image_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.img_view_pager);

        Glide.with(mContext)
                .load(mImages.get(position))
                .into(imageView);

        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mOnImageClickListener.onImageClick(position);
            }
        });

        return itemView;
    }


    public interface OnImageClickListener{
        void onImageClick(int pPosition);
    }

    public void setOnImageClickListener(OnImageClickListener pOnImageClickListener) {
        mOnImageClickListener = pOnImageClickListener;
    }
}
