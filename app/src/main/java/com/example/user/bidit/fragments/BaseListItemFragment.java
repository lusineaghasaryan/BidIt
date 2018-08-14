package com.example.user.bidit.fragments;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.BidItItemsRVAdapter;
import com.example.user.bidit.models.Item;

import java.util.List;

public abstract class BaseListItemFragment extends Fragment {

//    recyclerView fields
    private List<Item> mData;
    private BidItItemsRVAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

//    pagination fields
    private boolean loading = false;
    private boolean isLastPage = false; // ???


    private OnFragmentInteractionListener mListener;
    private BidItItemsRVAdapter.OnItemSelectedListener mOnItemSelectedListener =
            new BidItItemsRVAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Item item) {
            // TODO open show item fragment
        }

        @Override
        public void onItemAddToFavorite() {
            // TODO add to favorite
        }
    };


    public BaseListItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view){
        getDataFromFB();

        mAdapter = new BidItItemsRVAdapter();
        mAdapter.setDataToAdapter(mData);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.recycler_view_list_item_fragment);

        setRecyclerSittings();
        mAdapter.setOnItemSelectedListener(mOnItemSelectedListener);

    }

    private void setRecyclerSittings(){
//        pagination
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == mAdapter.getItemCount() - 1){
                    if (!loading && !isLastPage){
                        loading = true;
                        getDataFromFB();
                        mAdapter.notifyDataSetChanged();
                        loading = false;
                    }
                }
            }
        });

        mRecyclerView.addItemDecoration(new ItemOffSetDecoration(10));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

//    child will load data from server
    protected abstract void getDataFromFB();

    protected void setData(List<Item> data) {
        mData = data;
    }

    protected List<Item> getData() {
        return mData;
    }


    //    add padding to recycler items
    private class ItemOffSetDecoration extends RecyclerView.ItemDecoration{
        private int offSet;

        private ItemOffSetDecoration(int offSet) {
            this.offSet = offSet;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = offSet;
            outRect.left = offSet;
            outRect.top = offSet;
            outRect.bottom = offSet;
        }
    }

//    delegation
    public interface OnFragmentInteractionListener{
        void onShowItem();
    }

    public void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }
}
