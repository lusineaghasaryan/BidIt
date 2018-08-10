package com.example.user.bidit.fragments;


import android.annotation.SuppressLint;
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
import com.example.user.bidit.adapters.viewholder.BidItItemsRVViewHolder;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListItemFragment extends Fragment {

    public static final int ALL_ITEMS_DISPLAY_MODE = 0;
    public static final int HOT_ITEMS_DISPLAY_MODE = 1;

//    what kind of list need to display
    private int mMode;

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
    };


    public ListItemFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment") // ??
    private ListItemFragment(int mode) {
        mMode = mode;
    }


    public static ListItemFragment newInstance(int mode){
        ListItemFragment listItemFragment = new ListItemFragment(mode);
        Bundle args = new Bundle();
        return listItemFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        mData = new ArrayList<>(); // temp
        getDataFromFB();

        mAdapter = new BidItItemsRVAdapter();
        mAdapter.setDataToAdapter(mData);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.recycler_view_list_item_fragment);

        setRecyclerSittings();

    }

//    loading data from server
    private void getDataFromFB(){
        switch (mMode){
            case ALL_ITEMS_DISPLAY_MODE:{
                //TODO get all list from firebase or local db
            }
            case HOT_ITEMS_DISPLAY_MODE:{
                //TODO get hot list from firebase or local db
            }
            default:{
                break;
            }
        }

//        temp block
        for (int i = 0; i < 10; i++){
            Item item = new Item();
            item.setItemTitle("title" + i);
            item.setStartTime(Calendar.getInstance().getTimeInMillis() + (i * 10000));
            item.setCurrentPrice(i * 1000f);

            mData.add(item);
        }
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


//    add padding to recycler items
    public class ItemOffSetDecoration extends RecyclerView.ItemDecoration{
        private int offSet;

        public ItemOffSetDecoration(int offSet) {
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

    // ???
    public interface OnFragmentInteractionListener{
        void onShowItem();
    }

    public void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }
}
