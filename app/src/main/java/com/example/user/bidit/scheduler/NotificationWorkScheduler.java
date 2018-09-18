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
        ItemRoomDatabase database = ItemRoomDatabase.getDatabase(getApplicationContext());
        Item item = database.itemDao().getById(getInputData().getString(DATA_KEY));

        mIntent = new Intent(getApplicationContext(), ShowItemActivity.class);
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        sendNotification1(item);
        return Result.SUCCESS;
    }

    private void sendNotification1(Item pItem) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_favorite)
                .setContentTitle(pItem.getItemTitle())
                .setContentText("started")
                .setContentIntent(mPendingIntent);
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}
