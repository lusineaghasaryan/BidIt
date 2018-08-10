package com.example.user.bidit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.viewholder.BidItItemsRVViewHolder;
import com.example.user.bidit.models.Item;

import java.text.SimpleDateFormat;
import java.util.List;

public class BidItItemsRVAdapter extends RecyclerView.Adapter<BidItItemsRVViewHolder>{

    private List<Item> mData;

//    click listener
    private BidItItemsRVViewHolder.OnItemCLickListener mOnItemCLickListener =
        new BidItItemsRVViewHolder.OnItemCLickListener() {
        @Override
        public void onItemClick(int adapterPosition) {
            mOnItemSelectedListener.onItemSelected(mData.get(adapterPosition));
        }

        @Override
        public void onFavoriteClick(int adapterPosition) {
            // TODO add favorite button click logic
        }
    };
    private OnItemSelectedListener mOnItemSelectedListener;


    @NonNull
    @Override
    public BidItItemsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BidItItemsRVViewHolder viewHolder = new BidItItemsRVViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_bid_it_item, parent, false));
        viewHolder.setOnItemCLickListener(mOnItemCLickListener); // ???
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BidItItemsRVViewHolder holder, final int position) {

//        get current item
        Item item = mData.get(position);

//      set item fields into holder
        holder.getTxtAuctionTitle().setText(item.getItemTitle());
        holder.getTxtAuctionDate().setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(item.getStartTime()));
        holder.getTxtAuctionCurrentPrice().setText(String.valueOf(item.getCurrentPrice()) + "AMD");


        holder.setOnItemCLickListener(mOnItemCLickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setDataToAdapter(List<Item> data){
        mData = data;
    }


    public interface OnItemSelectedListener{
        void onItemSelected(Item item);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }
}
