package com.android.ososstar.learningepisode.lesson;

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


public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Lesson> mLessonList;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Create a new {@link LessonAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param lessonList is the list of {@link Lesson} to be displayed.
     */
    public LessonAdapter(Context context, ArrayList<Lesson> lessonList) {
        mContext = context;
        mLessonList = lessonList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson currentLesson = mLessonList.get(position);

        holder.lessonID.setText(mContext.getString(R.string.id) + currentLesson.getID());
        holder.lessonTitle.setText(currentLesson.getTitle());
        holder.lessonCreationDate.setText(mContext.getString(R.string.created_on) + currentLesson.getCreationDate());
        holder.lesson_ID = currentLesson.getID();
        holder.course_ID = currentLesson.getCourseID();

        holder.lesson_title = currentLesson.getTitle();
        holder.lesson_description = currentLesson.getDescription();
        holder.lesson_downloadURL = currentLesson.getLink();
        holder.lesson_videoURL = currentLesson.getVideoURL();

    }

    @Override
    public int getItemCount() {
        return mLessonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private TextView lessonID, lessonTitle, lessonCreationDate;
        private String admin_ID, lesson_ID, course_ID, lesson_title, lesson_description, lesson_downloadURL, lesson_videoURL;
        //getting the current user
        private User user = SharedPrefManager.getInstance(mContext).getUser();
        private RequestQueue mRequestQueue;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            //getting the current user type
            int userType = SharedPrefManager.getInstance(mContext).getUser().getType();
            if (userType == 0) {
                itemView.setOnCreateContextMenuListener(this);
            }

            lessonID = itemView.findViewById(R.id.lesson_item_ID);
            lessonTitle = itemView.findViewById(R.id.lesson_item_title);
            lessonCreationDate = itemView.findViewById(R.id.lesson_item_date);

            mRequestQueue = Volley.newRequestQueue(mContext);

            if (user.getType() == 0) {
                admin_ID = String.valueOf(user.getID());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
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
                case R.id.option_1 :
                    Intent modifyIntent = new Intent(itemView.getContext(), LessonModifyActivity.class);
                    Bundle modifyBundle = new Bundle();
                    modifyBundle.putString("admin_ID", admin_ID);
                    modifyBundle.putString("lesson_ID", lesson_ID);
                    modifyBundle.putString("lesson_title", lesson_title);
                    modifyBundle.putString("lesson_description", lesson_description);
                    modifyBundle.putString("lesson_downloadURL", lesson_downloadURL);
                    modifyBundle.putString("lesson_videoURL", lesson_videoURL);
                    modifyBundle.putString("course_ID", course_ID);
                    modifyIntent.putExtras(modifyBundle);
                    try {
                        ((LessonListActivity) mContext).startActivityForResult(modifyIntent, 2);
                    } catch (Exception e) {
                        Log.e("LessonAdapter", String.valueOf(e));
                    }
                    return true;

                case R.id.option_2:
                    deleteLesson();
                    return true;
            }
            return false;
        }

        private void deleteLesson() {

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_LESSON_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        String message = baseJSONObject.getString("message");
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            //Remove deleted row from RecyclerView List
                            mLessonList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                            itemView.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
                    pars.put("admin_ID", admin_ID);
                    pars.put("lesson_ID", lesson_ID);
                    return pars;
                }
            };
            mRequestQueue.add(request);
        }

    }

}