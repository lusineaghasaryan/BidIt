package com.example.user.bidit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.viewHolders.MultiSelectImageViewHolder;

import java.util.ArrayList;

public class MultiSelectImageAdapter extends RecyclerView.Adapter<MultiSelectImageViewHolder> {

    private ArrayList<String> mImagesList;
    private Context mContext;
    private SparseBooleanArray mSparseBooleanArray;

    private ArrayList<String> mSelectedImagesURLList;

    public MultiSelectImageAdapter(Context context, ArrayList<String> imageList) {
        mContext = context;
        mSparseBooleanArray = new SparseBooleanArray();
        mImagesList = new ArrayList<String>();
        mSelectedImagesURLList = new ArrayList<>();
        mImagesList.addAll(imageList);
    }


    @NonNull
    @Override
    public MultiSelectImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_multiphoto_item, parent, false);
        return new MultiSelectImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MultiSelectImageViewHolder holder, int position) {
        final String imageUrl = mImagesList.get(position);

        Glide.with(mContext)
                .load("file://"+imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                } else {
                    holder.checkBox.setChecked(true);
                }
            }
        });
        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(mSparseBooleanArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mSelectedImagesURLList.add(imageUrl);
                } else {
                    removeSelectedFromList(imageUrl);
                    //mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }


    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<mImagesList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(mImagesList.get(i));
            }
        }
        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void removeSelectedFromList(String url){
        mSelectedImagesURLList.remove(url);
    }

    public ArrayList<String> getSelectedItemsList(){
        return mSelectedImagesURLList;
    }
}
