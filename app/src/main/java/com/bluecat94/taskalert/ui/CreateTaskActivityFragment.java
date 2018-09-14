package com.bluecat94.taskalert.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecat94.taskalert.R;

import butterknife.BindView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateTaskActivityFragment extends Fragment {
    @BindView(R.id.task_title_name) TextView taskTitleNameTv;


    public CreateTaskActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_task, container, false);
    }
}
