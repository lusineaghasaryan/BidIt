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
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewHolders.FavoriteItemsViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoriteItemsAdapter extends RecyclerView.Adapter<FavoriteItemsViewHolder> {

    private List<Item> mFavoriteItemsList;
    private Context mContext;
    private List<Item> mUnFollowedItemsList = new ArrayList<>();

    public FavoriteItemsAdapter(List<Item> favoriteItemsList, Context context) {
        mFavoriteItemsList = favoriteItemsList;
        mContext = context;
    }

    @NonNull
    @Override
    public FavoriteItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_favorite_item, parent, false);
        return new FavoriteItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteItemsViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        final Item currentItem = mFavoriteItemsList.get(position);

        if (mUnFollowedItemsList.contains(currentItem)) {
            holder.getFollow().setImageResource(R.drawable.favorite_star_24dp);
        }else {
            holder.getFollow().setImageResource(R.drawable.favorite_star_border_24dp);
        }

        Glide.with(mContext)
                .load(currentItem.getPhotoUrls().get(0))
                .into(holder.getImageView());

        holder.getStartPrice().setText(String.valueOf(currentItem.getStartPrice()));
        holder.getTitle().setText(currentItem.getItemTitle());
        holder.getDate().setText(dateFormat.format(currentItem.getStartDate()));

        holder.getFollow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!currentItem.get) {
//                    holder.getStar().setImageResource(R.drawable.favorite_star_24dp);
//                    mUnFollowedItemsList.add(currentItem);
//                } else {
//                    holder.getStar().setImageResource(R.drawable.favorite_star_border_24dp);
//                    if (mUnFollowedItemsList.contains(currentItem)) {
//                        mUnFollowedItemsList.remove(currentItem);
//                    }
//                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFavoriteItemsList.size();
    }
}