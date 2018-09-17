package com.bluecat94.taskalert.ui;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;
import com.bluecat94.taskalert.helper.RecyclerViewAdapter;
import com.bluecat94.taskalert.helper.TasksAsyncHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class TaskListActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;
    private static final int LOADER_ID = 0;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    public TaskListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                mAdapter.removeItem(position);
                mAdapter.notifyItemRemoved(position);
                onResume();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        return view;
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
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
        if (mAdapter != null) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
