package com.example.user.bidit.viewHolders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class FavoriteItemsViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView, follow;
    private TextView title, date, duration, startPrice;

    public FavoriteItemsViewHolder(View itemView) {
        super(itemView);
        init();
    }

    private void init(){
        imageView = itemView.findViewById(R.id.image_item_image_favorite_view);
        title = itemView.findViewById(R.id.text_view_title_favorite_view);
        date = itemView.findViewById(R.id.text_view_start_date_favorite_view);
//        duration = itemView.findViewById(R.id.text_view_end_date_favorite_view);
        startPrice = itemView.findViewById(R.id.text_view_start_price_favorite_view);
        follow = itemView.findViewById(R.id.image_view_follow_favorite_view);

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

    public TextView getDuration() {
        return duration;
    }

    public TextView getStartPrice() {
        return startPrice;
    }

    public ImageView getFollow() {
        return follow;
    }
}
