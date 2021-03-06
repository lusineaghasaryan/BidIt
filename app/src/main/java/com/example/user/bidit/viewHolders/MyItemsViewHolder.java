package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class MyItemsViewHolder extends RecyclerView.ViewHolder{
    private ImageView imageView;
    private TextView title, date, startPrice, followersCount;
    private OnViewHolderItemClickListener mOnViewHolderItemClickListener;
    private Button removeItem;

    public MyItemsViewHolder(View itemView) {
        super(itemView);
        init();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnViewHolderItemClickListener.onClick(getAdapterPosition());
            }
        });
    }

    private void init(){
        removeItem = itemView.findViewById(R.id.btn_remove_my_item_view);
        imageView = itemView.findViewById(R.id.image_item_image_my_item_view);
        title = itemView.findViewById(R.id.text_title_view_my_item);
        date = itemView.findViewById(R.id.text_start_date_my_item_view);
        startPrice = itemView.findViewById(R.id.text_start_price_my_item_view);
        followersCount = itemView.findViewById(R.id.text_followers_count_view_my_item);


    }

    public Button getRemoveItem() {
        return removeItem;
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

    public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener pOnViewHolderItemClickListener) {
        mOnViewHolderItemClickListener = pOnViewHolderItemClickListener;
    }

    public interface OnViewHolderItemClickListener{
        void onClick(int pPosition);
    }
}
