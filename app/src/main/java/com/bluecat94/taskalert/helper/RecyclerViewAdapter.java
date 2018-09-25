package com.bluecat94.taskalert.helper;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private ItemClickListener mClickListener;
    private int mPosition;

    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.task_item_title) TextView taskTitle;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, mPosition);
            }
        }
    }

    public void removeItem(int position) {
        mCursor.moveToPosition(position);
        long createdTs = mCursor.getLong(mCursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_TS_CREATED));

        TasksAsyncHandler tasksAsyncHandler = new TasksAsyncHandler(mContext.getContentResolver()) {
            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                if (result != 0) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.delete_task_toast), Toast.LENGTH_LONG).show();
                }
            }
        };
        tasksAsyncHandler.startDelete(
                1,
                null,
                TasksContract.TaskEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(createdTs)).build(),
                null,
                null);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(RecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
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
            mPosition = position;
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
