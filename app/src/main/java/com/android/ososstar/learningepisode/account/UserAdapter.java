package com.android.ososstar.learningepisode.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import android.util.Log;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    private final Context mContext;
    private final List<User> mUserList;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(v, mListener);
    }

    /**
     * Create a new {@link UserAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param userList is the list of {@link User} to be displayed.
     */
    UserAdapter(Context context, ArrayList<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser = mUserList.get(position);

        holder.username = currentUser.getUsername();
        holder.email = currentUser.getEmail();
        holder.name = currentUser.getName();
        holder.imageURL = currentUser.getImageURL();
        holder.type = String.valueOf(currentUser.getType());

        holder.user_name.setText(currentUser.getName());
        StringBuilder userTypeSB = new StringBuilder(holder.itemView.getContext().getString(R.string.account_type_hash));
        switch (holder.type) {
            case "0":
                userTypeSB.append(mContext.getString(R.string.admin));
                holder.user_type.setText(userTypeSB);
                break;
            case "1":
                userTypeSB.append(mContext.getString(R.string.student));
                holder.user_type.setText(userTypeSB);
                break;
        }

//        holder.dateSB = new StringBuilder(mContext.getString(R.string.creation_date));
//        holder.dateSB.append(currentUser.getDate());
//        holder.user_creation_date.setText(holder.dateSB);

        StringBuilder creationDateSB = new StringBuilder(holder.itemView.getContext().getString(R.string.creation_date));
        if (Locale.getDefault().getLanguage().equals("ar")) {
            Locale localeAR = new Locale("ar");
            SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.time_pattern));
            Date date3 = null;
            try {
                date3 = sdf.parse(currentUser.getDate());
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", localeAR);
            String format = sdf.format(date3);
//            Log.wtf("result", format);
            creationDateSB.append(format);
            holder.user_creation_date.setText(creationDateSB);
        } else {
            Locale locale = new Locale("en");
            SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.time_pattern));
            Date date3 = null;
            try {
                date3 = sdf.parse(currentUser.getDate());
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", locale);
            String format = sdf.format(date3);
//            Log.wtf("result", format);
            creationDateSB.append(format);
            holder.user_creation_date.setText(creationDateSB);
        }


        holder.student_ID = String.valueOf(currentUser.getID());

        String userImg = currentUser.getImageURL();
        if (userImg != null && userImg.isEmpty()) {
            holder.user_image.setImageResource(R.drawable.user);
        } else {
            Picasso.get().load(userImg)
                    .placeholder(R.drawable.user).error(R.drawable.user).noFade()
                    .into(holder.user_image);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private final TextView user_name;
        private final TextView user_type;
        private final TextView user_creation_date;
        private final ImageView user_image;
        private StringBuilder dateSB;

        //getting the current user
        private final User user = SharedPrefManager.getInstance(mContext).getUser();
        private RequestQueue mRequestQueue;
        public static final String ADMIN_ID = "admin_ID";
        private String admin_ID, student_ID, username, email, name, imageURL, type;

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
            popup.getMenuInflater().inflate(R.menu.menu_management, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        public static final String STUDENT_ID = "student_ID";
        public static final String STUDENT_USERNAME = "username";
        public static final String STUDENT_EMAIL = "email";
        public static final String STUDENT_NAME = "name";
        public static final String STUDENT_IMAGE = "imageURL";
        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            //getting the current user type
            int userType = SharedPrefManager.getInstance(mContext).getUser().getType();
            if (userType == 0) {
                itemView.setOnCreateContextMenuListener(this);
            }
            if (user.getType() == 0) {
                admin_ID = String.valueOf(user.getID());
            }

            user_name = itemView.findViewById(R.id.user_name);
            user_type = itemView.findViewById(R.id.user_Type);
            user_creation_date = itemView.findViewById(R.id.user_creation_date);

            user_image = itemView.findViewById(R.id.user_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
        public static final String STUDENT_TYPE = "type";

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int id = item.getItemId();
            switch (id) {
                case R.id.option_1 :
                    modifyStudent();
                    return true;
                case R.id.option_2:
                    deleteStudent();
                    return true;

            }
            return false;
        }

        private void modifyStudent() {
            Intent modifyIntent = new Intent(itemView.getContext(), AccountModifyActivity.class);
            Bundle modifyBundle = new Bundle();
            modifyBundle.putString(ADMIN_ID, admin_ID);
            modifyBundle.putString(STUDENT_ID, student_ID);
            modifyBundle.putString(STUDENT_USERNAME, username);
            modifyBundle.putString(STUDENT_EMAIL, email);
            modifyBundle.putString(STUDENT_NAME, name);
            modifyBundle.putString(STUDENT_IMAGE, imageURL);
            modifyBundle.putString(STUDENT_TYPE, type);
            modifyIntent.putExtras(modifyBundle);
            ((AccountsListActivity) mContext).startActivityForResult(modifyIntent, 1);
        }

        private void deleteStudent() {
            mRequestQueue = Volley.newRequestQueue(mContext);

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_STUDENT_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(mContext, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                            //Remove deleted row from RecyclerView List
                            mUserList.remove(getAbsoluteAdapterPosition());
                            notifyItemRemoved(getAbsoluteAdapterPosition());
                            notifyItemRangeChanged(getAbsoluteAdapterPosition(), getItemCount());
                            itemView.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(mContext, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, itemView.getContext().getString(R.string.error_no_data_received), Toast.LENGTH_SHORT).show();
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
                    pars.put("student_ID", student_ID);
                    return pars;
                }
            };
            mRequestQueue.add(request);
        }


    }


}
