package com.example.user.bidit.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.activities.AddItemActivity;
import com.example.user.bidit.activities.HomeActivity;
import com.example.user.bidit.activities.MyItemsActivity;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.activities.HomeActivity;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewHolders.MyItemsViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;



public class MyItemsAdapter extends RecyclerView.Adapter<MyItemsViewHolder> {
    private List<Item> mMyItemList;
    private Context mContext;
    public IOnItemClick mIOnItemClick;



    public MyItemsAdapter(List<Item> itemList, Context context) {
        mMyItemList = itemList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_my_item, parent, false);
        return new MyItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemsViewHolder holder, final int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM kk:mm");
        Item currentItem = mMyItemList.get(position);

        Glide.with(mContext)
                .load(currentItem.getPhotoUrls().get(0))
                .into(holder.getImageView());

        holder.getTitle().setText(currentItem.getItemTitle());
        holder.getDate().setText(dateFormat.format(currentItem.getStartDate()));
        holder.getFollowersCount().setText(String.valueOf(currentItem.getFollowersCount()));
        holder.getStartPrice().setText(String.valueOf(currentItem.getStartPrice()));

//        MyItemsViewHolder.OnViewHolderItemClickListener mOnViewHolderItemClickListener =
//                new MyItemsViewHolder.OnViewHolderItemClickListener() {
//                    @Override
//                    public void onClick(int pPosition) {
//                        mIOnItemClick.onItemClick(mItemList.get(pPosition), pPosition);
//                    }
//                };

        holder.setOnViewHolderItemClickListener(new MyItemsViewHolder.OnViewHolderItemClickListener() {
            @Override
            public void onClick(int pPosition) {
                mIOnItemClick.onItemClick(mMyItemList.get(pPosition), pPosition);
            }
        });
        //mIOnItemClick.onItemClick(currentItem, position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /*Intent intent;
//                if (currentItem.getStartDate() > System.currentTimeMillis()) {
//                    intent = new Intent(mContext, AddItemActivity.class);
//                } else {
//                    intent = new Intent(mContext, ShowItemActivity.class);
//                }
//                intent.putExtra(HomeActivity.PUT_EXTRA_KEY, currentItem);
//                mContext.startActivity(intent);*/
//            }
//        });

        holder.getStartPrice().setText(String.valueOf((int)currentItem.getStartPrice()) + "$");
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext,ShowItemActivity.class);
//                intent.putExtra(ShowItemActivity.PUT_EXTRA_KEY_MODE_MY_ITEM, mMyItemList.get(position));
//                mContext.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mMyItemList.size();
    }

    public void setIOnItemClick(IOnItemClick pIOnItemClick) {
        mIOnItemClick = pIOnItemClick;
    }

    public interface IOnItemClick{
        void onItemClick(Item pItem, int pPosition);
    }
}
