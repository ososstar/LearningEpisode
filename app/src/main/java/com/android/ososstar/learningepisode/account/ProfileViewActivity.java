package com.android.ososstar.learningepisode.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.ososstar.learningepisode.R;

import static com.android.ososstar.learningepisode.account.AccountsListActivity.ADMIN_ID;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_DATE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_EMAIL;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_ID;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_NAME;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_PICTURE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_TYPE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_USERNAME;

public class ProfileViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get profile values
        Bundle profileBundle = getIntent().getExtras();
        String admin_ID = profileBundle.getString(ADMIN_ID);
        String id = profileBundle.getString(PROFILE_ID);
        String username = profileBundle.getString(PROFILE_USERNAME);
        String email = profileBundle.getString(PROFILE_EMAIL);
        String name = profileBundle.getString(PROFILE_NAME);
        String imageURL = profileBundle.getString(PROFILE_PICTURE);
        String type = profileBundle.getString(PROFILE_TYPE);
        String date = profileBundle.getString(PROFILE_DATE);
    }
}
