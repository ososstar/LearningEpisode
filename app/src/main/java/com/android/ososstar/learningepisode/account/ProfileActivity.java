package com.android.ososstar.learningepisode.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "userID";
    public static final String USER_USERNAME = "username";
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "userName";
    public static final String USER_IMAGE = "image";
    public static final String USER_TYPE = "type";
    public static final String USER_DATE = "date";
    private String id, username, email, name, imageURL, type, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //getting user values
        User user = SharedPrefManager.getInstance(this).getUser();

        id = String.valueOf(user.getID());
        username = user.getUsername();
        email = user.getEmail();
        name = user.getName();
        imageURL = user.getImageURL();
        type = String.valueOf(user.getType());
        date = user.getDate();

        Log.d("ProfileActivity", "onCreate: " + imageURL);

        //define ImageView
        ImageView profilePicture = findViewById(R.id.profilePic);
        if (imageURL != null && imageURL.isEmpty()) {
            profilePicture.setImageResource(R.drawable.man);
        } else {
            Picasso.with(ProfileActivity.this).load(imageURL).placeholder(R.drawable.man).error(R.drawable.man).noFade().into(profilePicture);
        }

        //define TextViews of profileActivity
        TextView profileID, profileUsername, profileEmail, profileName, profileType, profileDate;
        profileID = findViewById(R.id.profileID);
        profileUsername = findViewById(R.id.profileUsername);
        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileType = findViewById(R.id.profileType);
        profileDate = findViewById(R.id.profileDate);

        //set values on the TextViews
        StringBuilder idBuilder = new StringBuilder(getString(R.string.idHash));
        idBuilder.append(id);
        profileID.setText(idBuilder);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_add options from the res/menu_add/menu_home.xmll file.
        // This adds menu_add items to the app bar.
        if (type.equals("1")) {
            getMenuInflater().inflate(R.menu.menu_profile, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu_add option in the app bar overflow menu_add
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu_add option
            case R.id.editProfile:

                Bundle modifyBundle = new Bundle();
                modifyBundle.putString(USER_ID, id);
                modifyBundle.putString(USER_USERNAME, username);
                modifyBundle.putString(USER_EMAIL, email);
                modifyBundle.putString(USER_NAME, name);
                modifyBundle.putString(USER_IMAGE, imageURL);
                modifyBundle.putString(USER_TYPE, type);
                modifyBundle.putString(USER_DATE, date);

                startActivityForResult(new Intent(ProfileActivity.this, AccountModifyActivity.class).putExtras(modifyBundle), 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            finish();
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        }
    }
}
