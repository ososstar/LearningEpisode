package com.android.ososstar.learningepisode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.account.AccountsListActivity;
import com.android.ososstar.learningepisode.account.LoginActivity;
import com.android.ososstar.learningepisode.account.ProfileActivity;
import com.android.ososstar.learningepisode.account.User;
import com.android.ososstar.learningepisode.course.CourseListActivity;

import java.util.ArrayList;


public class HomeAdminActivity extends AppCompatActivity implements OptionAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        setTitle("Home Page");

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //greeting user in home activity
        TextView homeUser = findViewById(R.id.home_user);
        homeUser.setText("Welcome, " + user.getName());


        // Create a list of user Options
        final ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("Courses",R.drawable.books));
        options.add(new Option("Accounts",R.drawable.userlist));
        options.add(new Option("Feedback",R.drawable.feedback));

        // Create an {@link OptionAdapter}, whose data source is a list of {@link Option}s. The
        // adapter knows how to create list items for each item in the list.
        RecyclerView home_rv_options = findViewById(R.id.home_rv_options);
        home_rv_options.setHasFixedSize(true);
        LinearLayoutPagerManager layoutManager = new LinearLayoutPagerManager(this, LinearLayoutManager.HORIZONTAL, false, 3);
        home_rv_options.setLayoutManager(layoutManager);
        OptionAdapter adapter = new OptionAdapter(this, options);
        // Make the {@link GridView} use the {@link OptionAdapter} we created above, so that the
        // {@link GridView} will display list items for each {@link Option} in the list.
        home_rv_options.setAdapter(adapter);
        adapter.setOnItemClickListener(HomeAdminActivity.this);

    }

    // Add The Behaviour When the User Click An Item
    @Override
    public void onItemClickOptions(int position){
        switch (position){
            case 0:
                startActivity(new Intent(getBaseContext(), CourseListActivity.class));
                break;
            case 1:
                startActivity(new Intent(getBaseContext(), AccountsListActivity.class));
                break;
            case 2:
                Toast.makeText(getBaseContext(), "Feedback Activity is Under Construction", Toast.LENGTH_SHORT).show();
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_add options from the res/menu_add/menu_home.xmll file.
        // This adds menu_add items to the app bar.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu_add option in the app bar overflow menu_add
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu_add option
            case R.id.action_profile:
                startActivity(new Intent(HomeAdminActivity.this, ProfileActivity.class));
                return true;
            case R.id.menu_logout:
                finish();
                finishAffinity();
                SharedPrefManager.getInstance(getBaseContext()).logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
