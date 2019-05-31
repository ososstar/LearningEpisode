package com.android.ososstar.learningepisode.feedback;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class FeedbackInsertActivity extends AppCompatActivity {

    int student_ID, admin_ID;
    //getting the current user
    private User user = SharedPrefManager.getInstance(this).getUser();
    //declaring progressBar
    private ProgressBar progressBar;

    //declare EditTexts of FeedbackInsertActivity
    private EditText eFeedback_stuComment, eFeedback_attachImage, eFeedback_admComment;

    //declare spinner of FeedbackInsertActivity
    private Spinner eFeedback_type_sp;

    //declaring button of FeedbackInsertActivity
    private Button eFeedback_insert_b;

    private String typeChoice;

    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_feedback);

        //getting user id
        if (user.getType() == 1) {
            student_ID = user.getID();
        }
        if (user.getType() == 0) {
            admin_ID = user.getID();
        }

        //defining EditTexts
        eFeedback_stuComment = findViewById(R.id.editor_feedback_stuComment_et);
        eFeedback_attachImage = findViewById(R.id.editor_feedback_AttachImgUrl_et);
        eFeedback_admComment = findViewById(R.id.editor_feedback_admComment_et);

        TextView admCommentTitleTV = findViewById(R.id.editor_feedback_admComment_tv);

        if (user.getType() == 1) {
            eFeedback_admComment.setVisibility(View.GONE);
            admCommentTitleTV.setVisibility(View.GONE);
        }

        //define type spinner
        eFeedback_type_sp = findViewById(R.id.editor_feedback_type_sp);

        // Initializing a String Array
        String[] choices = new String[]{
                "unspecified",
                "Suggestion",
                "Technical Issue",
                "Other"
        };

        // Initializing an ArrayAdapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, choices);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Sets the Adapter used to provide the data which backs this Spinner.
        eFeedback_type_sp.setAdapter(spinnerAdapter);

        eFeedback_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //use position value
                switch (position) {
                    case 0:
                        //default unspecified
                        typeChoice = null;
                        break;
                    case 1:
                        //if "Suggestion" has been chosen
                        typeChoice = "0";
                        break;
                    case 2:
                        //if "Technical Issue" has been chosen
                        typeChoice = "1";
                        break;
                    case 3:
                        //if "Other" has been chosen
                        typeChoice = "2";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }

        });

        //set up a RequestQueue to handle the http request
        mRequestQueue = Volley.newRequestQueue(this);

        //defining ProgressBar
        progressBar = findViewById(R.id.editor_feedback_progressbar);

        //define insert Button
        eFeedback_insert_b = findViewById(R.id.editor_feedback_insert_b);

        eFeedback_insert_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertNewFeedback();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }


    private void insertNewFeedback() {
        //getting user input values
        final String stuComment, attachImage, admComment;
        stuComment = eFeedback_stuComment.getText().toString().trim();
        attachImage = eFeedback_attachImage.getText().toString().trim();
        admComment = eFeedback_admComment.getText().toString().trim();

        Log.d("fares", "insertNewFeedback: " + student_ID + typeChoice + stuComment + attachImage + admComment);

        //validating student Feedback Type Data
        if (typeChoice == null) {
            progressBar.setVisibility(View.GONE);
            eFeedback_type_sp.requestFocus();
            TextView errorText = (TextView) eFeedback_type_sp.getSelectedView();
            errorText.setError(getString(R.string.unspecified));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            eFeedback_insert_b.setEnabled(true);
            return;
        }

        //validating student comment data
        if (TextUtils.isEmpty(stuComment)) {
            eFeedback_stuComment.setError("Please make a comment");
            eFeedback_stuComment.requestFocus();
            return;
        }

        //validating image URL if inserted
        if (!TextUtils.isEmpty(attachImage)) {
            Pattern imageUrlPattern = Pattern.compile("(?:([^:/?#]+):)?(?://([^/?#]*))?([^?#]*\\.(?:jpg|gif|png))(?:\\?([^#]*))?(?:#(.*))?");
            if (!imageUrlPattern.matcher(attachImage).matches()) {
                eFeedback_attachImage.setError("Please Insert an Image With PNG, JPG, GIF Extension");
                eFeedback_attachImage.requestFocus();
                return;
            }
        }

        //if everything is fine start StringRequest
        eFeedback_insert_b.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_INSERT_NEW_FEEDBACK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    //if no error in response
                    if (!baseJSONObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                        //start Activity CourseListActivity
                        setResult(RESULT_OK, new Intent());
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        eFeedback_insert_b.setEnabled(true);
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
                eFeedback_insert_b.setEnabled(true);
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
                pars.put("student_ID", String.valueOf(student_ID));
                pars.put("f_type", typeChoice);
                pars.put("f_student_comment", stuComment);
                if (!TextUtils.isEmpty(attachImage)) pars.put("f_image", attachImage);
                if (!TextUtils.isEmpty(admComment)) pars.put("f_admin_comment", admComment);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }

}
