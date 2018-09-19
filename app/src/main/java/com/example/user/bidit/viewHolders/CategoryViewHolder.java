package com.example.user.bidit.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.user.bidit.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private TextView mTxtCategory;
    private CategoryViewHolder.OnCategoryItemClickListener mClickListener;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        mTxtCategory = itemView.findViewById(R.id.txt_category_view_name);

        setListeners(itemView);
    }

    public TextView getTxtCategory() {
        return mTxtCategory;
    }

    private void setListeners(View pV) {
        pV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                mClickListener.OnCategoryClick(getAdapterPosition());
            }
        });
    }

    // delegation
    public interface OnCategoryItemClickListener {
        void OnCategoryClick(int pAdapterPosition);
    }

    public void setClickListener(CategoryViewHolder.OnCategoryItemClickListener pClickListener) {
        mClickListener = pClickListener;
    }
}
