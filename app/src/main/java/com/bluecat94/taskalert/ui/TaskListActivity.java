package com.bluecat94.taskalert.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;
import com.bluecat94.taskalert.helper.Geofencing;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;


public class TaskListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        TaskListActivityFragment.OnDataPass,
        CreateTaskActivityFragment.OnTaskCreated,
        TaskDetailActivityFragment.OnTaskDeleted {

    private Geofencing mGeofencing;
    private GoogleApiClient mClient;
    private TaskDetailActivityFragment mFragment;

    public static final String TAG = TaskListActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaskListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = findViewById(R.id.activity_task_list_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getResources().getBoolean(R.bool.is_two_pane)) {
                    Intent intent = new Intent(view.getContext(), CreateTaskActivity.class);
                    view.getContext().startActivity(intent);
                } else {
                    CreateTaskActivityFragment fragment = new CreateTaskActivityFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detail_container, fragment)
                            .commit();
                }
            }
        });

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        mGeofencing = new Geofencing(this, mClient);
        mGeofencing.registerAllGeofences();

        if (getResources().getBoolean(R.bool.is_two_pane)) {
            mFragment = new TaskDetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TasksContract.TaskEntry.COLUMN_DESCRIPTION, "descrip");
            bundle.putString(TasksContract.TaskEntry.COLUMN_TITLE, "title");
            bundle.putString(TasksContract.TaskEntry.COLUMN_PLACE_ID, "123");
            bundle.putDouble(TasksContract.TaskEntry.COLUMN_LATITTUDE, 44.44);
            bundle.putDouble(TasksContract.TaskEntry.COLUMN_LONGITUDE, 44.44);
            bundle.putLong(TasksContract.TaskEntry.COLUMN_TS_CREATED, 12345);
            mFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.detail_container, mFragment)
                    .commit();
            TaskListActivityFragment fragment = new TaskListActivityFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.master_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true; //TODO: insert accessibility support
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                this.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaskListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        refreshPlacesData();
        Log.i(TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "API Client Connection Failed!");
    }

    public void refreshPlacesData() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public  android.support.v4.content.Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor c = null;

            @Override
            protected void onStartLoading() {
                if (c != null) {
                    deliverResult(c);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContext().getContentResolver().query(TasksContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TasksContract.TaskEntry.COLUMN_TS_CREATED);
                } catch (Exception e) {
                    Log.e(TaskListActivityFragment.class.getSimpleName(), "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                c = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) return;
        List<String> placeIds = new ArrayList<>();
        data.moveToFirst();
        placeIds.add(data.getString(data.getColumnIndex(TasksContract.TaskEntry.COLUMN_PLACE_ID)));
        while (data.moveToNext()) {
            placeIds.add(data.getString(data.getColumnIndex(TasksContract.TaskEntry.COLUMN_PLACE_ID)));
        }
        final PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient,
                placeIds.toArray(new String[placeIds.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                mGeofencing.updateGeofencesList(places);
                mGeofencing.registerAllGeofences();
            }
        });
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaskListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        refreshPlacesData();
    }

    @Override
    public void onDataPass(Bundle bundle) {
        mFragment = new TaskDetailActivityFragment();
        mFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_container, mFragment)
                .commit();
    }

    @Override
    public void onTaskCreated(String string) {
        if (string.equals("TASK_CREATED")) {
            TaskListActivityFragment fragment = new TaskListActivityFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onTaskDeleted(String string) {
        if (string.equals("TASK_DELETED")) {
            TaskListActivityFragment fragment = new TaskListActivityFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_container, fragment)
                    .commit();
        }
    }
}
