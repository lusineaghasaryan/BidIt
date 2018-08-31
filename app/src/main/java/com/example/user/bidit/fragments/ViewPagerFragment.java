//package com.example.user.bidit.fragments;
//
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.user.bidit.R;
//
//public class ViewPagerFragment extends Fragment {
//
//    private static final int PAGER_FRAGMENT_COUNT = 2;
//
//    private ViewPager mViewPager;
//    private FragmentVPAdapter mAdapter;
//    private TabLayout mTabLayout;
//
//    public ViewPagerFragment() {
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_view_pager, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        init(view);
//        setViewPagerAdapter();
//        addTabs();
//        setListeners();
//    }
//
//
////    initialize fields
//    private void init(View view){
//        mViewPager = view.findViewById(R.id.view_pager_fragment);
//        mAdapter = new FragmentVPAdapter(getFragmentManager());
//        mTabLayout = view.findViewById(R.id.tab_layout_fragment);
//    }
//
//    private void setViewPagerAdapter(){
//        mViewPager.setAdapter(mAdapter);
//    }
//
//    private void setListeners(){
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
//
//        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }
//
////    add FragmentViewPager tabs
//    private void addTabs(){
//        mTabLayout.addTab(mTabLayout.newTab().setText("ALL"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("HOT"));
//        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//    }
//
//
////    adapter class
//    private class FragmentVPAdapter extends FragmentPagerAdapter{
//
//        FragmentVPAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            Fragment fragment = null;
//            switch (position){
//                case 0:{
//                    fragment = ChildAllItemFragment.newInstance();
//                    break;
//                }
//                case 1:{
//                    fragment = ChildHotItemFragment.newInstance();
//                    break;
//                }
//                default:{
//                    break;
//                }
//            }
//            return fragment;
//        }
//
//        @Override
//        public int getCount() {
//            return PAGER_FRAGMENT_COUNT;
//        }
//    }
//}
