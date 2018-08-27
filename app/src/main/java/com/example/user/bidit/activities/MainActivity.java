package com.example.user.bidit.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.fragments.AddItemFragment;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setNavigationDrawer();
        initListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FireBaseAuthenticationManager.getInstance().isLoggedIn())
            FireBaseAuthenticationManager.getInstance().initCurrentUser();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_my_account:{
                startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
                break;
            }
            case R.id.nav_item_balance:{
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_item_history:{
                break;
            }
            case R.id.nav_item_favorite:{
                break;
            }
            case R.id.nav_item_my_items:{
                break;
            }
            case R.id.nav_item_log_out:{
                FireBaseAuthenticationManager.getInstance().signOut();
                break;
            }
            case R.id.nav_item_log_in:{
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            }
            case R.id.nav_item_help:{
            }
            case R.id.nav_item_about_us:
                Intent addItemIntent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(addItemIntent);
                //replaceFragment(new AddItemFragment());
            }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
    }

    private void initListeners() {
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
                updateNavigationDrawer();
            }
        });
    }

    private void setNavigationDrawer() {
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mToggle.setDrawerIndicatorEnabled(false);
        mToggle.setHomeAsUpIndicator(R.drawable.ic_account_circle_black_24dp);
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

}
