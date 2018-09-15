package com.bluecat94.taskalert.helper;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;

/**
 * Created by liumi on 9/15/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Cursor mCursor;
    private LayoutInflater mInflater;

    RecyclerViewAdapter(Context context, Cursor cursor) {
        this.mInflater = LayoutInflater.from(context);
        this.mCursor = cursor;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_item_title) TextView taskTitle;
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String title = mCursor.getString(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_TITLE));
            holder.taskTitle.setText(title);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {return 0;}
    }
}
