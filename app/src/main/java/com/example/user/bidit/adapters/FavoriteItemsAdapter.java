package com.example.user.bidit.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.FollowAndUnfollow;
import com.example.user.bidit.viewHolders.FavoriteItemsViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoriteItemsAdapter extends RecyclerView.Adapter<FavoriteItemsViewHolder> {

    private List<Item> mFavoriteItemsList = new ArrayList<>();
    private Context mContext;

    public FavoriteItemsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public FavoriteItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new FavoriteItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteItemsViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        final Item currentItem = mFavoriteItemsList.get(position);

        if (FollowAndUnfollow.isFollowed(currentItem)) {
            holder.getFollow().setImageResource(R.drawable.star_filled);
        } else {
            holder.getFollow().setImageResource(R.drawable.ic_nav_favorite);
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
                if (FollowAndUnfollow.isFollowed(currentItem)) {
                    FollowAndUnfollow.removeFromFavorite(currentItem);
                    holder.getFollow().setImageResource(R.drawable.ic_nav_favorite);
                } else {
                    FollowAndUnfollow.addToFavorite(currentItem);
                    holder.getFollow().setImageResource(R.drawable.star_filled);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFavoriteItemsList.size();
    }

    public void setFavoriteItemsList(List<Item> favoriteItemsList) {
        mFavoriteItemsList.clear();
        mFavoriteItemsList.addAll(favoriteItemsList);
    }
}