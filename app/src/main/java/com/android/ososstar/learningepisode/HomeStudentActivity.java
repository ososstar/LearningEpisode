package com.android.ososstar.learningepisode;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.account.LoginActivity;
import com.android.ososstar.learningepisode.account.User;
import com.android.ososstar.learningepisode.course.Course;
import com.android.ososstar.learningepisode.course.CourseActivity;
import com.android.ososstar.learningepisode.course.CourseAdapter;
import com.android.ososstar.learningepisode.course.CourseListActivity;
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
import java.util.HashMap;
import java.util.Map;

public class HomeStudentActivity extends AppCompatActivity implements CourseAdapter.OnItemClickListener, OptionAdapter.OnItemClickListener {

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * RecyclerView, Adapter, and Volley Method to show Enrollment data into list
     */
    private RecyclerView mList;
    private CourseAdapter mCourseAdapter;
    private ArrayList<Course> enrollmentList;
    private RequestQueue mRequestQueue;

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_ENROLLS = "enrolls";
    public static final String EXTRA_DATE = "date";

    //getting the current user
    private User user = SharedPrefManager.getInstance(HomeStudentActivity.this).getUser();
    String student_ID = String.valueOf(user.getID());

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
        setContentView(R.layout.activity_home_student);
        setTitle("Home Page");

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //greeting user in home activity
        TextView homeUser = findViewById(R.id.home_user);
        homeUser.setText("Welcome, " + user.getName());

        // Create a list of user Options
        final ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("Courses",R.drawable.books));
        options.add(new Option("Profile",R.drawable.profile));
        options.add(new Option("Feedback",R.drawable.feedback));

        // Create an {@link OptionAdapter}, whose data source is a list of {@link Option}s. The
        // adapter knows how to create list items for each item in the list.
        RecyclerView home_rv_options = findViewById(R.id.stuhome_rv_options);
        home_rv_options.setHasFixedSize(true);
        LinearLayoutPagerManager layoutManager = new LinearLayoutPagerManager(this, LinearLayoutManager.HORIZONTAL, false, 3);
        home_rv_options.setLayoutManager(layoutManager);
        OptionAdapter adapter = new OptionAdapter(this, options);
        // Make the {@link GridView} use the {@link OptionAdapter} we created above, so that the
        // {@link GridView} will display list items for each {@link Option} in the list.
        home_rv_options.setAdapter(adapter);
        adapter.setOnItemClickListener(HomeStudentActivity.this);

        mEmptyStateTextView = findViewById(R.id.stu_home_empty_view);

        mList = findViewById(R.id.stu_home_RecyclerView);

        enrollmentList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        if (isConnected(HomeStudentActivity.this)) {
            getStudentEnrollment();
        } else {
            Toast.makeText(HomeStudentActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.noInternet);
            connectASAP();
        }

    }

    private void connectASAP() {
        if (isConnected(HomeStudentActivity.this)) {
            getStudentEnrollment();
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

    private void getStudentEnrollment(){

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REQUEST_STUDENT_ENROLLS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mEmptyStateTextView.setVisibility(View.GONE);
                if (response != null) {
                    try {
                        //if no error in response
                        JSONObject baseJSONObject = new JSONObject(response);

                        if (!baseJSONObject.getBoolean("error")) {
                            //getting the courses list from the response
                            JSONArray courseArray = baseJSONObject.getJSONArray("enrolls");

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
                                enrollmentList.add(course);
                            }

                            mCourseAdapter = new CourseAdapter(HomeStudentActivity.this, enrollmentList);
                            mList.setAdapter(mCourseAdapter);
                            mCourseAdapter.notifyDataSetChanged();
                            mCourseAdapter.setOnItemClickListener(HomeStudentActivity.this);

                        } else {
                            // Set empty state text to display "No earthquakes found."
                            mEmptyStateTextView.setText(R.string.no_course);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Set empty state text to display "No earthquakes found."
                    mEmptyStateTextView.setText("No Data Received");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setText(R.string.noInternet);
                connectASAP();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("student_ID", student_ID);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_add options from the res/menu_add/menu_home.xmll file.
        // This adds menu_add items to the app bar.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu_add option in the app bar overflow menu_add
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu_add option
            case R.id.menu_logout:
                finish();
                SharedPrefManager.getInstance(getBaseContext()).logout();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position){
        Intent intent = new Intent(this, CourseActivity.class);

        Course clickedItem = enrollmentList.get(position);

        intent.putExtra(EXTRA_ID, clickedItem.getCourseID());
        intent.putExtra(EXTRA_NAME, clickedItem.getCourseName());
        intent.putExtra(EXTRA_DESCRIPTION, clickedItem.getCourseDescription());
        intent.putExtra(EXTRA_IMAGE, clickedItem.getCourseImage());
        intent.putExtra(EXTRA_ENROLLS, clickedItem.getCourseEnrolls());
        intent.putExtra(EXTRA_DATE, clickedItem.getCourseCreationDate());

        this.startActivity(intent);
    }

    @Override
    public void onItemClickOptions(int position){

        switch (position){
            case 0:
                startActivity(new Intent(getBaseContext(), CourseListActivity.class));
                break;
            case 1:
                Toast.makeText(this, "Profile Activity is under construction", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Feedback Activity is under construction", Toast.LENGTH_SHORT).show();
                break;
        }

    }


}
