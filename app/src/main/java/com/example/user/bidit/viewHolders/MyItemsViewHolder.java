package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class MyItemsViewHolder extends RecyclerView.ViewHolder{
    private ImageView imageView;
    private TextView title, date, duration, startPrice, followersCount;

    public MyItemsViewHolder(View itemView) {
        super(itemView);
        init();
    }

    private void init(){
        imageView = itemView.findViewById(R.id.image_user_image_my_item_view);
        title = itemView.findViewById(R.id.text_title_view_my_item);
        date = itemView.findViewById(R.id.text_start_date_my_item_view);
        duration = itemView.findViewById(R.id.text_duration_my_item_view);
        startPrice = itemView.findViewById(R.id.text_start_price_my_item_view);
        followersCount = itemView.findViewById(R.id.text_followers_count_view_my_item);

    }

    public TextView getStartPrice() {
        return startPrice;
    }

    public TextView getFollowersCount() {
        return followersCount;
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
}
