package com.bluecat94.taskalert.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive called");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int type = geofencingEvent.getGeofenceTransition();
        if (type == Geofence.GEOFENCE_TRANSITION_ENTER) {
            //TODO 1: start notification
        } else {
            Log.e(TAG, String.format("Unknown transition type: %d", type));
        }
    }

    private void sendNotification(Context context, int type) {
        if (type == Geofence.GEOFENCE_TRANSITION_ENTER) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context) //TODO 3: find out about channel IDs
                    .setContentTitle("TaskAlert!")
                    .setContentText("Remember to do your task!") //TODO 2: set text to task title
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            Log.e(TAG, String.format("Unknown transition type: %d", type));
        }
    }
}
