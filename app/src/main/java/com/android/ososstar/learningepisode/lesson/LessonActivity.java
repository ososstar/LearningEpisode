package com.android.ososstar.learningepisode.lesson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.question.QuestionListActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_DATE;
import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_DESCRIPTION;
import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_ID;
import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_LINK;
import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_TITLE;
import static com.android.ososstar.learningepisode.lesson.LessonListActivity.EXTRA_LESSON_VIDEO;

public class LessonActivity extends AppCompatActivity {

    public static final String API_KEY = "AIzaSyAPyC3VEWub9v1v6vPWLDDvru1lfpUqJKg";

    private String id, title, description, link, videoURL, creation_date;

    private String vidSplitter;

    private Button lessonDownloadB, lessonQuestionsB;

    public static final String LESSON_ID = "lesson_ID";

    //youtube player fragment
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        Bundle lessonBundle = getIntent().getExtras();
        if (lessonBundle == null) {
            closeOnError();
        }

        id = lessonBundle.getString(EXTRA_LESSON_ID);
        title = lessonBundle.getString(EXTRA_LESSON_TITLE);
        description = lessonBundle.getString(EXTRA_LESSON_DESCRIPTION);
        link = lessonBundle.getString(EXTRA_LESSON_LINK);
        videoURL = lessonBundle.getString(EXTRA_LESSON_VIDEO);
        creation_date = lessonBundle.getString(EXTRA_LESSON_DATE);

        setTitle(title);

        TextView lessonIdTV = findViewById(R.id.lesson_id_tv);
        lessonIdTV.setText(getString(R.string.id) + id);

        TextView lessonTitleTV = findViewById(R.id.lesson_title_tv);
        lessonTitleTV.setText(title);

        TextView lessonDescriptionTV = findViewById(R.id.lesson_description_tv);
        if (!description.isEmpty() && !description.equals("null")) {
            lessonDescriptionTV.setText(description);
        }else {
            lessonDescriptionTV.setText(R.string.no_description);
        }

        TextView lessonDateTv = findViewById(R.id.lesson_date_tv);
        lessonDateTv.setText(getString(R.string.creation_date) + creation_date);

        vidSplitter = videoURL.substring(Math.max(0, videoURL.length() - 11));

//        @SuppressWarnings({"deprecation"})
//        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
//                getFragmentManager().findFragmentById(R.id.youtubeFragment);
//        youtubeFragment.initialize(API_KEY,
//                new YouTubePlayer.OnInitializedListener() {
//                    @Override
//                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                                        YouTubePlayer youTubePlayer, boolean b) {
//                        //set the player style default
//                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
//                        // do any work here to cue video, play video, etc.
//                        youTubePlayer.cueVideo(vidSplitter);
//                    }
//                    @Override
//                    public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                        YouTubeInitializationResult youTubeInitializationResult) {
//
//                    }
//                });

        initializeYoutubePlayer();

        lessonDownloadB = findViewById(R.id.lesson_download_b);
        if (!TextUtils.isEmpty(link) && link != null && !link.equals("null")) {
            lessonDownloadB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    startActivity(intent);
                }
            });
        } else {
            lessonDownloadB.setVisibility(View.GONE);
        }

        lessonQuestionsB = findViewById(R.id.lesson_questions_b);
        lessonQuestionsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent questionsIntent = new Intent(LessonActivity.this, QuestionListActivity.class);
                    questionsIntent.putExtra(LESSON_ID, id);
                    startActivity(questionsIntent);
                }catch (Exception e){
                    Log.e("Lesson Activity ->", String.valueOf(e));
                }

            }
        });
    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeFragment);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;

                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //cue the 1st video by default
                    youTubePlayer.cueVideo(vidSplitter);
                }
            }


            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.no_description, Toast.LENGTH_SHORT).show();
    }


}
