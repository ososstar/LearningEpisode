package com.android.ososstar.learningepisode.feedback;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.URLs;
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

import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_ADMIN_ID;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_FEEDBACK_ADMIN_COMMENT;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_FEEDBACK_ATTACHED_IMAGE;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_FEEDBACK_ID;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_FEEDBACK_STUDENT_COMMENT;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_FEEDBACK_TYPE;
import static com.android.ososstar.learningepisode.feedback.FeedbackAdapter.ViewHolder.EXTRA_STUDENT_ID;

public class FeedbackModifyActivity extends AppCompatActivity {

    private String admin_ID, student_ID, feedback_ID, feedback_typeChoice, feedback_stuComment, feedback_attached_image, feedback_admComment;

    //declaring progressBar
    private ProgressBar progressBar;

    //declare EditTexts of FeedbackInsertActivity
    private EditText eFeedback_stuComment, eFeedback_attachImage, eFeedback_admComment;

    //declare spinner of FeedbackInsertActivity
    private Spinner eFeedback_type_sp;

    //declaring button of FeedbackInsertActivity
    private Button eFeedback_insert_b;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_feedback);

        TextView modifyTitle_tv = findViewById(R.id.editor_feedback_title);
        modifyTitle_tv.setText(getString(R.string.modify_feedback));

        //get received values from bundle
        Bundle modifyBundle = getIntent().getExtras();
        if (modifyBundle.getString(EXTRA_ADMIN_ID) != null) {
            admin_ID = modifyBundle.getString(EXTRA_ADMIN_ID);
        }
        if (modifyBundle.getString(EXTRA_STUDENT_ID) != null) {
            student_ID = modifyBundle.getString(EXTRA_STUDENT_ID);
        }
        feedback_ID = modifyBundle.getString(EXTRA_FEEDBACK_ID);
        feedback_typeChoice = modifyBundle.getString(EXTRA_FEEDBACK_TYPE);
        feedback_stuComment = modifyBundle.getString(EXTRA_FEEDBACK_STUDENT_COMMENT);
        feedback_attached_image = modifyBundle.getString(EXTRA_FEEDBACK_ATTACHED_IMAGE);
        feedback_admComment = modifyBundle.getString(EXTRA_FEEDBACK_ADMIN_COMMENT);

        //defining EditTexts
        eFeedback_stuComment = findViewById(R.id.editor_feedback_stuComment_et);
        eFeedback_attachImage = findViewById(R.id.editor_feedback_AttachImgUrl_et);
        eFeedback_admComment = findViewById(R.id.editor_feedback_admComment_et);

        TextView admCommentTitleTV = findViewById(R.id.editor_feedback_admComment_tv);

        eFeedback_stuComment.setText(feedback_stuComment);
        if (!feedback_attached_image.matches("null"))
            eFeedback_attachImage.setText(feedback_attached_image);
        if (!feedback_admComment.matches("null")) eFeedback_admComment.setText(feedback_admComment);

        if (admin_ID == null) {
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

        switch (feedback_typeChoice) {
            case "0":
                eFeedback_type_sp.setSelection(1);
                break;
            case "1":
                eFeedback_type_sp.setSelection(2);
                break;
            case "2":
                eFeedback_type_sp.setSelection(3);
                break;
        }

        eFeedback_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //use position value
                switch (position) {
                    case 0:
                        //default unspecified
                        feedback_typeChoice = null;
                        break;
                    case 1:
                        //if "Suggestion" has been chosen
                        feedback_typeChoice = "0";
                        break;
                    case 2:
                        //if "Technical Issue" has been chosen
                        feedback_typeChoice = "1";
                        break;
                    case 3:
                        //if "Other" has been chosen
                        feedback_typeChoice = "2";
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
                if (ConnectivityHelper.isNetworkAvailable(FeedbackModifyActivity.this)) {
                    modifyFeedback();
                } else {
                    Toast.makeText(FeedbackModifyActivity.this, getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void modifyFeedback() {
        //getting user input values
        final String stuComment, attachImage, admComment;
        stuComment = eFeedback_stuComment.getText().toString().trim();
        attachImage = eFeedback_attachImage.getText().toString().trim();
        admComment = eFeedback_admComment.getText().toString().trim();

        //validating student Feedback Type Data
        if (feedback_typeChoice == null) {
            progressBar.setVisibility(View.GONE);
            eFeedback_type_sp.requestFocus();
            TextView errorText = (TextView) eFeedback_type_sp.getSelectedView();
            errorText.setError(getString(R.string.specify_the_right_answer));
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

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_MODIFY_FEEDBACK, new Response.Listener<String>() {
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
                if (admin_ID != null) pars.put("admin_ID", admin_ID);
                if (student_ID != null) pars.put("student_ID", student_ID);
                pars.put("feedback_ID", feedback_ID);
                pars.put("f_type", feedback_typeChoice);
                pars.put("f_student_comment", stuComment);
                if (!TextUtils.isEmpty(attachImage)) pars.put("f_image", attachImage);
                if (!TextUtils.isEmpty(admComment)) pars.put("f_admin_comment", admComment);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }


}
