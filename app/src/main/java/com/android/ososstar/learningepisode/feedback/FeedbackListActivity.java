package com.android.ososstar.learningepisode.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedbackListActivity extends AppCompatActivity implements FeedbackAdapter.OnItemClickListener {

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
    private FeedbackAdapter mFeedbackAdapter;
    private ArrayList<Feedback> feedbackList;
    private RequestQueue mRequestQueue;

    //getting the current user
    private final User user = SharedPrefManager.getInstance(FeedbackListActivity.this).getUser();

    public static final String EXTRA_FEEDBACK_ID = "feedback_id";
    public static final String EXTRA_FEEDBACK_TYPE = "feedback_type";

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvailable(FeedbackListActivity.this)) {
            getFeedbackList();
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

    public static final String EXTRA_FEEDBACK_DATE = "feedback_date";

    private void getFeedbackList() {
        //making Volley String Request
        StringRequest request = new StringRequest(StringRequest.Method.POST, URLs.URL_REQUEST_FEEDBACK_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mEmptyStateTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                try {
                    //if no error in response
                    JSONObject baseJSONObject = new JSONObject(response);

                    feedbackList.clear();

                    if (!baseJSONObject.getBoolean("error")) {
                        //getting the lessons list from the response
                        JSONArray feedbackArray = baseJSONObject.getJSONArray("feedbackList");

                        // For each lesson in the lesson Array, create a {@link Lesson} object
                        for (int i = 0; i < feedbackArray.length(); i++) {
                            // Get a single lesson at position i within the list of lessonsList
                            JSONObject currentLesson = feedbackArray.getJSONObject(i);

                            // Extract the value for the key called "ID"
                            String id = currentLesson.getString("ID");

                            // Extract the value for the key called "f_type"
                            String type = currentLesson.getString("f_type");

                            // Extract the value for the key called "f_date"
                            String creationDate = currentLesson.getString("f_date");

                            // Extract the value for the key called "f_student_comment"
                            String student_comment = currentLesson.getString("f_student_comment");

                            // Extract the value for the key called "f_attached_photo"
                            String attached_image = currentLesson.getString("f_attached_image");

                            // Extract the value for the key called "f_admin_comment"
                            String admin_comment = currentLesson.getString("f_admin_comment");

                            // Extract the value for the key called "student_ID"
                            String studentID = currentLesson.getString("student_ID");

                            // Create a new {@link Feedback} object with the id, type, creationDate, student_comment, attached_photo, admin_comment, studentID from the JSON response.
                            Feedback feedback = new Feedback(id, type, creationDate, student_comment, attached_image, admin_comment, studentID);

                            // Add the new {@link Feedback} to the list of feedbackList.
                            feedbackList.add(feedback);
                        }

                        mFeedbackAdapter = new FeedbackAdapter(FeedbackListActivity.this, feedbackList);
                        mList.setAdapter(mFeedbackAdapter);
                        mFeedbackAdapter.notifyDataSetChanged();
                        mFeedbackAdapter.setOnItemClickListener(FeedbackListActivity.this);

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

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("admin_ID", admin_ID);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }

    public static final String EXTRA_FEEDBACK_STUDENT_COMMENT = "feedback_student_comment";
    public static final String EXTRA_FEEDBACK_STUDENT_ATTACHED_IMAGE = "feedback_attached_image";
    public static final String EXTRA_FEEDBACK_ADMIN_REPLY = "feedback_admin_reply";
    public static final String EXTRA_FEEDBACK_STUDENT_ID = "feedback_student_ID";
    private String admin_ID, student_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        switch (user.getType()) {
            case 0:
                admin_ID = String.valueOf(user.getID());
                break;
            case 1:
                student_ID = String.valueOf(user.getID());
                break;
        }

        setTitle(R.string.feedbackList);

        //define Empty State TextView
        mEmptyStateTextView = findViewById(R.id.l_empty_view);

        mList = findViewById(R.id.l_RV);

        feedbackList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FeedbackListActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        //defining progressBar that will show when making Http Request
        progressBar = findViewById(R.id.l_spinner);

        if (ConnectivityHelper.isNetworkAvailable(FeedbackListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            if (user.getType() == 0) getFeedbackList();
            if (user.getType() == 1) getStudentFeedbackList();

        } else {
            // Set empty state text to display "check your internet"
            connectASAP();
            mEmptyStateTextView.setText(R.string.noInternet);
        }

        //declare FloatingActionButton
        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);

        //show FAB for students
        if (user.getType() == 1) insertFAB.show();

        //set click listener for FAB
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to insert Feedback activity
                startActivityForResult(new Intent(FeedbackListActivity.this, FeedbackInsertActivity.class), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, FeedbackListActivity.class));
            finish();
        }
    }

    private void getStudentFeedbackList() {
        //making Volley String Request
        StringRequest request = new StringRequest(StringRequest.Method.POST, URLs.URL_REQUEST_STUDENT_FEEDBACK_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mEmptyStateTextView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                try {
                    //if no error in response
                    JSONObject baseJSONObject = new JSONObject(response);

                    feedbackList.clear();

                    if (!baseJSONObject.getBoolean("error")) {
                        //getting the lessons list from the response
                        JSONArray feedbackArray = baseJSONObject.getJSONArray("feedbackList");

                        // For each lesson in the lesson Array, create a {@link Lesson} object
                        for (int i = 0; i < feedbackArray.length(); i++) {
                            // Get a single lesson at position i within the list of lessonsList
                            JSONObject currentLesson = feedbackArray.getJSONObject(i);

                            // Extract the value for the key called "ID"
                            String id = currentLesson.getString("ID");

                            // Extract the value for the key called "f_type"
                            String type = currentLesson.getString("f_type");

                            // Extract the value for the key called "f_date"
                            String creationDate = currentLesson.getString("f_date");

                            // Extract the value for the key called "f_student_comment"
                            String student_comment = currentLesson.getString("f_student_comment");

                            // Extract the value for the key called "f_attached_photo"
                            String attached_image = currentLesson.getString("f_attached_image");

                            // Extract the value for the key called "f_admin_comment"
                            String admin_comment = currentLesson.getString("f_admin_comment");

                            // Extract the value for the key called "student_ID"
                            String studentID = currentLesson.getString("student_ID");

                            // Create a new {@link Feedback} object with the id, type, creationDate, student_comment, attached_photo, admin_comment, studentID from the JSON response.
                            Feedback feedback = new Feedback(id, type, creationDate, student_comment, attached_image, admin_comment, studentID);

                            // Add the new {@link Feedback} to the list of feedbackList.
                            feedbackList.add(feedback);
                        }

                        mFeedbackAdapter = new FeedbackAdapter(FeedbackListActivity.this, feedbackList);
                        mList.setAdapter(mFeedbackAdapter);
                        mFeedbackAdapter.notifyDataSetChanged();
                        mFeedbackAdapter.setOnItemClickListener(FeedbackListActivity.this);

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

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("student_ID", student_ID);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(FeedbackListActivity.this, FeedbackActivity.class);

        Feedback clickedItem = feedbackList.get(position);

        Bundle feedbackBundle = new Bundle();
        feedbackBundle.putString(EXTRA_FEEDBACK_ID, clickedItem.getID());
        feedbackBundle.putString(EXTRA_FEEDBACK_TYPE, clickedItem.getFeedbackType());
        feedbackBundle.putString(EXTRA_FEEDBACK_DATE, clickedItem.getDate());
        feedbackBundle.putString(EXTRA_FEEDBACK_STUDENT_COMMENT, clickedItem.getStudentComment());
        feedbackBundle.putString(EXTRA_FEEDBACK_STUDENT_ATTACHED_IMAGE, clickedItem.getStudentAttachedImage());
        feedbackBundle.putString(EXTRA_FEEDBACK_ADMIN_REPLY, clickedItem.getAdminReply());
        feedbackBundle.putString(EXTRA_FEEDBACK_STUDENT_ID, clickedItem.getStudentID());

        intent.putExtras(feedbackBundle);

        FeedbackListActivity.this.startActivity(intent);
    }

}
