package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.fragments.HomeListFragment;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.User;
import com.example.user.bidit.viewHolders.CategoryViewHolder;
import com.example.user.bidit.viewModels.CategoryListViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "asd";

    private User mUser;

    //    recyclers, viewPager and adapters
    private RecyclerView mRecyclerViewCategories;
    private CategoryAdapter mCategoryAdapter;

    //    data
    private List<Category> mCategoryData;
    private String mCurrentCategoryId;

    //    search view in toolbar
    private FloatingSearchView mSearchView;

    //navDrawer
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;

    private View mHeaderView;
    private TextView mHeaderUserName;
    private ImageView mHeaderAvatar;

    private boolean isInHome = true;

    private HomeListFragment mHomeListFragment;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        setNavigationDrawer();
        loadCategoryList();
        setListeners();
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
            FireBaseAuthenticationManager.getInstance().initCurrentUser(new FireBaseAuthenticationManager.LoginListener() {
                @Override
                public void onResponse(boolean pSuccess, User pUser) {
                    mUser = FireBaseAuthenticationManager.getInstance().getCurrentUser();
                    setSettingsInHeader(mUser);
                }
            });
        } else {
            setSettingsInHeader(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternet(mDrawer);
        if (mHomeListFragment != null) {
            mHomeListFragment.notifyRecyclerAndViewPager();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        if (!isInHome) {
            mSearchView.setLeftMenuOpen(false);
            mHomeListFragment.loadHomePage();
            isInHome = true;
        } else {
            finish();
        }
    }

    //    initialize fields
    private void init() {
        mCategoryData = new ArrayList<>();
        mCurrentCategoryId = null;
        mRecyclerViewCategories = findViewById(R.id.recycler_view_home_activity_categories);
        mCategoryAdapter = new CategoryAdapter(mCategoryData);
        setCategoryRecyclerSittings();

        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);
        mHeaderUserName = mHeaderView.findViewById(R.id.text_view_name_nav_header);
        mHeaderAvatar = mHeaderView.findViewById(R.id.circle_image_avatar_nav_header);

        mSearchView = findViewById(R.id.search_view_home_activity);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawer);

        mHomeListFragment = new HomeListFragment();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction
                .add(R.id.home_fragment_container, mHomeListFragment)
                .commit();
    }

    private void setListeners() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                mHomeListFragment.loadSearchList(newQuery, mCurrentCategoryId);
                isInHome = false;
            }
        });

        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                mDrawer.openDrawer(mNavigationView);
                updateNavigationDrawer();
            }

            @Override
            public void onMenuClosed() {
                if (!mDrawer.isDrawerOpen(mNavigationView)) {
                    Log.d(TAG, "onMenuClosed: ");
                    mHomeListFragment.loadHomePage();
                    isInHome = true;
                }
            }
        });
    }

    private void setCategoryRecyclerSittings() {
        mRecyclerViewCategories.setHasFixedSize(true);
        mRecyclerViewCategories.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCategories.setAdapter(mCategoryAdapter);
    }

    private void loadCategoryList() {
        final CategoryListViewModel categoryListViewModel =
                ViewModelProviders.of(this).get(CategoryListViewModel.class);
        categoryListViewModel.getCategoryList().removeObservers(this);
        categoryListViewModel.getCategoryList().observe(this, new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Category> pCategories) {
                mCategoryData.addAll(pCategories);
                mCategoryAdapter.notifyDataSetChanged();
                categoryListViewModel.getCategoryList().removeObservers(HomeActivity.this);
            }
        });
        categoryListViewModel.updateData();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        updateNavigationDrawer();
        switch (item.getItemId()) {
            case R.id.nav_item_my_account: {
                startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
                break;
            }
            case R.id.nav_item_add: {
                Intent addItemIntent =
                        new Intent(HomeActivity.this, AddItemActivity.class);
                startActivity(addItemIntent);
                break;
            }
            case R.id.nav_item_balance: {
                break;
            }
            case R.id.nav_item_history: {
                break;
            }
            case R.id.nav_item_favorite: {
                startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                break;
            }
            case R.id.nav_item_my_items: {
                startActivity(new Intent(HomeActivity.this, MyItemsActivity.class));
                break;
            }
            case R.id.nav_item_log_out: {
                FireBaseAuthenticationManager.getInstance().signOut();
                setSettingsInHeader(null);
                mHomeListFragment.notifyRecyclerAndViewPager();
                break;
            }
            case R.id.nav_item_log_in: {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                break;
            }
            case R.id.nav_item_help: {
                break;
            }
            case R.id.nav_item_about_us:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationDrawer() {
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mToggle.setDrawerIndicatorEnabled(false);
    }

    public void updateNavigationDrawer() {
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn()) {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, true);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(false);
        } else {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, false);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(true);
        }
    }

    private void setSettingsInHeader(User pUser) {
        String name;
        String photoUrl;
        if (pUser == null) {
            name = "";
            photoUrl = RegistrationActivity.DEFAULT_PHOTO_URL;
        } else {
            name = mUser.getName();
            photoUrl = mUser.getPhotoUrl();
        }
        mHeaderUserName.setText(name);
        Glide.with(HomeActivity.this)
                .load(photoUrl)
                .into(mHeaderAvatar);
    }


    //    category list recyclerView adapter
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<Category> mCategories;

        // category item click listener
        private CategoryViewHolder.OnCategoryItemClickListener mClickListener =
                new CategoryViewHolder.OnCategoryItemClickListener() {
                    @Override
                    public void OnCategoryClick(int pAdapterPosition) {
                        // hide hot items when click on category
                        mHomeListFragment.loadItemsByCategory(
                                mCategories.get(pAdapterPosition).getCategoryId());
                        mCurrentCategoryId = mCategories.get(pAdapterPosition).getCategoryId();
                        isInHome = false;
                        mSearchView.setLeftMenuOpen(true);
//                        mSearchView.clearQuery();
                    }
                };

        CategoryAdapter(List<Category> pCategories) {
            mCategories = pCategories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CategoryViewHolder categoryViewHolder =
                    new CategoryViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_category_item, parent, false));

            setListener(categoryViewHolder);

            return categoryViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.getTxtCategory().setText(mCategories.get(position).getCategoryTitle());
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }

        private void setListener(CategoryViewHolder pCategoryViewHolder) {
            pCategoryViewHolder.setClickListener(mClickListener);
        }
    }
}
