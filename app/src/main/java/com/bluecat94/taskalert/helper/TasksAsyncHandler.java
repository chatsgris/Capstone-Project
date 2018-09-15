package com.bluecat94.taskalert.helper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by liumi on 9/15/2018.
 */

public class TasksAsyncHandler extends AsyncQueryHandler {

    public TasksAsyncHandler(ContentResolver cr) {super(cr);}

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {}

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {}
}