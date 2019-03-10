package com.android.ososstar.learningepisode.course;

import android.content.Context;
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
import com.android.ososstar.learningepisode.account.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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


    }

    @Override
    public int getItemCount() {
        return mCourseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private CircleImageView courseImage;
        private TextView courseName, courseDescription, courseEnrolls, courseDate;

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
                    Toast.makeText(mContext, "modify activity is under construction", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.option_2:
                    Toast.makeText(mContext, "delete activity is under construction", Toast.LENGTH_SHORT).show();
                    return true;

            }
            return false;
        }

    }
}