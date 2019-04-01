package com.android.ososstar.learningepisode.account;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountsListActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = AccountsListActivity.class.getName();

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * progressBar that is displayed when the list is loading
     */
    private ProgressBar progressBar;

    /**
     * RecyclerView, Adapter, and Volley Method to show data into list
     */
    private RecyclerView mList;
    private UserAdapter mUserAdapter;
    private ArrayList<User> userList;
    private RequestQueue mRequestQueue;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();
    private String admin_ID = String.valueOf(user.getID());

    /**
     * check if network is Connected or not
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle(R.string.user_list);

        mEmptyStateTextView = findViewById(R.id.l_empty_view);

        mList = findViewById(R.id.l_RV);

        userList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        if (isConnected(this)) {
            progressBar = findViewById(R.id.l_spinner);
            progressBar.setVisibility(View.VISIBLE);
            parseJSON();
        }else {
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show();
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.noInternet);
        }

        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);
        //show FAB for admin
        if (user.getType() == 0) insertFAB.show();
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AccountsListActivity.this, AccountInsertActivity.class), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            parseJSON();
        }
    }

    public static final String ADMIN_ID = "admin_ID";
    public static final String PROFILE_ID = "user_ID";
    public static final String PROFILE_USERNAME = "username";
    public static final String PROFILE_EMAIL = "email";
    public static final String PROFILE_NAME = "name";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String PROFILE_TYPE = "type";
    public static final String PROFILE_DATE = "date";

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvailable(AccountsListActivity.this)) {
            parseJSON();
            return;
        }
        CountDownTimer cd = new CountDownTimer(2222, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                connectASAP();
            }
        };
        cd.start();
    }

    private void parseJSON(){
    StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REQUEST_STUDENT_LIST, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressBar.setVisibility(View.GONE);

            userList.clear();

            try {
                JSONObject baseJSONObject = new JSONObject(response);

                //if no error in response
                if (!baseJSONObject.getBoolean("error")) {
                    //getting the users list from the response
                    JSONArray usersArray = baseJSONObject.getJSONArray("users");

                    // For each user in the users Array, create a {@link User} object
                    for (int i = 0; i < usersArray.length(); i++) {
                        // Get a single User at position i within the list of userList
                        JSONObject currentUser = usersArray.getJSONObject(i);

                        // Extract the value for the key called "ID"
                        int id = currentUser.getInt("ID");

                        // Extract the value for the key called "c_description"
                        String username = currentUser.getString("s_username");

                        // Extract the value for the key called "c_image"
                        String email = currentUser.getString("s_email");

                        // Extract the value for the key called "c_name"
                        String name = currentUser.getString("s_name");

                        String image = currentUser.getString("s_image");

                        // Extract the value for the key called "c_enrolls"
                        int type = currentUser.getInt("type");

                        // Extract the value for the key called "c_enrolls"
                        String creationDate = currentUser.getString("creation_date");

                        // Create a new {@link User} object with the ID, Name, Description
                        // , Image, enrolls and creation Date from the JSON response.
                        User user = new User(id, username, email, name, image, type, creationDate);

                        // Add the new {@link User} to the list of userList.
                        userList.add(user);
                    }

                    mUserAdapter = new UserAdapter(AccountsListActivity.this, userList);
                    mList.setAdapter(mUserAdapter);
                    mUserAdapter.notifyDataSetChanged();
                    mUserAdapter.setOnItemClickListener(AccountsListActivity.this);

                } else {
                    // Set empty state text to display "No earthquakes found."
                    mEmptyStateTextView.setText(R.string.no_users);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressBar.setVisibility(View.GONE);
            SharedPrefManager.getInstance(AccountsListActivity.this).setSSLStatus(1);
            connectASAP();
            error.printStackTrace();
            // Set empty state text to display "No Users is found."
            mEmptyStateTextView.setText(R.string.error_no_data_received);
        }
    }){
        @Override
        public byte[] getBody() {

            String requestBody = "admin_ID="+admin_ID;  //The request body goes in here.
//            StandardCharsets.UTF_8
            return requestBody.getBytes();
        }

        @Override
        public String getBodyContentType() {
            return "application/x-www-form-urlencoded; charset=utf-8";
        }
    };
    mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {

        Intent profileIntent = new Intent(AccountsListActivity.this, ProfileViewActivity.class);

        User clickedItem = userList.get(position);

        Bundle profileBundle = new Bundle();
        profileBundle.putString(ADMIN_ID, admin_ID);
        profileBundle.putString(PROFILE_ID, String.valueOf(clickedItem.getID()));
        profileBundle.putString(PROFILE_USERNAME, clickedItem.getUsername());
        profileBundle.putString(PROFILE_EMAIL, clickedItem.getEmail());
        profileBundle.putString(PROFILE_NAME, clickedItem.getName());
        profileBundle.putString(PROFILE_PICTURE, clickedItem.getImageURL());
        profileBundle.putString(PROFILE_TYPE, String.valueOf(clickedItem.getType()));
        profileBundle.putString(PROFILE_DATE, clickedItem.getDate());

        profileIntent.putExtras(profileBundle);

        AccountsListActivity.this.startActivity(profileIntent);

    }

}
