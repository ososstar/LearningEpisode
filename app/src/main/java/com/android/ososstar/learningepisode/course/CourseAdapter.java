package com.android.ososstar.learningepisode.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Course> mCourseList;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Create a new {@link CourseAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param courseList is the list of {@link Course} to be displayed.
     */
    public CourseAdapter(Context context, ArrayList<Course> courseList) {
        mContext = context;
        mCourseList = courseList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course currentCourse = mCourseList.get(position);

        String courseImg = currentCourse.getCourseImage();
        if (courseImg != null && courseImg.isEmpty()) {
            holder.courseImage.setImageResource(R.drawable.defaultplaceholder);
        } else {
        Picasso.with(mContext).load(courseImg)
                .placeholder(R.drawable.defaultplaceholder).error(R.drawable.defaultplaceholder).noFade()
                .into(holder.courseImage);
        }
        holder.courseName.setText(currentCourse.getCourseName());

        if (!currentCourse.getCourseDescription().isEmpty()) {
            holder.courseDescription.setText(String.valueOf(currentCourse.getCourseDescription()));
        }else {
            holder.courseDescription.setText(R.string.no_description);
        }
        holder.courseEnrolls.setText(new StringBuffer().append("Total Enrolls ").append(String.valueOf(currentCourse.getCourseEnrolls())));
        holder.courseDate.setText(new StringBuffer().append("Created ON ").append(String.valueOf(currentCourse.getCourseCreationDate())).toString());

        holder.course_ID = currentCourse.getCourseID();

    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private CircleImageView courseImage;
        private TextView courseName, courseDescription, courseEnrolls, courseDate;
        private String admin_ID, course_ID;
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

            courseName = itemView.findViewById(R.id.course_item_name);
            courseDescription = itemView.findViewById(R.id.course_item_description);
            courseImage = itemView.findViewById(R.id.course_item_image);
            courseEnrolls = itemView.findViewById(R.id.course_item_enrolls);
            courseDate = itemView.findViewById(R.id.course_item_date);

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
                    Intent modifyIntent = new Intent(itemView.getContext(), CourseModifyActivity.class);
                    Bundle modifyBundle = new Bundle();
                    modifyBundle.putString("admin_ID", admin_ID);
                    modifyBundle.putString("course_ID", course_ID);
                    modifyIntent.putExtras(modifyBundle);
                    ((CourseListActivity) mContext).startActivityForResult(modifyIntent, 1);
                    return true;

                case R.id.option_2:
                    deleteCourse();
                    return true;
            }
            return false;
        }

        private void deleteCourse() {
            mRequestQueue = Volley.newRequestQueue(mContext);

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_COURSE_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(mContext, String.valueOf(baseJSONObject.getString("message")), Toast.LENGTH_SHORT).show();
                            //TODO refresh RecyclerView List

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
                    Map<String, String> pars = new HashMap<String, String>();
                    pars.put("Content-Type", "application/x-www-form-urlencoded");
                    return pars;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pars = new HashMap<String, String>();
                    pars.put("admin_ID", admin_ID);
                    pars.put("course_ID", course_ID);
                    return pars;
                }
            };
            mRequestQueue.add(request);
        }

    }

}