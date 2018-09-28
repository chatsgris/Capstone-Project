package com.bluecat94.taskalert.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;
import com.bluecat94.taskalert.helper.TasksAsyncHandler;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTaskActivityFragment extends Fragment {
    @BindView(R.id.task_title_value) EditText taskTitleEt;
    @BindView(R.id.task_description_value) EditText taskDescriptionEt;
    @BindView(R.id.image_place_picker) ImageView placePickerImage;
    @BindView(R.id.button_place_picker) Button placePickerButton;

    private final static int PLACE_PICKER_REQUEST = 999;
    private String mTitle;
    private String mDescription;
    private double mLat;
    private double mLong;
    private String mPlaceId;

    public CreateTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);
        ButterKnife.bind(this, view);

        placePickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        placePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this.getContext(), data);
                mLat = place.getLatLng().latitude;
                mLong = place.getLatLng().longitude;
                mPlaceId = place.getId();
                String toastMsg = String.format("Venue: %s, %s", String.valueOf(mLat), String.valueOf(mLong));
                Toast.makeText(this.getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addNewTask() {
        mTitle = taskTitleEt.getText().toString();
        mDescription = taskDescriptionEt.getText().toString();

        if ((mTitle == null || mTitle.startsWith(" ") || mTitle.startsWith("\n")) || (mLong == 0 && mLat == 0)) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle(getResources().getString(R.string.create_task_error_title));
            alertDialog.setMessage(getResources().getString(R.string.create_task_error_content));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            ContentValues cv = new ContentValues();
            long now = new Date().getTime();
            cv.put(TasksContract.TaskEntry.COLUMN_TS_CREATED, now);
            cv.put(TasksContract.TaskEntry.COLUMN_TITLE, mTitle);
            cv.put(TasksContract.TaskEntry.COLUMN_DESCRIPTION, mDescription);
            cv.put(TasksContract.TaskEntry.COLUMN_LATITTUDE, mLat);
            cv.put(TasksContract.TaskEntry.COLUMN_LONGITUDE, mLong);
            cv.put(TasksContract.TaskEntry.COLUMN_PLACE_ID, mPlaceId);

            TasksAsyncHandler tasksAsyncHandler = new TasksAsyncHandler(getContext().getContentResolver()) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    if(uri != null) {
                        Toast.makeText(getContext(), getResources().getString(R.string.create_task_toast), Toast.LENGTH_LONG).show();
                    }
                }
            };
            tasksAsyncHandler.startInsert(1, null, TasksContract.TaskEntry.CONTENT_URI, cv);
            getActivity().finish();
        }
    }
}
