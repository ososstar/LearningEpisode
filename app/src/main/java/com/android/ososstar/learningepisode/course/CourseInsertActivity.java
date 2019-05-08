package com.android.ososstar.learningepisode.course;

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
import com.android.ososstar.learningepisode.account.LoginActivity;
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

public class CourseInsertActivity extends AppCompatActivity {

    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();
    private String admin_ID = String.valueOf(user.getID());

    private ProgressBar progressBar;

    //declare EditTexts of CourseInsertActivity
    private EditText icourse_name_et, icourse_description_et, icourse_imgurl_et;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_course);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn() && user.getType() != 0) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        progressBar = findViewById(R.id.icourse_spinner);

        //define EditTexts of CourseInsertActivity
        icourse_name_et = findViewById(R.id.editor_course_name_et);
        icourse_description_et = findViewById(R.id.editor_course_description_et);
        icourse_imgurl_et = findViewById(R.id.editor_course_image_et);

        //define the insert Button of CourseInsertActivity
        //declare the insert Button of CourseInsertActivity
        Button icourse_insert_b = findViewById(R.id.editor_course_insert_b);

        mRequestQueue = Volley.newRequestQueue(this);

        //if user presses on insert button, calling the method insert new course
        icourse_insert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isNetworkAvailable(CourseInsertActivity.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    insertNewCourse();
                }else {
                    Toast.makeText(CourseInsertActivity.this, getText(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void insertNewCourse(){
        //getting user values
        final String course_name = icourse_name_et.getText().toString().trim();
        final String course_description = icourse_description_et.getText().toString().trim();
        final String course_imageUrl = icourse_imgurl_et.getText().toString().trim();

        //validating inputs
        if (TextUtils.isEmpty(course_name)) {
            progressBar.setVisibility(View.GONE);
            icourse_name_et.setError("Please enter the course name");
            icourse_name_et.requestFocus();
            return;
        }

        //if everything is fine
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_INSERT_NEW_COURSE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    //if no error in response
                    if (!baseJSONObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                        //start Activity CourseListActivity
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
                pars.put("name", course_name);
                pars.put("description", course_description);
                pars.put("image", course_imageUrl);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }
}
