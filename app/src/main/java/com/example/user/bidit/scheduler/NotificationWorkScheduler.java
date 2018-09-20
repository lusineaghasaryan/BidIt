package com.example.user.bidit.scheduler;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.user.bidit.R;
import com.example.user.bidit.activities.ShowItemActivity;
import com.example.user.bidit.db.ItemRepository;
import com.example.user.bidit.db.ItemRoomDatabase;
import com.example.user.bidit.models.Item;

import androidx.work.Worker;

public class NotificationWorkScheduler extends Worker{
    private static String CHANNEL_ID;

    public static final String DATA_KEY = "data key";

    private static final String CHANNEL_NAME = "bid_channel_name";
    private static final String CHANNEL_DESCRIPTION = "bid_channel_description";

    private PendingIntent mPendingIntent;
    private Intent mIntent;

    @NonNull
    @Override
    public Result doWork() {
        createPendingIntent();
        sendNotification1();
        getInputData().getString(DATA_KEY);
        return Result.SUCCESS;
    }

    private void sendNotification1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("asd", "asd", importance);
            channel.setDescription("asd");
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), "asd")
                .setSmallIcon(R.drawable.ic_nav_favorite)
                .setContentTitle("title")
                .setContentText("started")
                .setContentIntent(mPendingIntent);
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void createPendingIntent() {
        mIntent = new Intent(getApplicationContext(), ShowItemActivity.class);
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
