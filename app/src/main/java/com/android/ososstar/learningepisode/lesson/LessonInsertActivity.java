package com.android.ososstar.learningepisode.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
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

    //show progressBar spinner when sending data
    private ProgressBar progressBar;

    //declare the EditTexts to insert new lesson
    private EditText insertLessonTitle_et, insertLessonDescription_et, insertLessonLink_et, insertLessonVideoUrl_et;

    private String admin_ID, course_ID;

    //declare RequestQueue to make http connection
    private RequestQueue mRequestQueue;

    private static final Pattern youtubePattern = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_lesson);

        //define progressBar
        progressBar = findViewById(R.id.editor_lesson_progressBar);

        //check user identity, only if admin take his id value
        if (user.getType() == 0){
            admin_ID = String.valueOf(user.getID());
        }

        //define the EditTexts to insert new lesson
        insertLessonTitle_et = findViewById(R.id.editor_lesson_title_et);
        insertLessonDescription_et = findViewById(R.id.editor_lesson_description_et);
        insertLessonLink_et = findViewById(R.id.editor_lesson_link_et);
        insertLessonVideoUrl_et = findViewById(R.id.editor_lesson_video_et);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        course_ID = intent.getStringExtra(COURSE_ID);

        //set up a RequestQueue to handle the http request
        mRequestQueue = Volley.newRequestQueue(this);

        //declare and define the insert button
        Button insertLessonInsert_b = findViewById(R.id.editor_lesson_insert_b);
        //if the insert button is clicked, calling the method insert new lesson to send the values in the EditTexts to the database
        insertLessonInsert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isNetworkAvailable(getBaseContext())) {
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
        final String lesson_title = insertLessonTitle_et.getText().toString().trim();
        final String lesson_description = insertLessonDescription_et.getText().toString().trim();
        final String lesson_link = insertLessonLink_et.getText().toString().trim();
        final String lesson_videoURL = insertLessonVideoUrl_et.getText().toString().trim();

        //validating the input in lesson title
        if (TextUtils.isEmpty(lesson_title)) {
            progressBar.setVisibility(View.GONE);
            insertLessonTitle_et.setError("Please enter the Lesson Title");
            insertLessonTitle_et.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lesson_videoURL)) {
//            boolean isValidYoutube = youtubePattern.matcher(lesson_videoURL).matches();
//            if (!isValidYoutube){
            progressBar.setVisibility(View.GONE);
            insertLessonVideoUrl_et.setError("Please enter valid video URL");
            insertLessonVideoUrl_et.requestFocus();
            return;
//                return;
//            }
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
                        //start Activity LessonListActivity
                        setResult(RESULT_OK);
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
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
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
