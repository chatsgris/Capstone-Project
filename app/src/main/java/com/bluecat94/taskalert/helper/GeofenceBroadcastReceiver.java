package com.bluecat94.taskalert.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bluecat94.taskalert.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by mimiliu on 9/27/18.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    public String CHANNEL_ID = "123";
    public int NOTIFICATION_ID = 123;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int type = geofencingEvent.getGeofenceTransition();
        if (type == Geofence.GEOFENCE_TRANSITION_ENTER) {
            sendNotification(context, type);
        } else {
            Log.e(TAG, String.format("Unknown transition type: %d", type));
        }
    }

    private void sendNotification(Context context, int type) {
        if (type == Geofence.GEOFENCE_TRANSITION_ENTER) {
            NotificationManager notificationManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                notificationManager = context.getSystemService(NotificationManager.class);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = context.getResources().getString(R.string.channel_name);
                String description = context.getResources().getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("TaskAlert!")
                        .setContentText("Remember to do your task!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                Notification notification = builder.build();
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        } else {
            Log.e(TAG, String.format("Unknown transition type: %d", type));
        }
    }
}
