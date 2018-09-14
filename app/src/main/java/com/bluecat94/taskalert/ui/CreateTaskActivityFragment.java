package com.bluecat94.taskalert.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluecat94.taskalert.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTaskActivityFragment extends Fragment {
    @BindView(R.id.task_title_name) TextView taskTitleTv;
    @BindView(R.id.task_title_value) EditText taskTitleEt;
    @BindView(R.id.task_description_name) TextView taskDescriptionTv;
    @BindView(R.id.task_description_value) EditText taskDescriptionEt;
    @BindView(R.id.task_venue_name) TextView taskVenueTv;
    @BindView(R.id.image_place_picker) ImageView placePickerImage;
    @BindView(R.id.text_place_picker) TextView placePickerTv;
    @BindView(R.id.button_place_picker) Button placePickerButton;

    private final static int PLACE_PICKER_REQUEST = 999;

    public CreateTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);
        ButterKnife.bind(this, view);

        String title = taskTitleEt.getText().toString();
        String description = taskDescriptionEt.getText().toString();

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
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this.getContext(), data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this.getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
