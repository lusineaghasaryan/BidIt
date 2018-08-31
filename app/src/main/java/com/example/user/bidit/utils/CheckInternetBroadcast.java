package com.example.user.bidit.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetBroadcast {

    public static boolean isNetworkAvailable(Context context, int [] typeNetworks) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int type : typeNetworks) {
                NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getNetworkInfo(type) : null;
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
