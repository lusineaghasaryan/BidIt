package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class AllListViewHolder extends RecyclerView.ViewHolder {
    //        view holder item fields
    private TextView mTxtAuctionTitle, mTxtAuctionStartDate, mTxtAuctionStartPrice;
    private ImageView mImgAuctionImage;
    private ImageView mImgFavorite;

    private AllListViewHolder.OnAllItemClickListener mClickListener;

    public AllListViewHolder(View itemView) {
        super(itemView);

        //        initialize fields
        mTxtAuctionTitle = itemView.findViewById(R.id.text_view_title_item_view);
        mTxtAuctionStartPrice = itemView.findViewById(R.id.text_view_start_price_item_view);
        mTxtAuctionStartDate = itemView.findViewById(R.id.text_view_start_date_item_view);
        mImgAuctionImage = itemView.findViewById(R.id.image_item_image_item_view);
        mImgFavorite = itemView.findViewById(R.id.image_view_follow_item_view);

        setListeners(itemView);
    }

    public TextView getTxtAuctionTitle() {
        return mTxtAuctionTitle;
    }

    public TextView getTxtAuctionStartDate() {
        return mTxtAuctionStartDate;
    }

    public TextView getTxtAuctionStartPrice() {
        return mTxtAuctionStartPrice;
    }

    public ImageView getImgAuctionImage() {
        return mImgAuctionImage;
    }

    public ImageView getImgFavorite() {
        return mImgFavorite;
    }

    private void setListeners(View pV) {
        pV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mClickListener.onAllItemClick(getAdapterPosition());
            }
        });

        mImgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mClickListener.onAllFavoriteClick(getAdapterPosition(), mImgFavorite);
            }
        });
    }

    public interface OnAllItemClickListener {
        void onAllItemClick(int pAdapterPosition);

        void onAllFavoriteClick(int pAdapterPosition, ImageView pFavoriteView);
    }

    public void setClickListener(AllListViewHolder.OnAllItemClickListener pClickListener) {
        mClickListener = pClickListener;
    }
}
