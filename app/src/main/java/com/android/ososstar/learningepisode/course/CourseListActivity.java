package com.android.ososstar.learningepisode.course;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity implements CourseAdapter.OnItemClickListener {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = CourseListActivity.class.getName();

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
    private CourseAdapter mCourseAdapter;
    private ArrayList<Course> courseList;
    private RequestQueue mRequestQueue;

    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_ENROLLS = "enrolls";
    public static final String EXTRA_DATE = "date";

//    /**
//     * check if network is Connected or not
//     */
//    public static boolean isConnected(Context context) {
//        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
//        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle(R.string.course_list);

        progressBar = findViewById(R.id.l_spinner);

        mEmptyStateTextView = findViewById(R.id.l_empty_view);

        mList = findViewById(R.id.l_RV);

        courseList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);


        mRequestQueue = Volley.newRequestQueue(this);

        if (ConnectivityHelper.isNetworkAvaliable(CourseListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            parseJSON();

        } else {// Set empty state text to display "Check your connection"
            mEmptyStateTextView.setText(R.string.noInternet);
            connectASAP();
            }

        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);
        //show FAB for admin
        if (user.getType() == 0) insertFAB.show();
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to insert course activity
                startActivityForResult(new Intent(CourseListActivity.this, CourseInsertActivity.class), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, CourseListActivity.class));
            finish();
        }
    }

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvaliable(CourseListActivity.this)) {
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
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URLs.URL_REQUEST_COURSE_LIST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mEmptyStateTextView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        if (response != null) {
                            try {
                                //if no error in response
                                if (!response.getBoolean("error")) {
                                    //getting the courses list from the response
                                    JSONArray courseArray = response.getJSONArray("courses");

                                    // For each course in the course Array, create a {@link Course} object
                                    for (int i = 0; i < courseArray.length(); i++) {
                                        // Get a single Course at position i within the list of courseList
                                        JSONObject currentCourse = courseArray.getJSONObject(i);

                                        // Extract the value for the key called "ID"
                                        String id = currentCourse.getString("ID");

                                        // Extract the value for the key called "c_name"
                                        String name = currentCourse.getString("c_name");

                                        // Extract the value for the key called "c_description"
                                        String description = currentCourse.getString("c_description");

                                        // Extract the value for the key called "c_image"
                                        String image = currentCourse.getString("c_image");

                                        // Extract the value for the key called "c_enrolls"
                                        String enrolls = currentCourse.getString("c_enrolls");

                                        // Extract the value for the key called "c_enrolls"
                                        String creationDate = currentCourse.getString("creation_date");

                                        // Create a new {@link Course} object with the ID, Name, Description
                                        // , Image, enrolls and creation Date from the JSON response.
                                        Course course = new Course(id, name, description, image, enrolls, creationDate);

                                        // Add the new {@link Course} to the list of courseList.
                                        courseList.add(course);
                                    }

                                    mCourseAdapter = new CourseAdapter(CourseListActivity.this, courseList);
                                    mList.setAdapter(mCourseAdapter);
                                    mCourseAdapter.notifyDataSetChanged();
                                    mCourseAdapter.setOnItemClickListener(CourseListActivity.this);


                                } else {
                                    // Set empty state text to display "No earthquakes found."
                                    mEmptyStateTextView.setText(R.string.no_course);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i(LOG_TAG, "TEST: mainAdapter.addall(courseList) executed...");

                        } else {
                            // Set empty state text to display "No earthquakes found."
                            mEmptyStateTextView.setText("No Data Received");
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                connectASAP();
                SharedPrefManager.getInstance(CourseListActivity.this).setSSLStatus(1);
                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                mEmptyStateTextView.setText(R.string.error_no_data_received);

            }
        });
        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position){
        Intent intent = new Intent(CourseListActivity.this, CourseActivity.class);
        Course clickedItem = courseList.get(position);
        intent.putExtra(EXTRA_ID, clickedItem.getCourseID());
        intent.putExtra(EXTRA_NAME, clickedItem.getCourseName());
        intent.putExtra(EXTRA_DESCRIPTION, clickedItem.getCourseDescription());
        intent.putExtra(EXTRA_IMAGE, clickedItem.getCourseImage());
        intent.putExtra(EXTRA_ENROLLS, clickedItem.getCourseEnrolls());
        intent.putExtra(EXTRA_DATE, clickedItem.getCourseCreationDate());
        CourseListActivity.this.startActivity(intent);
    }


}