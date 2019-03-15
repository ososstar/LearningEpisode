package com.android.ososstar.learningepisode.course;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.ososstar.learningepisode.lesson.LessonListActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_DATE;
import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_DESCRIPTION;
import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_ENROLLS;
import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_ID;
import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_IMAGE;
import static com.android.ososstar.learningepisode.course.CourseListActivity.EXTRA_NAME;

public class CourseActivity extends AppCompatActivity {

    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();

    public static final String EXTRA_COURSE_ID = "id";
    public static final String EXTRA_STUDENT_ID ="student_ID";
    public static final String EXTRA_ADMIN_ID ="admin_ID";
    public static final String EXTRA_COURSE_NAME ="course_name";

    private RequestQueue mRequestQueue;

    private String student_ID, admin_ID;

    boolean isEnrolled;

    private String id, name, description, image, enrolls, creation_date;

    private Button course_enroll_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        image = intent.getStringExtra(EXTRA_IMAGE);

        ImageView courseImage = findViewById(R.id.course_image);
        if (image != null && image.isEmpty()) {
            courseImage.setImageResource(R.drawable.defaultplaceholder);
        } else {
            Picasso.with(CourseActivity.this).load(image)
                    .placeholder(R.drawable.defaultplaceholder).error(R.drawable.defaultplaceholder)
                    .into(courseImage);
        }

        TextView course_enrolls_tv, course_date_tv, course_name_tv, course_description;
        course_enrolls_tv = findViewById(R.id.course_enrolls_tv);
        course_date_tv = findViewById(R.id.course_date_tv);
        course_name_tv = findViewById(R.id.course_name_tv);
        course_description = findViewById(R.id.course_description_tv);

        id = intent.getStringExtra(EXTRA_ID);

        name = intent.getStringExtra(EXTRA_NAME);
        course_name_tv.setText(name);

        description = intent.getStringExtra(EXTRA_DESCRIPTION);
        course_description.setText(description);

        enrolls = intent.getStringExtra(EXTRA_ENROLLS);
        course_enrolls_tv.setText("Total Enrolls " + enrolls);

        creation_date = intent.getStringExtra(EXTRA_DATE);
        course_date_tv.setText("Created On " + creation_date);

        setTitle(name);

        final Button course_lessons_b;
        course_enroll_b = findViewById(R.id.course_enroll_b);
        course_lessons_b = findViewById(R.id.course_lessons_b);

        mRequestQueue = Volley.newRequestQueue(this);

        switch (user.getType()) {
            case 0:
                course_enroll_b.setVisibility(View.GONE);
                admin_ID = String.valueOf(user.getID());
                break;
            case 1:
                course_enroll_b.setVisibility(View.INVISIBLE);
                if (ConnectivityHelper.isNetworkAvailable(this)) {
                    isEnrolledCheck();
                    student_ID = String.valueOf(user.getID());
                }else{
                    Toast.makeText(this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                }
                break;
        }

        course_enroll_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getType() == 1) {
                    if (ConnectivityHelper.isNetworkAvailable(CourseActivity.this)) {
                        if (!isEnrolled) {
                            makeEnroll();
                            course_enroll_b.setText(R.string.enrolled);
                        }else{
                            deleteEnroll();
                            course_enroll_b.setText(R.string.enroll);
                        }
                    }else {
                        Toast.makeText(CourseActivity.this, "please check your connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        course_lessons_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, LessonListActivity.class);

                intent.putExtra(EXTRA_COURSE_ID, id);
                switch (user.getType()){
                    case 0:
                        intent.putExtra(EXTRA_ADMIN_ID, admin_ID);
                        break;
                    case 1:
                        intent.putExtra(EXTRA_STUDENT_ID, student_ID);
                        break;
                }
                startActivity(intent);

            }
        });

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.no_description, Toast.LENGTH_SHORT).show();
    }

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvailable(CourseActivity.this)) {
            isEnrolledCheck();
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

    private void isEnrolledCheck(){

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_CHECK_STUDENT_ENROLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    isEnrolled = !baseJSONObject.getBoolean("error");
                    assignEnrollment();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                pars.put("student_ID", student_ID);
                pars.put("course_ID", id);
                return pars;
            }
        };

        mRequestQueue.add(request);
    }

    private void assignEnrollment(){
        if (isEnrolled) course_enroll_b.setText(R.string.enrolled);
        else course_enroll_b.setText(R.string.enroll);
        course_enroll_b.setVisibility(View.VISIBLE);
    }

    private void makeEnroll(){

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_INSERT_NEW_ENROLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    String message = baseJSONObject.getString("message");
                    Toast.makeText(CourseActivity.this, message, Toast.LENGTH_SHORT).show();
                    isEnrolledCheck();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPrefManager.getInstance(CourseActivity.this).setSSLStatus(1);
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
            protected Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("student_ID", student_ID);
                pars.put("course_ID", id);
                return pars;
            }
        };

        mRequestQueue.add(request);
    }

    private void deleteEnroll(){

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_ENROLL_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    String message = baseJSONObject.getString("message");
                    Toast.makeText(CourseActivity.this, message, Toast.LENGTH_SHORT).show();
                    isEnrolledCheck();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                switch (user.getType()){
                    case 0:
                        pars.put("admin_ID", admin_ID);
                        break;
                    case 1:
                        pars.put("student_ID", student_ID);
                        break;
                }
                pars.put("course_ID", id);
                return pars;
            }
        };

        mRequestQueue.add(request);
    }


}
