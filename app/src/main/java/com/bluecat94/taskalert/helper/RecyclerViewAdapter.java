package com.bluecat94.taskalert.helper;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluecat94.taskalert.R;
import com.bluecat94.taskalert.data.TasksContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liumi on 9/15/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Cursor mCursor;
    private Context mContext;


    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_item_title);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String title = mCursor.getString(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_TITLE));
            holder.taskTitle.setText(title);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
