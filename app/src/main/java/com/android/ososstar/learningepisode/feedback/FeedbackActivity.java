package com.android.ososstar.learningepisode.feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_ADMIN_REPLY;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_DATE;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_ID;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_STUDENT_ATTACHED_IMAGE;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_STUDENT_COMMENT;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_STUDENT_ID;
import static com.android.ososstar.learningepisode.feedback.FeedbackListActivity.EXTRA_FEEDBACK_TYPE;

public class FeedbackActivity extends AppCompatActivity {

    //setup request queue to get student name
    RequestQueue mRequestQueue;
    private String id, type, date, student_comment, attached_image, admin_comment, student_ID, student_name;
    private TextView feedbackStudentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //get feedback values
        Bundle feedbackBundle = getIntent().getExtras();
        id = feedbackBundle.getString(EXTRA_FEEDBACK_ID);
        type = feedbackBundle.getString(EXTRA_FEEDBACK_TYPE);
        date = feedbackBundle.getString(EXTRA_FEEDBACK_DATE);
        student_comment = feedbackBundle.getString(EXTRA_FEEDBACK_STUDENT_COMMENT);
        attached_image = feedbackBundle.getString(EXTRA_FEEDBACK_STUDENT_ATTACHED_IMAGE);
        admin_comment = feedbackBundle.getString(EXTRA_FEEDBACK_ADMIN_REPLY);
        student_ID = feedbackBundle.getString(EXTRA_FEEDBACK_STUDENT_ID);

        //declaring TextViews of Feedback Activity
        TextView feedbackIDTV, feedbackDateTV, feedbackTypeTV, feedbackStudentCommentTV, feedbackAdminCommentTitle, feedbackAdminComment;
        feedbackIDTV = findViewById(R.id.feedback_id_tv);
        feedbackDateTV = findViewById(R.id.feedback_date_tv);
        feedbackTypeTV = findViewById(R.id.feedback_type);
        feedbackStudentCommentTV = findViewById(R.id.feedback_stuComment);
        feedbackAdminCommentTitle = findViewById(R.id.feedback_admCommentTitle);
        feedbackAdminComment = findViewById(R.id.feedback_admComment);

        //defining TextViews of Feedback Activity
        StringBuilder idBuilder = new StringBuilder(getString(R.string.idHash));
        idBuilder.append(id);
        feedbackIDTV.setText(idBuilder);

//        StringBuilder dateBuilder = new StringBuilder(getString(R.string.created_on));
//        dateBuilder.append(date);
//        feedbackDateTV.setText(dateBuilder);

        StringBuilder creationDateSB = new StringBuilder(getString(R.string.created_on));
        if (Locale.getDefault().getLanguage().equals("ar")) {
            Locale localeAR = new Locale("ar");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");
            Date date3 = null;
            try {
                date3 = sdf.parse(date);
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("d MMMM yyyy", localeAR);
            String format = sdf.format(date3);
            Log.wtf("result", format);
            creationDateSB.append(format);
            feedbackDateTV.setText(creationDateSB);
        } else {
            Locale localeAR = new Locale("en");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");
            Date date3 = null;
            try {
                date3 = sdf.parse(date);
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("d MMMM yyyy", localeAR);
            String format = sdf.format(date3);
            Log.wtf("result", format);
            creationDateSB.append(format);
            feedbackDateTV.setText(creationDateSB);
        }


        StringBuilder typeBuilder = new StringBuilder("#");

        switch (type) {
            case "0":
                typeBuilder.append("Suggestion");
                break;
            case "1":
                typeBuilder.append("Technical Issue");
                break;
            case "2":
                typeBuilder.append("Other");
                break;
        }

        feedbackTypeTV.setText(typeBuilder);

        feedbackStudentCommentTV.setText(student_comment);

        Log.d("fares", "onCreate: " + admin_comment);
        //determines of admin added reply or not
        if (!TextUtils.isEmpty(admin_comment)) {
            feedbackAdminComment.setText(admin_comment);
            feedbackAdminCommentTitle.setVisibility(View.VISIBLE);
            feedbackAdminComment.setVisibility(View.VISIBLE);
        } else {
            feedbackAdminCommentTitle.setVisibility(View.GONE);
            feedbackAdminComment.setVisibility(View.GONE);
        }

        mRequestQueue = Volley.newRequestQueue(this);
        feedbackStudentName = findViewById(R.id.feedback_stu_name);
        if (ConnectivityHelper.isNetworkAvailable(FeedbackActivity.this)) {
            getStudentName();
        }

        //declaring ImageView for the attached image, hide ImageView if no attached image url
        ImageView attachedImage_IV = findViewById(R.id.feedback_attached_image);
        if (!attached_image.matches("null")) {
            if (!TextUtils.isEmpty(attached_image)) {
                Picasso.with(FeedbackActivity.this).load(attached_image).placeholder(R.drawable.defaultplaceholder).error(R.drawable.defaultplaceholder).noFade().into(attachedImage_IV);
            }
        } else {
            attachedImage_IV.setVisibility(View.GONE);
        }

    }

    private void getStudentName() {

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REQUEST_STUDENT_NAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject baseJSONObject = new JSONObject(response);
                    if (!baseJSONObject.getBoolean("error")) {
                        student_name = baseJSONObject.getString("name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringBuilder nameBuilder = new StringBuilder(getString(R.string.feedback_by));
                nameBuilder.append(student_name);
                feedbackStudentName.setText(nameBuilder);
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
            protected Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("student_ID", student_ID);
                return pars;
            }
        };
        mRequestQueue.add(request);


    }

}
