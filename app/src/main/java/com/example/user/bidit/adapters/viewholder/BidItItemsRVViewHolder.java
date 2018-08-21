package com.example.user.bidit.adapters.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bidit.R;

public class BidItItemsRVViewHolder extends RecyclerView.ViewHolder {

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_btn_view_holder_favorite_btn:{
                    mOnItemCLickListener.onFavoriteClick(getAdapterPosition());
                }
                default:{
                    mOnItemCLickListener.onItemClick(getAdapterPosition());
                }
            }
        }
    };
    private OnItemCLickListener mOnItemCLickListener;

//    view holder item fields
    private TextView mTxtAuctionTitle, mTxtAuctionDate, mTxtAuctionCurrentPrice;
    private ImageView mImgAuctionImage, mImgAuctionStatus;
    private ImageButton mImgBtnFavorite;

    public BidItItemsRVViewHolder(View itemView) {
        super(itemView);

//        initialize fields
        mTxtAuctionTitle = itemView.findViewById(R.id.txt_view_holder_auction_title);
        mTxtAuctionDate = itemView.findViewById(R.id.txt_view_holder_auction_date);
        mTxtAuctionCurrentPrice = itemView.findViewById(R.id.txt_view_holder_auction_current_price);
        mImgAuctionImage = itemView.findViewById(R.id.img_view_holder_auction_image);
        mImgAuctionStatus = itemView.findViewById(R.id.img_view_holder_auction_status);
        mImgBtnFavorite = itemView.findViewById(R.id.img_btn_view_holder_favorite_btn);

//        set click listener on item, and favorite button
        itemView.setOnClickListener(mOnClickListener);
        itemView.findViewById(R.id.img_btn_view_holder_favorite_btn).setOnClickListener(mOnClickListener);
    }

    public TextView getTxtAuctionTitle() {
        return mTxtAuctionTitle;
    }

    public TextView getTxtAuctionDate() {
        return mTxtAuctionDate;
    }

    public TextView getTxtAuctionCurrentPrice() {
        return mTxtAuctionCurrentPrice;
    }

    public ImageView getImgAuctionImage() {
        return mImgAuctionImage;
    }

    public ImageView getImgAuctionStatus() {
        return mImgAuctionStatus;
    }

    public ImageButton getImgBtnFavorite() {
        return mImgBtnFavorite;
    }


//    click listener
    public interface OnItemCLickListener{
        void onItemClick(int adapterPosition);
        void onFavoriteClick(int adapterPosition);
    }

    public void setOnItemCLickListener(OnItemCLickListener onItemCLickListener){
        mOnItemCLickListener = onItemCLickListener;
    }
}
