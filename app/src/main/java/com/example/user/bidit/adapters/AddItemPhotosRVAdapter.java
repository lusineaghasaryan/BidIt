package com.example.user.bidit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.viewHolders.AddItemPhotosViewHolder;

import java.util.ArrayList;

public class AddItemPhotosRVAdapter extends RecyclerView.Adapter<AddItemPhotosViewHolder> {

    public Context mContext;
    public ArrayList<String> mPhotosList;

    public IOnAddPhotoListener mIOnAddPhotoListener;

    public AddItemPhotosRVAdapter(Context mContext, ArrayList<String> photosList) {
        this.mContext = mContext;
        mPhotosList = new ArrayList<>();
        mPhotosList = photosList;
        mPhotosList.add("drawable://" + R.drawable.favorite_star_24dp);
    }

    @NonNull
    @Override
    public AddItemPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_photo_recycler_view, parent, false);
        return new AddItemPhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddItemPhotosViewHolder holder, final int position) {

        final String imageUrl = mPhotosList.get(position);
        Log.v("IMAGE", "IMAGEURL = " + imageUrl);

        if (imageUrl.contains("https://firebasestorage.googleapis.com") || imageUrl.equals("drawable:")){
            Glide.with(mContext)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.pic_trans)
                    .into(holder.mPotoImageView);
        }
        else {
            Glide.with(mContext)
                    .load("file://" + imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.pic_trans)
                    .into(holder.mPotoImageView);

            if (!imageUrl.contains("drawable://"))
                mIOnAddPhotoListener.addPhoto(imageUrl);
        }



        holder.mRemovePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIOnAddPhotoListener.removePhoto(position);
            }
        });

        if(position == getItemCount()-1){
            holder.mRemovePhotoBtn.setVisibility(View.GONE);
            holder.mPotoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIOnAddPhotoListener.openGallery();
                }
            });
        }
        else
        {
            holder.mRemovePhotoBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mPhotosList.size();
    }

    public void setIOnAddPhotoListener(IOnAddPhotoListener pIOnAddPhotoListener) {
        mIOnAddPhotoListener = pIOnAddPhotoListener;
    }

    public interface IOnAddPhotoListener{
        public void addPhoto(String pImageUrl);
        public void removePhoto(int pPosition);
        public void openGallery();
    }
}
