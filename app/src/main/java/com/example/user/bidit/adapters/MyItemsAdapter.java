package com.example.user.bidit.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.activities.MyItemsActivity;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewHolders.MyItemsViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyItemsAdapter extends RecyclerView.Adapter<MyItemsViewHolder> {
    private List<Item> mItemList;
    private Context mContext;

    public MyItemsAdapter(List<Item> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_my_item, parent, false);
        return new MyItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemsViewHolder holder, int position) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyy  kk:mm");
        Item currentItem = mItemList.get(position);

        Glide.with(mContext)
                .load(currentItem.getPhotoUrls().get(0))
                .into(holder.getImageView());

        holder.getTitle().setText(currentItem.getItemTitle());
        holder.getDate().setText(mDateFormat.format(currentItem.getStartDate()));
        holder.getDuration().setText(mDateFormat.format(currentItem.getEndDate()));
        holder.getFollowersCount().setText(String.valueOf(currentItem.getFollowersCount()));
        holder.getStartPrice().setText(String.valueOf(currentItem.getStartPrice()));

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
