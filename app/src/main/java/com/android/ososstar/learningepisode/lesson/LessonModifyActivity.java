package com.android.ososstar.learningepisode.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LessonModifyActivity extends AppCompatActivity {

    private static final Pattern youtubePattern = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$");
    //declare EditTexts of CourseInsertActivity
    private EditText modifyLessonTitle_et, modifyLessonDescription_et, modifyLessonLink_et, modifyLessonVideoUrl_et;
    private Button modifyInsert_b;
    //show progressBar spinner when sending data
    private ProgressBar progressBar;
    private RequestQueue mRequestQueue;
    private String admin_ID, lesson_ID, course_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_lesson);

        TextView mLesson_tv = findViewById(R.id.editor_lesson_tv);
        mLesson_tv.setText("Modify Lesson Data");

        //get received values
        Bundle modifyBundle = getIntent().getExtras();
        if (modifyBundle != null) {
            if (modifyBundle.containsKey("admin_ID") && modifyBundle.containsKey("lesson_ID") && modifyBundle.containsKey("course_ID")) {
                admin_ID = modifyBundle.getString("admin_ID");
                lesson_ID = modifyBundle.getString("lesson_ID");
                course_ID = modifyBundle.getString("course_ID");
            }
        }

        //define the EditTexts to insert new lesson
        modifyLessonTitle_et = findViewById(R.id.editor_lesson_title_et);
        modifyLessonDescription_et = findViewById(R.id.editor_lesson_description_et);
        modifyLessonLink_et = findViewById(R.id.editor_lesson_link_et);
        modifyLessonVideoUrl_et = findViewById(R.id.editor_lesson_video_et);

        //define progressBar
        progressBar = findViewById(R.id.editor_lesson_progressBar);

        //set up a RequestQueue to handle the http request
        mRequestQueue = Volley.newRequestQueue(this);

        //declare and define the insert button
        modifyInsert_b = findViewById(R.id.editor_lesson_insert_b);
        //if the insert button is clicked, calling the method insert new lesson to send the values in the EditTexts to the database
        modifyInsert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isNetworkAvaliable(getBaseContext())) {
                    modifyInsert_b.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    modifyLessonRequest();
                } else {
                    Toast.makeText(LessonModifyActivity.this, getText(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void modifyLessonRequest() {
        final String lesson_title, lesson_description, lesson_link, lesson_videoURL;
        //getting user input values
        lesson_title = modifyLessonTitle_et.getText().toString().trim();
        lesson_description = modifyLessonDescription_et.getText().toString().trim();
        lesson_link = modifyLessonLink_et.getText().toString().trim();
        lesson_videoURL = modifyLessonVideoUrl_et.getText().toString().trim();

        //validating the input in lesson title
        if (!TextUtils.isEmpty(lesson_title) || !TextUtils.isEmpty(lesson_description) || !TextUtils.isEmpty(lesson_link) || !TextUtils.isEmpty(lesson_videoURL)) {
            // if the new video url is not youtube url
            if (!TextUtils.isEmpty(lesson_videoURL)) {
                boolean isValidYoutube = youtubePattern.matcher(lesson_videoURL).matches();
                if (!isValidYoutube) {
                    progressBar.setVisibility(View.GONE);
                    modifyLessonVideoUrl_et.setError("Please enter valid YouTube URL");
                    modifyLessonVideoUrl_et.requestFocus();
                    modifyInsert_b.setEnabled(true);
                    return;
                }
            }

            //continue

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_MODIFY_LESSON_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    modifyInsert_b.setEnabled(true);
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(LessonModifyActivity.this, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                            //finish this activity and start Activity LessonListActivity
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(LessonModifyActivity.this, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    modifyInsert_b.setEnabled(true);
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
                    pars.put("lesson_ID", lesson_ID);
                    pars.put("course_ID", course_ID);
                    if (!TextUtils.isEmpty(lesson_title)) {
                        pars.put("title", lesson_title);
                    }
                    if (!TextUtils.isEmpty(lesson_description)) {
                        pars.put("description", lesson_description);
                    }
                    if (!TextUtils.isEmpty(lesson_link)) {
                        pars.put("link", lesson_link);
                    }
                    if (!TextUtils.isEmpty(lesson_videoURL)) {
                        pars.put("video", lesson_videoURL);
                    }
                    return pars;
                }
            };
            mRequestQueue.add(request);

        } else {
            modifyInsert_b.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error: please make any modification to process", Toast.LENGTH_SHORT).show();
        }

    }


}
