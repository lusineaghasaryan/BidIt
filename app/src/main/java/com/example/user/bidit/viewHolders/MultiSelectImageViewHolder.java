package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.user.bidit.R;

public class MultiSelectImageViewHolder extends RecyclerView.ViewHolder {

    public CheckBox checkBox;
    public ImageView imageView;

    public MultiSelectImageViewHolder(View itemView) {
        super(itemView);
        checkBox = (CheckBox) itemView.findViewById(R.id.check_box_item_mutiselect_photo_rv);
        imageView = (ImageView) itemView.findViewById(R.id.image_view_item_mutiselect_photo_rv);
    }
}
