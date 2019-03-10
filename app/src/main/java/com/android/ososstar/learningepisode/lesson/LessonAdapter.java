package com.android.ososstar.learningepisode.lesson;

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

import java.util.ArrayList;
import java.util.List;

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

    }

    @Override
    public int getItemCount() {
        return mLessonList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private TextView lessonID, lessonTitle, lessonDescription, lessonLink, lessonImage, lessonCreationDate;


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