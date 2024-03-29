package com.android.ososstar.learningepisode.account;

import static com.android.ososstar.learningepisode.account.AccountsListActivity.ADMIN_ID;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_DATE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_EMAIL;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_ID;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_NAME;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_PICTURE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_TYPE;
import static com.android.ososstar.learningepisode.account.AccountsListActivity.PROFILE_USERNAME;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.ososstar.learningepisode.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        //define ImageView
        ImageView profilePicture = findViewById(R.id.profilePic);

        //define TextViews of profileActivity
        TextView profileID, profileUsername, profileEmail, profileName, profileType, profileDate;
        profileID = findViewById(R.id.profileID);
        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileType = findViewById(R.id.profileType);
        profileDate = findViewById(R.id.profileDate);

        //set values to show the profile picture and the TextViews
        Picasso.get().load(imageURL).placeholder(R.drawable.user).error(R.drawable.user).noFade().into(profilePicture);
        profileName.setText(name);

        StringBuilder ID_sb = new StringBuilder(getString(R.string.idHash));
        ID_sb.append(id);
        profileID.setText(ID_sb);

        StringBuilder username_sb = new StringBuilder(getString(R.string.usernameHash));
        username_sb.append(username);
        profileUsername.setText(username_sb);

        StringBuilder email_sb = new StringBuilder(getString(R.string.emailHash));
        email_sb.append(email);
        profileEmail.setText(email_sb);

        StringBuilder type_sb = new StringBuilder(getString(R.string.typeHash));

        switch (type) {
            case "0":
                type_sb.append(getString(R.string.admin));
                profileType.setText(type_sb);
                break;
            case "1":
                type_sb.append(getString(R.string.student));
                profileType.setText(type_sb);
                break;
        }

//        StringBuilder date_sb = new StringBuilder(getString(R.string.creation_date));
//        date_sb.append(date);
//        profileDate.setText(date_sb);

        StringBuilder creationDateSB = new StringBuilder(getString(R.string.creation_date));
        if (Locale.getDefault().getLanguage().equals("ar")) {
            Locale localeAR = new Locale("ar");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");
            Date date3 = null;
            try {
                date3 = sdf.parse(date);
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", localeAR);
            String format = sdf.format(date3);
            Log.wtf("result", format);
            creationDateSB.append(format);
            profileDate.setText(creationDateSB);
        } else {
            Locale localeAR = new Locale("en");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d");
            Date date3 = null;
            try {
                date3 = sdf.parse(date);
            } catch (Exception e) {

            }
            sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", localeAR);
            String format = sdf.format(date3);
            Log.wtf("result", format);
            creationDateSB.append(format);
            profileDate.setText(creationDateSB);
        }

    }
}
