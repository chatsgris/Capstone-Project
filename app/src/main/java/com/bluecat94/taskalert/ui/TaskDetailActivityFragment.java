package com.bluecat94.taskalert.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskDetailActivityFragment extends Fragment implements OnMapReadyCallback {
    private String mTitle;
    private String mDescription;
    private double mLong;
    private double mLat;
    private GoogleMap mGoogleMap;

    @BindView(R.id.detail_button_delete) Button mButton;
    @BindView(R.id.detail_description_name) TextView mDescriptionName;
    @BindView(R.id.detail_description_value) TextView mDescriptionValue;
    @BindView(R.id.detail_title_name) TextView mTitleName;
    @BindView(R.id.detail_title_value) TextView mTitleValue;
    @BindView(R.id.detail_map) MapView mMap;
    @BindView(R.id.detail_venue_name) TextView mVenueName;

    public TaskDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTitle = getArguments().getString(TasksContract.TaskEntry.COLUMN_TITLE);
        mDescription = getArguments().getString(TasksContract.TaskEntry.COLUMN_DESCRIPTION);
        mLat = getArguments().getDouble(TasksContract.TaskEntry.COLUMN_LATITTUDE);
        mLong = getArguments().getDouble(TasksContract.TaskEntry.COLUMN_LONGITUDE);

        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        ButterKnife.bind(this, view);
        mTitleValue.setText(mTitle);
        mDescriptionValue.setText(mDescription);


            // Initialise the MapView
            mMap.onCreate(null);
            // Set the map ready callback to receive the GoogleMap object
            mMap.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        setMapLocation();
    }

    private void setMapLocation() {
        if (mGoogleMap == null) return;
        // Add a marker for this item and set the camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLat, mLong), 16));
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLat, mLong)));
    }
}
