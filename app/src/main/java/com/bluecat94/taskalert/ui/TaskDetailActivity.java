package com.bluecat94.taskalert.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = findViewById(R.id.activity_task_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra(TasksContract.TaskEntry.COLUMN_TITLE);
        String description = intent.getStringExtra(TasksContract.TaskEntry.COLUMN_DESCRIPTION);
        long longitude = intent.getLongExtra(TasksContract.TaskEntry.COLUMN_LONGITUDE, 0);
        long lat = intent.getLongExtra(TasksContract.TaskEntry.COLUMN_LATITTUDE, 0);

        Bundle bundle = new Bundle();
        bundle.putString(TasksContract.TaskEntry.COLUMN_TITLE, title);
        bundle.putString(TasksContract.TaskEntry.COLUMN_DESCRIPTION, description);
        bundle.putLong(TasksContract.TaskEntry.COLUMN_LATITTUDE, lat);
        bundle.putLong(TasksContract.TaskEntry.COLUMN_LONGITUDE, longitude);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TaskDetailActivityFragment fragment = new TaskDetailActivityFragment();
        fragment.setArguments(bundle);
        fragmentTransaction.add(R.id.fragment_task_detail_container, fragment);
        fragmentTransaction.commit();
    }
}
