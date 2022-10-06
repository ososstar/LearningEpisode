package com.android.ososstar.learningepisode.course;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class CourseModifyActivity extends AppCompatActivity {

    //declare EditTexts of CourseInsertActivity
    private EditText modifyCourseName_et, modifyCourseDescription_et, modifyCourseImgURL_et;

    private Button modifyInsert_b;

    private ProgressBar progressBar;

    private RequestQueue mRequestQueue;

    private String admin_ID, course_ID, course_name, course_description, course_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_course);

        //get received values
        Bundle modifyBundle = getIntent().getExtras();
        if (modifyBundle != null) {
            if (modifyBundle.containsKey("admin_ID") && modifyBundle.containsKey("course_ID")) {
                admin_ID = modifyBundle.getString("admin_ID");
                course_ID = modifyBundle.getString("course_ID");
                course_name = modifyBundle.getString("course_name");
                course_description = modifyBundle.getString("course_description");
                course_image = modifyBundle.getString("course_image");
            }
        }

        Log.d("CourseAdapter", "onCreate: " + "admin_ID=" + admin_ID + " course_ID=" + course_ID);

        TextView mCourse_tv = findViewById(R.id.editor_course_tv);
        mCourse_tv.setText("Modify Course Details");

        progressBar = findViewById(R.id.icourse_spinner);

        modifyCourseName_et = findViewById(R.id.editor_course_name_et);
        modifyCourseDescription_et = findViewById(R.id.editor_course_description_et);
        modifyCourseImgURL_et = findViewById(R.id.editor_course_image_et);

        modifyCourseName_et.setText(course_name);
        if (!course_description.matches("null"))
            modifyCourseDescription_et.setText(course_description);
        if (!course_image.matches("null")) modifyCourseImgURL_et.setText(course_image);

        mRequestQueue = Volley.newRequestQueue(this);

        modifyInsert_b = findViewById(R.id.editor_course_insert_b);
        modifyInsert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if is connected start modify course request
                if (ConnectivityHelper.isNetworkAvailable(getBaseContext())) {
                    modifyCourseRequest();
                } else {
                    Toast.makeText(CourseModifyActivity.this, "Please Check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void modifyCourseRequest() {
        final String course_name, course_description, course_image;
        course_name = modifyCourseName_et.getText().toString().trim();
        course_description = modifyCourseDescription_et.getText().toString().trim();
        course_image = modifyCourseImgURL_et.getText().toString().trim();

        //declare strings to get values from the EditTexts

        //validating inputs
        if (TextUtils.isEmpty(this.course_name)) {
            progressBar.setVisibility(View.GONE);
            modifyCourseName_et.setError("Please specify the course name");
            modifyCourseName_et.requestFocus();
            return;
        }

        Log.d("fares", "modifyCourseRequest: " + admin_ID + course_ID + course_name + course_description + course_image);

        //check if there is any value in these strings
        //continue
        modifyInsert_b.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_MODIFY_COURSE_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                modifyInsert_b.setEnabled(true);
                try {
                    JSONObject baseJSONObject = new JSONObject(response);
                    if (baseJSONObject.getBoolean("error")) {
                        Toast.makeText(CourseModifyActivity.this, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CourseModifyActivity.this, baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        //finish this activity and start Activity CourseListActivity
                        setResult(RESULT_OK);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                setResult(RESULT_OK);
//                finish();

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
                pars.put("course_ID", course_ID);
                if (!TextUtils.isEmpty(modifyCourseName_et.getText().toString().trim()))
                    pars.put("name", course_name);
                    pars.put("description", course_description);
                    pars.put("image", course_image);
                return pars;
            }
        };
        mRequestQueue.add(request);

    }

}