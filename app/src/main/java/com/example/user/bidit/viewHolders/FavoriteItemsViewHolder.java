package com.example.user.bidit.viewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class FavoriteItemsViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView, follow;
    private TextView title, date, startPrice;

    public FavoriteItemsViewHolder(View itemView) {
        super(itemView);
        init();
    }

    private void init(){
        imageView = itemView.findViewById(R.id.image_item_image_item_view);
        title = itemView.findViewById(R.id.text_view_title_item_view);
        date = itemView.findViewById(R.id.text_view_start_date_item_view);
        startPrice = itemView.findViewById(R.id.text_view_start_price_item_view);
        follow = itemView.findViewById(R.id.image_view_follow_item_view);

    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getDate() {
        return date;
    }

    public TextView getStartPrice() {
        return startPrice;
    }

    public ImageView getFollow() {
        return follow;
    }
}
