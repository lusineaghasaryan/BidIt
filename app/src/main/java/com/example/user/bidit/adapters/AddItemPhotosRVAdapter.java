package com.example.user.bidit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.bidit.R;
import com.example.user.bidit.viewHolders.AddItemPhotosViewHolder;

import java.util.ArrayList;

public class AddItemPhotosRVAdapter extends RecyclerView.Adapter<AddItemPhotosViewHolder> {

    public Context mContext;
    public ArrayList<String> mPhotosList;

    public AddItemPhotosRVAdapter(Context mContext, ArrayList<String> photosList) {
        this.mContext = mContext;
        mPhotosList = new ArrayList<>();
        mPhotosList.addAll(photosList);
    }


    @NonNull
    @Override
    public AddItemPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_add_photo_recycler_view, parent, false);
        return new AddItemPhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddItemPhotosViewHolder holder, int position) {

        //downloadImageAsync.execute(IMAGE_URL, filesDir);
    }

    @Override
    public int getItemCount() {
        return mPhotosList.size();
    }
}
