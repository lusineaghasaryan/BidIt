package com.example.user.bidit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Button;
import android.widget.Toast;

import com.example.user.bidit.R;
import com.example.user.bidit.firebase.FireBaseAuthenticationHelper;
import com.example.user.bidit.fragments.AddItemFragment;
import com.example.user.bidit.fragments.LoginFragment;
import com.example.user.bidit.fragments.MyAccountFragment;
import com.example.user.bidit.fragments.RegistrationFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private AddItemFragment mAddItemFragment;
    private LoginFragment mLoginFragment;
    private LoginFragment.OnFragmentChange mOnFragmentChange;
    private RegistrationFragment mRegistrationFragment;
    private RegistrationFragment.OnRegistrationCompleted mOnRegistrationComplated;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Button button;
    private MyAccountFragment mMyAccountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
//        setNavigationBar();
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
                FireBaseAuthenticationHelper.signOut();
                Log.d("TAG", "Sign Out");
                break;
            case R.id.nav_item_log_in:
                Log.d("TAG", "Create Account");
                replaceFragment(mLoginFragment);
                break;
            case R.id.nav_item_help:
                Log.d("TAG", "help");
                break;
            case R.id.nav_item_about_us:
                Toast.makeText(this, "sddgs", Toast.LENGTH_SHORT).show();
                break;
        }
        updateNavBar();
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

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
//        mNavigationView = findViewById(R.id.nav_view);
        mFragmentManager = getSupportFragmentManager();
        mAddItemFragment = new AddItemFragment();
        mLoginFragment = new LoginFragment();
        mRegistrationFragment = new RegistrationFragment();
        button = findViewById(R.id.button);
        mMyAccountFragment = new MyAccountFragment();
    }

    private void initListeners() {
        mOnFragmentChange = new LoginFragment.OnFragmentChange() {
            @Override
            public void onRemove() {
                removeFragment(mLoginFragment);
            }
            @Override
            public void onAdd() {
                addFragment(new RegistrationFragment());
            }
        };
        mLoginFragment.setOnFragmentChange(mOnFragmentChange);
        mOnRegistrationComplated = new RegistrationFragment.OnRegistrationCompleted() {
            @Override
            public void onFragmentRemove() {
                removeFragment(mRegistrationFragment);
            }
        };
        mRegistrationFragment.setOnRegistrationCompleted(mOnRegistrationComplated);
    }

    public void setNavigationBar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        updateNavBar();
    }

    public void updateNavBar() {
        if (FireBaseAuthenticationHelper.isLoggedIn()) {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, true);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(false);
        } else {
            mNavigationView.getMenu().setGroupVisible(R.id.menu_group_signed, false);
            mNavigationView.getMenu().findItem(R.id.nav_item_log_in).setVisible(true);
        }
    }

}
