package com.bluecat94.taskalert.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;

/**
 * Created by mimiliu on 10/4/18.
 */

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;

    public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {mCursor.close();}
        mCursor = mContext.getContentResolver().query(TasksContract.TaskEntry.CONTENT_URI,
                null,
                null,
                null,
                TasksContract.TaskEntry.COLUMN_TS_CREATED);
    }

    @Override
    public void onDestroy() {mCursor.close();}

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);
        String title = mCursor.getString(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_TITLE));
        String description = mCursor.getString(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_DESCRIPTION));
        String placeId = mCursor.getString(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_PLACE_ID));
        double lat = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_LATITTUDE));
        double longitude = mCursor.getDouble(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_LONGITUDE));
        long createdTs = mCursor.getLong(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_TS_CREATED));

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        views.setTextViewText(R.id.widget_item_title, title);

        // Fill in the onClick PendingIntent Template
        Bundle extras = new Bundle();
        extras.putLong(TasksContract.TaskEntry.COLUMN_TS_CREATED, createdTs);
        extras.putString(TasksContract.TaskEntry.COLUMN_PLACE_ID, placeId);
        extras.putString(TasksContract.TaskEntry.COLUMN_DESCRIPTION, description);
        extras.putString(TasksContract.TaskEntry.COLUMN_TITLE, title);
        extras.putDouble(TasksContract.TaskEntry.COLUMN_LONGITUDE, longitude);
        extras.putDouble(TasksContract.TaskEntry.COLUMN_LATITTUDE, lat);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_item_title, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
