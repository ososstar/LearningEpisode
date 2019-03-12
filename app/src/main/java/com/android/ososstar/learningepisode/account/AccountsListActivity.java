package com.android.ososstar.learningepisode.account;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AccountsListActivity extends AppCompatActivity {

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
        if (user.getType() == 0) insertFAB.setVisibility(View.VISIBLE);
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountsListActivity.this, "Insert New Account Activity is Under Construction", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void connectASAP() {
        if (LoginActivity.isConnected(AccountsListActivity.this)) {
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

                        // Extract the value for the key called "c_enrolls"
                        int type = currentUser.getInt("type");

                        // Extract the value for the key called "c_enrolls"
                        String creationDate = currentUser.getString("creation_date");

                        // Create a new {@link User} object with the ID, Name, Description
                        // , Image, enrolls and creation Date from the JSON response.
                        User user = new User(id, username, email, name, type, creationDate);

                        // Add the new {@link User} to the list of userList.
                        userList.add(user);
                    }

                    mUserAdapter = new UserAdapter(getBaseContext(), userList);
                    mList.setAdapter(mUserAdapter);

                } else {
                    // Set empty state text to display "No earthquakes found."
                    mEmptyStateTextView.setText(R.string.no_users);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            VolleyLog.wtf(response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressBar.setVisibility(View.GONE);
            SharedPrefManager.getInstance(AccountsListActivity.this).setSSLStatus(1);
            connectASAP();
            error.printStackTrace();
            // Set empty state text to display "No Users is found."
            mEmptyStateTextView.setText("Error: No Data Received");
        }
    }){
        @Override
        public byte[] getBody() {

            String requestBody = "admin_ID="+admin_ID;  //The request body goes in here.

            return requestBody.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String getBodyContentType() {
            return "application/x-www-form-urlencoded; charset=utf-8";
        }
    };
    mRequestQueue.add(request);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar for the admin.
        if (user.getType() == 0){
            getMenuInflater().inflate(R.menu.menu_add, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
             //Respond to a click on the "Insert" icon menu option
            case R.id.action_insert_new:
                // move to insert course activity
//                startActivity(new Intent(AccountsListActivity.this, InsertAccountActivity.class));
                Toast.makeText(AccountsListActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                // Exit Activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
