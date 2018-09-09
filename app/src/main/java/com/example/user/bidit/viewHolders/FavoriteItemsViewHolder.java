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
        imageView = itemView.findViewById(R.id.img_recycler_item_auction_image);
        title = itemView.findViewById(R.id.txt_recycler_item_auction_title);
        date = itemView.findViewById(R.id.txt_recycler_item_auction_start_date);
//        duration = itemView.findViewById(R.id.text_view_end_date_favorite_view);
        startPrice = itemView.findViewById(R.id.txt_recycler_item_auction_start_price);
        follow = itemView.findViewById(R.id.img_recycler_item_auction_follow);

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
