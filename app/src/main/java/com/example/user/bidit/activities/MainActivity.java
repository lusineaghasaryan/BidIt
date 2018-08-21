package com.example.user.bidit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.fragments.AddItemFragment;
import com.example.user.bidit.fragments.LoginFragment;
import com.example.user.bidit.fragments.MyAccountFragment;
import com.example.user.bidit.fragments.RegistrationFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private AddItemFragment mAddItemFragment;
    private LoginFragment mLoginFragment;
    private LoginFragment.OnLoginFragmentChange mOnLoginFragmentChange;
    private RegistrationFragment mRegistrationFragment;
    private RegistrationFragment.OnRegistrationCompleted mOnRegistrationCompleted;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private MyAccountFragment mMyAccountFragment;
    private LoginActivity mLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setNavigationDrawer();
        initListeners();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowItemActivity.class);
                startActivity(intent);
//                replaceFragment(mLoginFragment);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(mMyAccountFragment);
            }
        });
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
            case R.id.nav_item_my_account:
                replaceFragment(new MyAccountFragment());
                break;
            case R.id.nav_item_balance:
                break;
            case R.id.nav_item_history:
                break;
            case R.id.nav_item_favorite:
                break;
            case R.id.nav_item_my_items:
                break;
            case R.id.nav_item_log_out:
                FireBaseAuthenticationManager.getInstance().signOut();
                break;
            case R.id.nav_item_log_in:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.nav_item_help:
                break;
            case R.id.nav_item_about_us:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragment_container, fragment);
        mFragmentTransaction.addToBackStack("fragment");
        mFragmentTransaction.commit();
    }

    public void removeFragment(Fragment fragment) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.remove(fragment);
        mFragmentManager.popBackStack();
        mFragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fragment_container, fragment);
        mFragmentTransaction.addToBackStack("fragment");
        mFragmentTransaction.commit();
    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mFragmentManager = getSupportFragmentManager();
        mAddItemFragment = new AddItemFragment();
        mLoginFragment = new LoginFragment();
        mRegistrationFragment = new RegistrationFragment();
        mMyAccountFragment = new MyAccountFragment();
        mLoginActivity = new LoginActivity();
    }

    private void initListeners() {
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
                updateNavigationDrawer();
            }
        });

        mOnLoginFragmentChange = new LoginFragment.OnLoginFragmentChange() {
            @Override
            public void onRemove() {
                removeFragment(mLoginFragment);
            }

            @Override
            public void onAdd() {
                addFragment(mRegistrationFragment);
            }
        };

        mLoginFragment.setOnLoginFragmentChange(mOnLoginFragmentChange);

        mOnRegistrationCompleted = new RegistrationFragment.OnRegistrationCompleted() {
            @Override
            public void onFragmentRemove() {
                removeFragment(mRegistrationFragment);
            }
        };
        mRegistrationFragment.setOnRegistrationCompleted(mOnRegistrationCompleted);
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
