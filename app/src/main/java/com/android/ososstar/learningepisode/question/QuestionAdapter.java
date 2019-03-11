package com.android.ososstar.learningepisode.question;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Question> mQuestionList;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Create a new {@link QuestionAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param questionList is the list of {@link Question} to be displayed.
     */
    public QuestionAdapter(Context context, ArrayList<Question> questionList) {
        mContext = context;
        mQuestionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Question currentQuestion = mQuestionList.get(position);

        holder.setQuestion(currentQuestion.getTitle());
        holder.setOptions(currentQuestion, position);
        holder.question_ID = currentQuestion.getID();

        holder.question_Title = currentQuestion.getTitle();
        holder.question_Choice1 = currentQuestion.getChoice1();
        holder.question_Choice2 = currentQuestion.getChoice2();
        holder.question_Choice3 = currentQuestion.getChoice3();
    }

    @Override
    public int getItemCount() {
        if (mQuestionList == null) {
            return 0;
        } else {
            return mQuestionList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
        private TextView questionTitle, questiodID;
        private RadioGroup radioGroup;
        public static final String EXTRA_ADMIN_ID = "admin_ID";
        public static final String EXTRA_QUESTION_ID = "question_ID";

        //getting the current user
        private User user = SharedPrefManager.getInstance(mContext).getUser();
        private RequestQueue mRequestQueue;
        public static final String EXTRA_QUESTION_TITLE = "q_title";


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
            popup.getMenuInflater().inflate(R.menu.menu_management, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        public static final String EXTRA_QUESTION_CHOICE_1 = "q_choice1";
        public static final String EXTRA_QUESTION_CHOICE_2 = "q_choice2";
        public static final String EXTRA_QUESTION_CHOICE_3 = "q_choice3";
        public static final String EXTRA_QUESTION_ANSWER = "q_answer";
        private RadioButton RQuestionChoice1, RQuestionChoice2, RQuestionChoice3;
        private String admin_ID, question_ID, question_Title, question_Choice1, question_Choice2, question_Choice3, question_Answer, student_ID;
        public ViewHolder(View itemView, final OnItemClickListener mListener) {
            super(itemView);
            //getting the current user type
            int userType = SharedPrefManager.getInstance(mContext).getUser().getType();
            if (userType == 0) {
                itemView.setOnCreateContextMenuListener(this);
            }

            if (user.getType() == 0) {
                admin_ID = String.valueOf(user.getID());
            }

            questionTitle = itemView.findViewById(R.id.q_title);
            radioGroup = itemView.findViewById(R.id.q_radioGroup);
            RQuestionChoice1 = itemView.findViewById(R.id.q_choice1);
            RQuestionChoice2 = itemView.findViewById(R.id.q_choice2);
            RQuestionChoice3 = itemView.findViewById(R.id.q_choice3);

            RQuestionChoice1.setChecked(false);
            RQuestionChoice2.setChecked(false);
            RQuestionChoice3.setChecked(false);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int id = item.getItemId();
            // ERROR : How to get item position which ContextMenu Created
            switch (id) {
                case R.id.option_1 :
                    Toast.makeText(mContext, "modify activity is under construction", Toast.LENGTH_SHORT).show();
                    Intent modifyIntent = new Intent(itemView.getContext(), QuestionModifyActivity.class);
                    Bundle modifyBundle = new Bundle();
                    modifyBundle.putString(EXTRA_ADMIN_ID, admin_ID);
                    modifyBundle.putString(EXTRA_QUESTION_ID, question_ID);
                    modifyBundle.putString(EXTRA_QUESTION_TITLE, question_Title);
                    modifyBundle.putString(EXTRA_QUESTION_CHOICE_1, question_Choice1);
                    modifyBundle.putString(EXTRA_QUESTION_CHOICE_2, question_Choice2);
                    modifyBundle.putString(EXTRA_QUESTION_CHOICE_3, question_Choice3);
                    modifyBundle.putString(EXTRA_QUESTION_ANSWER, question_Answer);
                    modifyIntent.putExtras(modifyBundle);
                    ((QuestionListActivity) mContext).startActivityForResult(modifyIntent, 1);
                    return true;
                case R.id.option_2:
                    deleteQuestion();
                    return true;

            }
            return false;
        }

        private void deleteQuestion() {
            mRequestQueue = Volley.newRequestQueue(mContext);

            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_QUESTION_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject baseJSONObject = new JSONObject(response);
                        if (!baseJSONObject.getBoolean("error")) {
                            Toast.makeText(mContext, String.valueOf(baseJSONObject.getString("message")), Toast.LENGTH_SHORT).show();
                            //Remove deleted row from RecyclerView List
                            mQuestionList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), mQuestionList.size());

                        } else {
                            Toast.makeText(mContext, String.valueOf(baseJSONObject.getString("message")), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> pars = new HashMap<String, String>();
                    pars.put("Content-Type", "application/x-www-form-urlencoded");
                    return pars;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> pars = new HashMap<String, String>();
                    pars.put("admin_ID", admin_ID);
                    pars.put("question_ID", question_ID);
                    return pars;
                }
            };
            mRequestQueue.add(request);
        }


        public void setQuestion(String question) {
            questionTitle.setText(question);
        }


        public void setOptions(Question question, int position) {
            radioGroup.setTag(position);
            RQuestionChoice1.setText(question.getChoice1());
            RQuestionChoice2.setText(question.getChoice2());
            RQuestionChoice3.setText(question.getChoice3());
            question_Answer = question.getAnswer();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int pos = (int) group.getTag();
                    Question question = mQuestionList.get(pos);
                    question.setCheckedId(checkedId);

                    switch (question.getCheckedId()){
                        case R.id.q_choice1:
                            if (RQuestionChoice1.getText().toString().equals(question_Answer)) {
                                Log.d("QuestionAdapter", "onCheckedChanged: " + "right");
                                RQuestionChoice1.setBackgroundColor(0xFF00FF00);
                                rightAnswer();

                            }
                            break;
                        case R.id.q_choice2:
                            if (RQuestionChoice2.getText().toString().equals(question_Answer)) {
                                Log.d("QuestionAdapter", "onCheckedChanged: " + "right");
                                RQuestionChoice2.setBackgroundColor(0xFF00FF00);
                                rightAnswer();
                            }
                            break;
                        case R.id.q_choice3:
                            if (RQuestionChoice3.getText().toString().equals(question_Answer)) {
                                Log.d("QuestionAdapter", "onCheckedChanged: " + "right");
                                RQuestionChoice3.setBackgroundColor(0xFF00FF00);
                                rightAnswer();
                            }
                            break;
                    }
                }
            });
        }

        /** Handles playback of all the sound files */
        private MediaPlayer mMediaPlayer;

        private void rightAnswer(){
            // Create and setup the {@link MediaPlayer} for the audio resource associated
            // with the correct answer
            mMediaPlayer = MediaPlayer.create(mContext, R.raw.correct);
            // Start the audio file
            mMediaPlayer.start();

            //getting the current user
            User user = SharedPrefManager.getInstance(mContext).getUser();


            if (user.getType() == 1) {
                student_ID = String.valueOf(user.getID());

                mRequestQueue = Volley.newRequestQueue(mContext);

                StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_RECORD_STUDENT_ANSWER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject baseJSONObject = new JSONObject(response);
                            Log.i("QuestionAdapter", "onResponse: "+ baseJSONObject.getString("message"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    public Map<String, String> getParams() {
                        Map<String, String> pars = new HashMap<String, String>();
                        pars.put("student_ID", student_ID);
                        pars.put("question_ID", question_ID);
                        pars.put("answer_status", "1");
                        return pars;
                    }
                };
                mRequestQueue.add(request);
            }
        }


    }
}
