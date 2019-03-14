package com.android.ososstar.learningepisode.question;

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
import android.widget.Toast;

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

import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_ADMIN_ID;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_ANSWER;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_CHOICE_1;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_CHOICE_2;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_CHOICE_3;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_ID;
import static com.android.ososstar.learningepisode.question.QuestionAdapter.ViewHolder.EXTRA_QUESTION_TITLE;

public class QuestionModifyActivity extends AppCompatActivity {

    private static final String TAG = "QuestionModifyActivity";
    private String admin_ID, question_ID, question_title, question_choice1, question_choice2, question_choice3, question_answer;

    //show progressBar spinner when sending data
    private ProgressBar progressBar;

    //declare the EditTexts to Modify a Question
    private EditText eQuestionTitle, eQuestionChoiceOne, eQuestionChoiceTwo, eQuestionChoiceThree;

    private Spinner answerSpinner;

    private Button insertButton;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_question);

        setTitle("Modify a Question");

        progressBar = findViewById(R.id.eQuestionProgressBar);

        //getting intent values
        Bundle modifyBundle = getIntent().getExtras();
        Log.d(TAG, "onCreate: " + modifyBundle);
        admin_ID = modifyBundle.getString(EXTRA_ADMIN_ID);
        question_ID = modifyBundle.getString(EXTRA_QUESTION_ID);
        question_title = modifyBundle.getString(EXTRA_QUESTION_TITLE);
        question_choice1 = modifyBundle.getString(EXTRA_QUESTION_CHOICE_1);
        question_choice2 = modifyBundle.getString(EXTRA_QUESTION_CHOICE_2);
        question_choice3 = modifyBundle.getString(EXTRA_QUESTION_CHOICE_3);
        question_answer = modifyBundle.getString(EXTRA_QUESTION_ANSWER);

        //define EditTexts to Modify a Question
        eQuestionTitle = findViewById(R.id.eQuestionTitle_et);
        eQuestionTitle.setText(question_title);
        eQuestionChoiceOne = findViewById(R.id.eQuestionChoiceOne_et);
        eQuestionChoiceOne.setText(question_choice1);
        eQuestionChoiceTwo = findViewById(R.id.eQuestionChoiceTwo_et);
        eQuestionChoiceTwo.setText(question_choice2);
        eQuestionChoiceThree = findViewById(R.id.eQuestionChoiceThree_et);
        eQuestionChoiceThree.setText(question_choice3);

        // Initializing a String Array
        String[] choices = new String[]{
                "unspecified",
                "Choice One",
                "Choice Two",
                "Choice Three"
        };

        // Initializing an ArrayAdapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, choices);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        answerSpinner = findViewById(R.id.eQuestionAnswer_sp);

        //Sets the Adapter used to provide the data which backs this Spinner.
        answerSpinner.setAdapter(spinnerAdapter);

        answerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //use position value
                switch (position) {
                    case 0:
                        question_answer = null;
                        break;
                    case 1:
                        question_answer = eQuestionChoiceOne.getText().toString().trim();
                        break;
                    case 2:
                        question_answer = eQuestionChoiceTwo.getText().toString().trim();
                        break;
                    case 3:
                        question_answer = eQuestionChoiceThree.getText().toString().trim();
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

        insertButton = findViewById(R.id.eQuestionInsert_b);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyQuestion();
            }
        });
    }

    private void modifyQuestion() {
        //getting user input values
        final String questionTitle = eQuestionTitle.getText().toString().trim();
        final String questionChoiceOne = eQuestionChoiceOne.getText().toString().trim();
        final String questionChoiceTwo = eQuestionChoiceTwo.getText().toString().trim();
        final String questionChoiceThree = eQuestionChoiceThree.getText().toString().trim();

        if (!TextUtils.isEmpty(questionTitle) || !TextUtils.isEmpty(questionChoiceOne) || !TextUtils.isEmpty(questionChoiceTwo) || !TextUtils.isEmpty(questionChoiceThree)) {
            //if there atleast one new modification start StringRequest

            // if the new video url is not youtube url
            if (TextUtils.isEmpty(question_answer) || question_answer == null) {
                Toast.makeText(this, "Please specify the question answer", Toast.LENGTH_SHORT).show();
                answerSpinner.requestFocus();
                return;
            }
            insertButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_MODIFY_QUESTION_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    insertButton.setEnabled(true);
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);

                        //if no error in response
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                            //start Activity CourseListActivity
                            setResult(RESULT_OK);
                            finish();

                        } else {
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
                    insertButton.setEnabled(true);
                    Toast.makeText(QuestionModifyActivity.this, getString(R.string.error_no_data_received), Toast.LENGTH_SHORT).show();
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
                    pars.put("admin_ID", admin_ID);
                    pars.put("question_ID", question_ID);
                    if (!TextUtils.isEmpty(questionTitle)) {
                        pars.put("q_title", questionTitle);
                    }
                    if (!TextUtils.isEmpty(questionChoiceOne)) {
                        pars.put("q_choice1", questionChoiceOne);
                    }
                    if (!TextUtils.isEmpty(questionChoiceTwo)) {
                        pars.put("q_choice2", questionChoiceTwo);
                    }
                    if (!TextUtils.isEmpty(questionChoiceThree)) {
                        pars.put("q_choice3", questionChoiceThree);
                    }
                    pars.put("q_answer", question_answer);
                    return pars;
                }
            };
            mRequestQueue.add(request);


        } else {
            insertButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error: please make any modification to process", Toast.LENGTH_SHORT).show();
        }

    }


}
