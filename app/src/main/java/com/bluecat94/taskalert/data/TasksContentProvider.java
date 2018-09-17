package com.bluecat94.taskalert.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by mimiliu on 9/11/18.
 */

public class TasksContentProvider extends ContentProvider{

    private TasksDbHelper mTasksDbHelper;
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    public static final int TASKS = 100;
    public static final int TASKS_WITH_TS = 101;


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TasksContract.AUTHORITY, TasksContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TasksContract.AUTHORITY, TasksContract.PATH_TASKS + "/#", TASKS_WITH_TS);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTasksDbHelper = new TasksDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,  String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mTasksDbHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case TASKS:
                cursor = db.query(TasksContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTasksDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case TASKS:
                long id = db.insert(TasksContract.TaskEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TasksContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mTasksDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int tasksDeleted;
        switch (match) {
            case TASKS_WITH_TS:
                String ts = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TasksContract.TaskEntry.TABLE_NAME, TasksContract.TaskEntry.COLUMN_TS_CREATED + "=?", new String[]{ts});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
