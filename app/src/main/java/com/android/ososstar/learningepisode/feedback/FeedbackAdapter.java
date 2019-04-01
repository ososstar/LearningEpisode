package com.android.ososstar.learningepisode.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            StringBuilder idBuilder = new StringBuilder(mContext.getString(R.string.idHash));
            idBuilder.append(currentFeedback.getID());
            holder.feedbackID.setText(idBuilder);

            switch (currentFeedback.getFeedbackType()) {
                case "0":
                    holder.feedbackType.setText("Suggestion");
                    break;
                case "1":
                    holder.feedbackType.setText("Technical Issue");
                    break;
                case "2":
                    holder.feedbackType.setText("Other");
                    break;
            }

            StringBuilder dateHash = new StringBuilder(mContext.getString(R.string.dateHash));
            dateHash.append(currentFeedback.getDate());
            holder.feedbackDate.setText(dateHash);

            holder.feedbackStudentComment.setText(currentFeedback.getStudentComment());

            holder.feedback_ID = currentFeedback.getID();

            holder.feedback_type = currentFeedback.getFeedbackType();
            holder.feedback_stuComment = currentFeedback.getStudentComment();
            holder.feedback_attachImage = currentFeedback.getStudentAttachedImage();
            holder.feedback_admComment = currentFeedback.getAdminReply();

        } catch (Exception e) {
            Log.e("FeedbackAdapter", "onBindViewHolder: ", e);
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
        public static final String EXTRA_ADMIN_ID = "admin_ID";
        public static final String EXTRA_STUDENT_ID = "student_ID";
        public static final String EXTRA_FEEDBACK_ID = "feedback_ID";
        public static final String EXTRA_FEEDBACK_TYPE = "feedback_type";

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
            popup.getMenuInflater().inflate(R.menu.menu_management, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        public static final String EXTRA_FEEDBACK_STUDENT_COMMENT = "feedback_stuComment";
        public static final String EXTRA_FEEDBACK_ATTACHED_IMAGE = "feedback_attachedImage";
        public static final String EXTRA_FEEDBACK_ADMIN_COMMENT = "feedback_admComment";
        private RequestQueue mRequestQueue;
        private String admin_ID, student_ID, feedback_ID, feedback_type, feedback_stuComment, feedback_attachImage, feedback_admComment;
        //getting the current user
        private User user = SharedPrefManager.getInstance(mContext).getUser();
        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);

            //getting the current user type
            int userType = user.getType();

            switch (userType) {
                case 0:
                    admin_ID = String.valueOf(user.getID());
                    break;
                case 1:
                    student_ID = String.valueOf(user.getID());
                    break;
            }

            feedbackID = itemView.findViewById(R.id.feedbackList_item_ID);
            feedbackType = itemView.findViewById(R.id.feedbackList_item_type);
            feedbackDate = itemView.findViewById(R.id.feedbackList_item_date);
            feedbackStudentComment = itemView.findViewById(R.id.feedbackList_item_stuComment);

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
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.option_1:
                    Intent modifyIntent = new Intent(itemView.getContext(), FeedbackModifyActivity.class);
                    Bundle modifyBundle = new Bundle();
                    switch (user.getType()) {
                        case 0:
                            modifyBundle.putString(EXTRA_ADMIN_ID, admin_ID);
                            break;
                        case 1:
                            modifyBundle.putString(EXTRA_STUDENT_ID, student_ID);
                            break;
                    }
                    modifyBundle.putString(EXTRA_FEEDBACK_ID, feedback_ID);
                    modifyBundle.putString(EXTRA_FEEDBACK_TYPE, feedback_type);
                    modifyBundle.putString(EXTRA_FEEDBACK_STUDENT_COMMENT, feedback_stuComment);
                    modifyBundle.putString(EXTRA_FEEDBACK_ATTACHED_IMAGE, feedback_attachImage);
                    modifyBundle.putString(EXTRA_FEEDBACK_ADMIN_COMMENT, feedback_admComment);
                    modifyIntent.putExtras(modifyBundle);
                    ((FeedbackListActivity) mContext).startActivityForResult(modifyIntent, 1);

                    return true;

                case R.id.option_2:
                    deleteFeedback();
                    return true;
            }
            return false;
        }

        private void deleteFeedback() {

            //if no internet connection
            if (!ConnectivityHelper.isNetworkAvailable(mContext)) {
                Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            mRequestQueue = Volley.newRequestQueue(mContext);

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_FEEDBACK, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(mContext, String.valueOf(baseJSONObject.getString("message")), Toast.LENGTH_SHORT).show();
                            //Remove deleted row from RecyclerView List
                            mFeedbackList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                            itemView.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(mContext, String.valueOf(baseJSONObject.getString("message")), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> pars = new HashMap<>();
                    pars.put("Content-Type", "application/x-www-form-urlencoded");
                    return pars;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pars = new HashMap<>();
                    switch (user.getType()) {
                        case 0:
                            pars.put("admin_ID", admin_ID);
                            break;
                        case 1:
                            pars.put("student_ID", student_ID);
                            break;
                    }
                    pars.put("feedback_ID", feedback_ID);
                    return pars;
                }
            };
            mRequestQueue.add(request);
        }


    }
}
