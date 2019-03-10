package com.android.ososstar.learningepisode.course;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.question.QuestionInsertActivity;
import com.android.ososstar.learningepisode.question.QuestionListActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.google.android.exoplayer2.C;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoursesListActivity extends AppCompatActivity implements CourseAdapter.OnItemClickListener {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = CoursesListActivity.class.getName();

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

    /**
     * check if network is Connected or not
     */
    public static boolean isConnected(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

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

        if (isConnected(getBaseContext())) {
            progressBar.setVisibility(View.VISIBLE);
            parseJSON();

        }else {Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show();
            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.noInternet);
            }

        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);
        //show FAB for admin
        if (user.getType() == 0) insertFAB.setVisibility(View.VISIBLE);
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to insert course activity
                startActivityForResult(new Intent(CoursesListActivity.this, CourseInsertActivity.class), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, CoursesListActivity.class));
            finish();
        }
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

                                    mCourseAdapter = new CourseAdapter(CoursesListActivity.this, courseList);
                                    mList.setAdapter(mCourseAdapter);
                                    mCourseAdapter.notifyDataSetChanged();
                                    mCourseAdapter.setOnItemClickListener(CoursesListActivity.this);


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
                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                mEmptyStateTextView.setText(R.string.error_no_data_received);
                CountDownTimer CDT = new CountDownTimer(2500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        parseJSON();
                    }
                };
                CDT.start();

            }
        });
        mRequestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
//        if (user.getType() == 0){
//            getMenuInflater().inflate(R.menu.menu_add, menu);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // Respond to a click on the "Insert" icon menu option
            case R.id.action_insert_new:
                // move to insert course activity
                startActivity(new Intent(CoursesListActivity.this, CourseInsertActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position){
        Intent intent = new Intent(CoursesListActivity.this, CourseActivity.class);
        Course clickedItem = courseList.get(position);
        intent.putExtra(EXTRA_ID, clickedItem.getCourseID());
        intent.putExtra(EXTRA_NAME, clickedItem.getCourseName());
        intent.putExtra(EXTRA_DESCRIPTION, clickedItem.getCourseDescription());
        intent.putExtra(EXTRA_IMAGE, clickedItem.getCourseImage());
        intent.putExtra(EXTRA_ENROLLS, clickedItem.getCourseEnrolls());
        intent.putExtra(EXTRA_DATE, clickedItem.getCourseCreationDate());
        CoursesListActivity.this.startActivity(intent);
    }


}