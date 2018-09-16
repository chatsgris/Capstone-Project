package com.bluecat94.taskalert.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mimiliu on 9/11/18.
 */

public class TasksDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 4;

    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TasksContract.TaskEntry.TABLE_NAME + " (" +
                TasksContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT, " +
                TasksContract.TaskEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_LATITTUDE + " FLOAT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_LONGITUDE + " FLOAT NOT NULL, " +
                TasksContract.TaskEntry.COLUMN_TS_CREATED + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TasksContract.TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}
