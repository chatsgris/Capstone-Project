package com.bluecat94.taskalert.helper;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by mimiliu on 10/4/18.
 */

public class WidgetRemoteViewsServie extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
