package com.android.ososstar.learningepisode.lesson;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.ososstar.learningepisode.course.CourseInsertActivity;
import com.android.ososstar.learningepisode.course.CoursesListActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.android.ososstar.learningepisode.lesson.LessonListActivity.COURSE_ID;

public class LessonInsertActivity extends AppCompatActivity {

    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();
    String admin_ID;

    //show progressBar spinner when sending data
    private ProgressBar progressBar;

    //declare the EditTexts to insert new lesson
    private EditText iLessonTitle_et, iLessonDescription_et, iLessonLink_et, iLessonVideoUrl_et;

    private String course_ID;

    //declare RequestQueue to make http connection
    private RequestQueue mRequestQueue;

    private static final Pattern youtubePattern = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$");

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
        setContentView(R.layout.activity_editor_lesson);

        //define spinner
        progressBar = findViewById(R.id.ilesson_spinner);

        //check user identity, only if admin take his id value
        if (user.getType() == 0){
            admin_ID = String.valueOf(user.getID());
        }

        //define the EditTexts to insert new lesson
        iLessonTitle_et = findViewById(R.id.ilesson_title_et);
        iLessonDescription_et = findViewById(R.id.ilesson_description_et);
        iLessonLink_et = findViewById(R.id.ilesson_link_et);
        iLessonVideoUrl_et = findViewById(R.id.ilesson_video_et);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        course_ID = intent.getStringExtra(COURSE_ID);

        //set up a RequestQueue to handle the http request
        mRequestQueue = Volley.newRequestQueue(this);

        //define the insert button
        //declare the insert new lesson button
        Button iLessonInsert_b = findViewById(R.id.ilesson_insert_b);
        //if the insert button is clicked, calling the method insert new lesson to send the values in the EditTexts to the database
        iLessonInsert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getBaseContext())) {
                    progressBar.setVisibility(View.VISIBLE);
                    insertNewLesson();
                } else {
                    Toast.makeText(LessonInsertActivity.this, getText(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void insertNewLesson(){

        //getting user input values
        final String lesson_title = iLessonTitle_et.getText().toString().trim();
        final String lesson_description = iLessonDescription_et.getText().toString().trim();
        final String lesson_link = iLessonLink_et.getText().toString().trim();
        final String lesson_videoURL = iLessonVideoUrl_et.getText().toString().trim();

        //validating the input in lesson title
        if (TextUtils.isEmpty(lesson_title)) {
            progressBar.setVisibility(View.GONE);
            iLessonTitle_et.setError("Please enter the Lesson Title");
            iLessonTitle_et.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(lesson_videoURL)){
            boolean isValidYoutube = youtubePattern.matcher(lesson_videoURL).matches();
            if (!isValidYoutube){
                progressBar.setVisibility(View.GONE);
                iLessonVideoUrl_et.setError("Please enter valid YouTube URL");
                iLessonVideoUrl_et.requestFocus();
                return;
            }
        }

        //if everything is fine start StringRequest
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_INSERT_NEW_LESSON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    //if no error in response
                    if (!baseJSONObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                        //start Activity CoursesListActivity
                        setResult(RESULT_OK, new Intent());
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
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
                error.printStackTrace();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("admin_ID", admin_ID);
                pars.put("title", lesson_title);
                pars.put("course_ID", course_ID);
                if (!TextUtils.isEmpty(lesson_description)){
                    pars.put("description", lesson_description);
                }
                if (!TextUtils.isEmpty(lesson_link)){
                    pars.put("link", lesson_link);
                }
                if (!TextUtils.isEmpty(lesson_videoURL)){
                    pars.put("video", lesson_videoURL);
                }
                return pars;
            }
        };

        mRequestQueue.add(request);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.no_description, Toast.LENGTH_SHORT).show();
    }

}
