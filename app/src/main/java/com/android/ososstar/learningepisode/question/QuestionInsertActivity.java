package com.android.ososstar.learningepisode.question;

import static com.android.ososstar.learningepisode.question.QuestionListActivity.EXTRA_ADMIN_ID;
import static com.android.ososstar.learningepisode.question.QuestionListActivity.EXTRA_LESSON_ID;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

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

public class QuestionInsertActivity extends AppCompatActivity {

    private String answer, admin_ID, lesson_ID;

    //show progressBar spinner when sending data
    private ProgressBar progressBar;

    //declare the EditTexts to insert new Question
    private EditText eQuestionTitle, eQuestionChoiceOne, eQuestionChoiceTwo, eQuestionChoiceThree;

    private Spinner answerSpinner;

    private Button insertButton;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_question);

        progressBar = findViewById(R.id.eQuestionProgressBar);

        Intent getIntent = getIntent();
        admin_ID = getIntent.getStringExtra(EXTRA_ADMIN_ID);
        lesson_ID = getIntent.getStringExtra(EXTRA_LESSON_ID);

        //define the EditTexts to insert new Question
        eQuestionTitle = findViewById(R.id.eQuestionTitle_et);
        eQuestionChoiceOne = findViewById(R.id.eQuestionChoiceOne_et);
        eQuestionChoiceTwo = findViewById(R.id.eQuestionChoiceTwo_et);
        eQuestionChoiceThree = findViewById(R.id.eQuestionChoiceThree_et);

        // Get reference of widgets from XML layout
        answerSpinner = findViewById(R.id.eQuestionAnswer_sp);

        // Initializing a String Array
        String[] choices = new String[]{
                getString(R.string.unspecified),
                getString(R.string.choiceOne),
                getString(R.string.choiceTwo),
                getString(R.string.choiceThree)
        };

        // Initializing an ArrayAdapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, choices);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Sets the Adapter used to provide the data which backs this Spinner.
        answerSpinner.setAdapter(spinnerAdapter);

        answerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //use position value
                switch (position){
                    case 0:
                        answer = null;
                        break;
                    case 1:
                        answer = eQuestionChoiceOne.getText().toString().trim();
                        break;
                    case 2:
                        answer = eQuestionChoiceTwo.getText().toString().trim();
                        break;
                    case 3:
                        answer = eQuestionChoiceThree.getText().toString().trim();
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
                insertNewQuestion();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void insertNewQuestion(){
        insertButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        //getting user input values
        final String questionTitle = eQuestionTitle.getText().toString().trim();
        final String questionChoiceOne = eQuestionChoiceOne.getText().toString().trim();
        final String questionChoiceTwo = eQuestionChoiceTwo.getText().toString().trim();
        final String questionChoiceThree = eQuestionChoiceThree.getText().toString().trim();

        //validating the input in question title
        if (TextUtils.isEmpty(questionTitle)){
            progressBar.setVisibility(View.GONE);
            eQuestionTitle.setError(getString(R.string.specify_question_title));
            eQuestionTitle.requestFocus();
            insertButton.setEnabled(true);
            return;
        }
        //validating the input in question choice one
        if (TextUtils.isEmpty(questionChoiceOne)){
            progressBar.setVisibility(View.GONE);
            eQuestionChoiceOne.setError(getString(R.string.specify_choice_one));
            eQuestionChoiceOne.requestFocus();
            insertButton.setEnabled(true);
            return;
        }
        //validating the input in question choice two
        if (TextUtils.isEmpty(questionChoiceTwo)){
            progressBar.setVisibility(View.GONE);
            eQuestionChoiceTwo.setError(getString(R.string.specify_choice_two));
            eQuestionChoiceTwo.requestFocus();
            insertButton.setEnabled(true);
            return;
        }
        //validating the input in question choice three
        if (TextUtils.isEmpty(questionChoiceThree)){
            progressBar.setVisibility(View.GONE);
            eQuestionChoiceThree.setError(getString(R.string.specify_choice_three));
            eQuestionChoiceThree.requestFocus();
            insertButton.setEnabled(true);
            return;
        }

        if (answer == null || TextUtils.isEmpty(answer)){
            progressBar.setVisibility(View.GONE);
            answerSpinner.requestFocus();
            TextView errorText = (TextView)answerSpinner.getSelectedView();
            errorText.setError(getString(R.string.specify_the_right_answer));
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            insertButton.setEnabled(true);
            return;
        }


        //if everything is fine start StringRequest
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_INSERT_NEW_QUESTION, new Response.Listener<String>() {
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

                    }else{
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        insertButton.setEnabled(true);
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
                pars.put("lesson_ID", lesson_ID);
                pars.put("q_title", questionTitle);
                pars.put("q_choice1", questionChoiceOne);
                pars.put("q_choice2", questionChoiceTwo);
                pars.put("q_choice3", questionChoiceThree);
                pars.put("q_answer", answer);
                return pars;
            }
        };
        mRequestQueue.add(request);
    }

}
