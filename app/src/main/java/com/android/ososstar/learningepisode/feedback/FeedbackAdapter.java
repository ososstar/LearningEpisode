package com.android.ososstar.learningepisode.feedback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Feedback> mFeedbackList;

    /**
     * Create a new {@link FeedbackAdapter} object.
     *
     * @param context      is the current context (i.e. Activity) that the adapter is being created in.
     * @param feedbackList is the list of {@link Feedback} to be displayed.
     */
    public FeedbackAdapter(Context context, ArrayList<Feedback> feedbackList) {
        mContext = context;
        mFeedbackList = feedbackList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        Feedback currentFeedback = mFeedbackList.get(position);

        try {
            holder.feedbackID.setText(currentFeedback.getID());
            holder.feedbackType.setText(currentFeedback.getFeedbackType());
            holder.feedbackDate.setText(currentFeedback.getDate());
            holder.feedbackStudentComment.setText(currentFeedback.getStudentComment());
        } catch (Exception e) {
            Log.e("FeedbackAdapter", "onBindViewHolder: osos", e);
        }

    }

    @Override
    public int getItemCount() {
        return mFeedbackList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private TextView feedbackID, feedbackType, feedbackDate, feedbackStudentComment;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            //getting the current user type
            int userType = SharedPrefManager.getInstance(mContext).getUser().getType();
            if (userType == 0) {
                itemView.setOnCreateContextMenuListener(this);
            }

            feedbackID = itemView.findViewById(R.id.feedback_item_ID);
            feedbackType = itemView.findViewById(R.id.feedback_item_type);
            feedbackDate = itemView.findViewById(R.id.feedback_item_date);
            feedbackStudentComment = itemView.findViewById(R.id.feedback_item_stuComment);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
            popup.getMenuInflater().inflate(R.menu.menu_management, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            // ERROR : How to get item position which ContextMenu Created
            switch (id) {
                case R.id.option_1:
                    Toast.makeText(mContext, "nice", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.option_2:
                    Toast.makeText(mContext, "nice2", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }
}
