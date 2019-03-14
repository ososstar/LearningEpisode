package com.android.ososstar.learningepisode.account;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.ososstar.learningepisode.R;
import com.android.volley.RequestQueue;

public class AccountModifyActivity extends AppCompatActivity {

    //declare TextInputLayout of Account InsertActivity
    private TextInputLayout eAccountUsernameTL, eAccountPasswordTL, eAccountEmailTL, eAccountNameTL;

    //declare EditTexts of AccountInsertActivity
    private EditText eAccountUsernameET, eAccountPasswordET, eAccountEmailET, eAccountNameET;

    //declare spinner of AccountInsertActivity
    private Spinner eAccountTypeSP;

    //declare the insert button of this activity
    private Button eAccountInsertB;

    //declare RequestQueue for volley http request
    private RequestQueue mRequestQueue;

    //declare progressBar
    private ProgressBar progressBar;

    //user type variable
    private String eAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_account);

        //set activity name in action bar
        setTitle(R.string.modify_user_data);

        //change title name of this activity
        TextView titleTextView = findViewById(R.id.eAccountTitle);
        titleTextView.setText(R.string.modify_user_data);

        //define TextInputLayouts
        eAccountUsernameTL = findViewById(R.id.eAccountUsername);
        eAccountPasswordTL = findViewById(R.id.eAccountPassword);
        eAccountEmailTL = findViewById(R.id.eAccountEmail);
        eAccountNameTL = findViewById(R.id.eAccountName);

        //define EditText
        eAccountUsernameET = findViewById(R.id.eUsername_et);
        eAccountPasswordET = findViewById(R.id.ePassword_et);
        eAccountEmailET = findViewById(R.id.eEmail_et);
        eAccountNameET = findViewById(R.id.eName_et);

        //define type spinner
        eAccountTypeSP = findViewById(R.id.eAccountType_sp);


    }
}
