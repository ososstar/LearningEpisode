package com.android.ososstar.learningepisode.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //getting user values
        User user = SharedPrefManager.getInstance(this).getUser();

        String id = String.valueOf(user.getID());
        String username = user.getUsername();
        String email = user.getEmail();
        String name = user.getName();
        String imageURL = user.getImageURL();
        String type = String.valueOf(user.getType());
        String date = user.getDate();

        Log.d("ProfileActivity", "onCreate: " + imageURL);

        //define ImageView
        ImageView profilePicture = findViewById(R.id.profilePic);
        Picasso.with(ProfileActivity.this).load(imageURL).placeholder(R.drawable.man).error(R.drawable.man).noFade().into(profilePicture);

        //define TextViews of profileActivity
        TextView profileID, profileUsername, profileEmail, profileName, profileType, profileDate;
        profileID = findViewById(R.id.profileID);
        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileType = findViewById(R.id.profileType);
        profileDate = findViewById(R.id.profileDate);

        //set values on the TextViews
        profileID.setText(id);
        profileUsername.setText(username);
        profileEmail.setText(email);
        profileName.setText(name);

        switch (type) {
            case "0":
                profileType.setText(getString(R.string.admin));
                break;
            case "1":
                profileType.setText(getString(R.string.student));
                break;
        }

        profileDate.setText(date);



    }
}
