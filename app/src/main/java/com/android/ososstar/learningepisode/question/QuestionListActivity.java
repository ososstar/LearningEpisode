package com.android.ososstar.learningepisode.question;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.account.User;
import com.android.ososstar.learningepisode.sslService;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.ososstar.learningepisode.lesson.LessonActivity.LESSON_ID;

public class QuestionListActivity extends AppCompatActivity implements QuestionAdapter.OnItemClickListener {

    public static final String EXTRA_ADMIN_ID = "admin_ID";
    public static final String EXTRA_LESSON_ID ="lesson_ID";

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * progressBar that is displayed when the list is loading
     */
    private ProgressBar progressBar;

    /**
     * RecyclerView, Adapter, and Volley Method to show Enrollment data into list
     */
    private RecyclerView mList;
    private QuestionAdapter mQuestionAdapter;
    private ArrayList<Question> questionList;
    private RequestQueue mRequestQueue;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    //getting the current user
    private User user = SharedPrefManager.getInstance(QuestionListActivity.this).getUser();

    private String lesson_ID, admin_ID, student_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (user.getType() == 1){
            student_ID = String.valueOf(user.getID());
        }
        else {
            admin_ID = String.valueOf(user.getID());
        }

        //Display List Title
        setTitle(R.string.questions_list);

        Intent intent = getIntent();
        //getting value of lesson ID
        lesson_ID = intent.getStringExtra(LESSON_ID);

        mEmptyStateTextView = findViewById(R.id.l_empty_view);

        mList = findViewById(R.id.l_RV);

        questionList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);

        mRequestQueue = Volley.newRequestQueue(this);

        progressBar = findViewById(R.id.l_spinner);

        if (ConnectivityHelper.isNetworkAvailable(QuestionListActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            getLessonQuestions();
        }else {
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show();
            connectASAP();
            // Set empty state text to display "Check your connection"
            mEmptyStateTextView.setText(R.string.noInternet);
        }

        FloatingActionButton insertFAB = findViewById(R.id.l_insertFAB);
        //show FAB for admin
        if (user.getType() == 0) insertFAB.show();
        insertFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insertIntent = new Intent(QuestionListActivity.this, QuestionInsertActivity.class);
                insertIntent.putExtra(EXTRA_ADMIN_ID, admin_ID);
                insertIntent.putExtra(EXTRA_LESSON_ID, lesson_ID);
                startActivityForResult(insertIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getLessonQuestions();
        }
    }

    private void connectASAP() {
        if (ConnectivityHelper.isNetworkAvailable(QuestionListActivity.this)) {
            Intent sslIntent = new Intent(this, sslService.class);
            startService(sslIntent);
            getLessonQuestions();
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

    private void getLessonQuestions() {

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REQUEST_QUESTION_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.GONE);
                try {
                    //if no error in response
                    JSONObject baseJSONObject = new JSONObject(response);

                    if (!baseJSONObject.getBoolean("error")) {

                        questionList.clear();

                        //getting the questions list from the response
                        JSONArray questionArray = baseJSONObject.getJSONArray("questions");

                        // For each question in the question Array, create a {@link Question} object
                        for (int i = 0; i < questionArray.length(); i++) {
                            // Get a single question at position i within the list of questionList
                            JSONObject currentQuestion = questionArray.getJSONObject(i);

                            // Extract the value for the key called "ID"
                            String id = currentQuestion.getString("ID");

                            // Extract the value for the key called "c_name"
                            String title = currentQuestion.getString("q_title");

                            // Extract the value for the key called "l_description"
                            String choice1 = currentQuestion.getString("q_choice1");

                            // Extract the value for the key called "c_description"
                            String choice2 = currentQuestion.getString("q_choice2");

                            // Extract the value for the key called "c_image"
                            String choice3 = currentQuestion.getString("q_choice3");

                            // Extract the value for the key called "c_enrolls"
                            String answer = currentQuestion.getString("q_answer");

                            // Extract the value for the key called "c_enrolls"
                            String lessonID = currentQuestion.getString("lesson_ID");

                            // Create a new {@link Question} object with the ID, Name, Description
                            // , Image, enrolls and creation Date from the JSON response.
                            Question question = new Question(id, title, choice1, choice2, choice3, answer, lessonID);

                            // Add the new {@link Question} to the list of questionList.
                            questionList.add(question);
                        }

                        mQuestionAdapter = new QuestionAdapter(QuestionListActivity.this, questionList);
                        mList.setAdapter(mQuestionAdapter);
                        mQuestionAdapter.notifyDataSetChanged();
                        mQuestionAdapter.setOnItemClickListener(QuestionListActivity.this);

                    } else {
                        // Set empty state text to display "No questions found."
                        mEmptyStateTextView.setText(R.string.no_questions);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                error.printStackTrace();
                connectASAP();
                SharedPrefManager.getInstance(QuestionListActivity.this).setSSLStatus(1);
                // Set empty state text to display "No Users is found."
                mEmptyStateTextView.setText(R.string.error_no_data_received);
                CountDownTimer CDT = new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        getLessonQuestions();
                    }
                };
                CDT.start();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("lesson_ID", lesson_ID);
                return pars;
            }
        };
        mRequestQueue.add(request);


    }



    @Override
    public void onItemClick(int position) {

    }
}
