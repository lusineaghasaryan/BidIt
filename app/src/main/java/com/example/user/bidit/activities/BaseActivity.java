package com.example.user.bidit.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.user.bidit.utils.CheckInternetBroadcast;
import com.example.user.bidit.utils.UserMessages;

public class BaseActivity extends AppCompatActivity {
    protected BroadcastReceiver mBroadcastReceiver;
    protected IntentFilter mNetworkIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void checkInternet(final View view) {
        mNetworkIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] networkType = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_MOBILE};
                if (!CheckInternetBroadcast.isNetworkAvailable(context, networkType)) {
                    UserMessages.showSnackBarShort(view, "No Internet Connection");
                }
            }
        };
        registerReceiver(mBroadcastReceiver, mNetworkIntentFilter);
    }

}
