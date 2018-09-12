package com.bluecat94.taskalert.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mimiliu on 9/11/18.
 */

public class TasksContract {
    public static final String AUTHORITY = "com.bluecat94.taskalert";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "tableTasks";
        public static final String COLUMN_TITLE = "taskData";
        public static final String COLUMN_DESCRIPTION = "taskDescription";
        public static final String COLUMN_LATITTUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TS_CREATED = "createdTimestamp";
    }
}
