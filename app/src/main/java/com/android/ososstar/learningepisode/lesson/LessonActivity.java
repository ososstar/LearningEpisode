package com.android.ososstar.learningepisode.lesson;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.question.QuestionListActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.regex.Pattern;

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

    //declare ExoPlayer keys
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final Pattern youtubePattern = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$");
    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    private int mResumeWindow;
    private long mResumePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }

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

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeFragment);

        //show/hide youtube player when youtube url is inserted
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);

        if (youtubePattern.matcher(videoURL).matches()) {
            ft.show(youTubePlayerFragment);
            vidSplitter = videoURL.substring(Math.max(0, videoURL.length() - 11));
            initializeYoutubePlayer();
        } else {
            ft.hide(youTubePlayerFragment);
        }


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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (!youtubePattern.matcher(videoURL).matches()) {
            outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
            outState.putLong(STATE_RESUME_POSITION, mResumePosition);
            outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        }
        super.onSaveInstanceState(outState);
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(LessonActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.playerLayout)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(LessonActivity.this, R.drawable.ic_fullscreen_expand));
    }


    private void initFullscreenButton() {

        PlaybackControlView controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    private void initExoPlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        mExoPlayerView.getPlayer().prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle lessonBundle = getIntent().getExtras();
        if (lessonBundle == null) {
            closeOnError();
        }

        videoURL = lessonBundle.getString(EXTRA_LESSON_VIDEO);

        Log.d("LessonActivity", "onResume: " + videoURL);


        if (youtubePattern.matcher(videoURL).matches()) {
            mExoPlayerView = findViewById(R.id.exoPlayer);
            mExoPlayerView.setVisibility(View.GONE);
        } else {
            if (mExoPlayerView == null) {

                mExoPlayerView = findViewById(R.id.exoPlayer);
                initFullscreenDialog();
                initFullscreenButton();

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory =
                        new DefaultDataSourceFactory(this, Util.getUserAgent(this, "LearningEpisode"));

                // Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                // This is the MediaSource representing the media to be played.
                Uri videoUri = Uri.parse(videoURL);

                // This is the MediaSource representing the media to be played.
                mVideoSource = new ExtractorMediaSource(videoUri,
                        dataSourceFactory, extractorsFactory, null, null);
            }

            initExoPlayer();

            if (mExoPlayerFullscreen) {
                ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
                mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(LessonActivity.this, R.drawable.ic_fullscreen_skrink));
                mFullScreenDialog.show();
            }
        }

    }


    @Override
    protected void onPause() {

        super.onPause();

        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
            mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());

            mExoPlayerView.getPlayer().release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }


//    private void initializePlayer() {
//        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(LessonActivity.this);
//
//        PlayerView playerView = findViewById(R.id.exoPlayer);
//
//        // Bind the player to the view.
//        playerView.setPlayer(player);
//
//        // Produces DataSource instances through which media data is loaded.
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(LessonActivity.this,
//                Util.getUserAgent(LessonActivity.this, "LearningEpisode"));
//        // This is the MediaSource representing the media to be played.
//        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(videoURL));
//        // Prepare the player with the source.
//        player.prepare(videoSource);
//    }



    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     */
    private void initializeYoutubePlayer() {

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
