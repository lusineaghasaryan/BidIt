package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.user.bidit.R;

public class AddItemPhotosViewHolder extends RecyclerView.ViewHolder {

    public ImageView mPotoImageView;
    public Button mRemovePhotoBtn;

    public AddItemPhotosViewHolder(View itemView) {
        super(itemView);
        mPotoImageView = itemView.findViewById(R.id.image_view_item_add_photo_rv);
        mRemovePhotoBtn = itemView.findViewById(R.id.btn_remove_item_add_photo_rv);
    }
}
