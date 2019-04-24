package com.android.ososstar.learningepisode.lesson;

import android.content.Intent;
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

import com.android.ososstar.learningepisode.ConnectivityHelper;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.ososstar.learningepisode.course.CourseActivity.EXTRA_COURSE_ID;

public class LessonListActivity extends AppCompatActivity implements LessonAdapter.OnItemClickListener {

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * progressBar that is displayed when the list is loading
     */
    private ProgressBar progressBar;

    /**
     * RecyclerView, Adapter, and Volley Method to show Enrollment data into list
     */
    private RecyclerView mList;
    private LessonAdapter mLessonAdapter;
    private ArrayList<Lesson> lessonList;
    private RequestQueue mRequestQueue;

    //getting the current user
    private User user = SharedPrefManager.getInstance(LessonListActivity.this).getUser();

    private String course_ID, admin_ID, student_ID;

    public static final String EXTRA_LESSON_ID = "id";
    public static final String EXTRA_LESSON_TITLE = "title";
    public static final String EXTRA_LESSON_DESCRIPTION = "description";
    public static final String EXTRA_LESSON_LINK = "link";
    public static final String EXTRA_LESSON_VIDEO = "videoURL";
    public static final String EXTRA_LESSON_DATE = "date";
    public static final String COURSE_ID = "course_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (user.getType() == 1){
            student_ID = String.valueOf(user.getID());
        }
        else {
            admin_ID = String.valueOf(user.getID());
        }

        setTitle(R.string.lessons_list);

        Intent intent = getIntent();
        //getting values of course, admin or student IDs
        course_ID = intent.getStringExtra(EXTRA_COURSE_ID);

        mEmptyStateTextView = findViewById(R.id.l_empty_view);

        mList = findViewById(R.id.l_RV);

        lessonList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        progressBar = findViewById(R.id.l_spinner);

        if (ConnectivityHelper.isNetworkAvailable(LessonListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            getCourseLessons();

        }else {
            // Set empty state text to display "check your internet"
            connectASAP();
            mEmptyStateTextView.setText(R.string.noInternet);
        }

        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);
        //show FAB for admin
        if (user.getType() == 0) insertFAB.show();
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to insert course activity
                Intent insertLessonIntent = new Intent(LessonListActivity.this, LessonInsertActivity.class);
                insertLessonIntent.putExtra(COURSE_ID, course_ID);
                startActivityForResult(insertLessonIntent, 10);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getCourseLessons();
        }
    }

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvailable(LessonListActivity.this)) {
            getCourseLessons();
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

    private void getCourseLessons(){
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REQUEST_COURSE_LESSONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mEmptyStateTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                try {
                    //if no error in response
                    JSONObject baseJSONObject = new JSONObject(response);

                    lessonList.clear();

                    if (!baseJSONObject.getBoolean("error")) {
                        //getting the lessons list from the response
                        JSONArray lessonArray = baseJSONObject.getJSONArray("lessons");

                        // For each lesson in the lesson Array, create a {@link Lesson} object
                        for (int i = 0; i < lessonArray.length(); i++) {
                            // Get a single lesson at position i within the list of lessonsList
                            JSONObject currentLesson = lessonArray.getJSONObject(i);

                            // Extract the value for the key called "ID"
                            String id = currentLesson.getString("ID");

                            // Extract the value for the key called "c_name"
                            String title = currentLesson.getString("l_title");

                            // Extract the value for the key called "l_description"
                            String description = currentLesson.getString("l_description");

                            // Extract the value for the key called "c_description"
                            String link = currentLesson.getString("l_link");

                            // Extract the value for the key called "c_image"
                            String video = currentLesson.getString("l_video");

                            // Extract the value for the key called "c_enrolls"
                            String creationDate = currentLesson.getString("creation_date");

                            // Extract the value for the key called "c_enrolls"
                            String courseID = currentLesson.getString("course_ID");

                            // Create a new {@link Lesson} object with the ID, Name, Description
                            // , Image, enrolls and creation Date from the JSON response.
                            Lesson lesson = new Lesson(id, title, description, link, video, creationDate, courseID);

                            // Add the new {@link Lesson} to the list of lessonList.
                            lessonList.add(lesson);
                        }

                        mLessonAdapter = new LessonAdapter(LessonListActivity.this, lessonList);
                        mList.setAdapter(mLessonAdapter);
                        mLessonAdapter.notifyDataSetChanged();
                        mLessonAdapter.setOnItemClickListener(LessonListActivity.this);

                    } else {
                        // Set empty state text to display "No earthquakes found."
                        mEmptyStateTextView.setText(baseJSONObject.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                connectASAP();
                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                mEmptyStateTextView.setText(R.string.error_no_data_received);
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
                Map<String, String> pars = new HashMap<>();
                pars.put("course_ID", course_ID);
                switch (user.getType()){
                    case 0:
                        pars.put("admin_ID", admin_ID);
                        break;
                    case 1:
                        pars.put("student_ID", student_ID);
                        break;
                }

                return pars;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position){

        Intent intent = new Intent(LessonListActivity.this, LessonActivity.class);

        Lesson clickedItem = lessonList.get(position);

        Bundle lessonBundle = new Bundle();
        lessonBundle.putString(EXTRA_LESSON_ID, clickedItem.getID());
        lessonBundle.putString(EXTRA_LESSON_TITLE, clickedItem.getTitle());
        lessonBundle.putString(EXTRA_LESSON_DESCRIPTION, clickedItem.getDescription());
        lessonBundle.putString(EXTRA_LESSON_LINK, clickedItem.getLink());
        lessonBundle.putString(EXTRA_LESSON_VIDEO, clickedItem.getVideoURL());
        lessonBundle.putString(EXTRA_LESSON_DATE, clickedItem.getCreationDate());

        intent.putExtras(lessonBundle);

        LessonListActivity.this.startActivity(intent);

    }

}
