package com.bluecat94.taskalert.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;
import com.bluecat94.taskalert.helper.TasksAsyncHandler;
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
    private long mTs;
    private GoogleMap mGoogleMap;
    OnTaskDeleted taskDeletedSignal;

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
        mTs = getArguments().getLong(TasksContract.TaskEntry.COLUMN_TS_CREATED);

        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);
        ButterKnife.bind(this, view);
        mTitleValue.setText(mTitle);
        mDescriptionValue.setText(mDescription);

        // Initialise the MapView
        mMap.onCreate(null);
        // Set the map ready callback to receive the GoogleMap object
        mMap.getMapAsync(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksAsyncHandler tasksAsyncHandler = new TasksAsyncHandler(getContext().getContentResolver()) {
                    @Override
                    protected void onDeleteComplete(int token, Object cookie, int result) {
                        if (result != 0) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.delete_task_toast), Toast.LENGTH_LONG).show();
                        }

                        if (!getContext().getResources().getBoolean(R.bool.is_two_pane)) {
                            getActivity().finish();
                        } else {
                            mButton.setVisibility(View.INVISIBLE);
                            mDescriptionName.setVisibility(View.INVISIBLE);
                            mDescriptionValue.setVisibility(View.INVISIBLE);
                            mTitleName.setVisibility(View.INVISIBLE);
                            mTitleValue.setVisibility(View.INVISIBLE);
                            mVenueName.setVisibility(View.INVISIBLE);
                            mMap.setVisibility(View.INVISIBLE);
                            taskDeleted("TASK_DELETED");
                        }
                    }
                };
                tasksAsyncHandler.startDelete(
                        1,
                        null,
                        TasksContract.TaskEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mTs)).build(),
                        null,
                        null);
            }
        });

        if (this.getResources().getBoolean(R.bool.is_two_pane) && mTs == 12345) {
            mButton.setVisibility(View.INVISIBLE);
            mDescriptionName.setVisibility(View.INVISIBLE);
            mDescriptionValue.setVisibility(View.INVISIBLE);
            mTitleName.setVisibility(View.INVISIBLE);
            mTitleValue.setVisibility(View.INVISIBLE);
            mVenueName.setVisibility(View.INVISIBLE);
            mMap.setVisibility(View.INVISIBLE);
        }

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

    public interface OnTaskDeleted {
        public void onTaskDeleted(String string);
    }

    public void taskDeleted(String string) {
        taskDeletedSignal.onTaskDeleted(string);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        taskDeletedSignal = (OnTaskDeleted) context;
    }
}
