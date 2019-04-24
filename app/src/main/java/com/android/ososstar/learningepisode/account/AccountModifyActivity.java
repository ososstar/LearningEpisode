package com.android.ososstar.learningepisode.account;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_EMAIL;
import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_ID;
import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_IMAGE;
import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_NAME;
import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_TYPE;
import static com.android.ososstar.learningepisode.account.ProfileActivity.USER_USERNAME;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.ADMIN_ID;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_EMAIL;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_ID;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_IMAGE;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_NAME;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_TYPE;
import static com.android.ososstar.learningepisode.account.UserAdapter.ViewHolder.STUDENT_USERNAME;

public class AccountModifyActivity extends AppCompatActivity {

    private static final String TAG = "AccountModifyActivity";
    //declare TextInputLayout of Account InsertActivity
    private TextInputLayout eAccountUsernameTL, eAccountPasswordTL, eAccountEmailTL, eAccountNameTL, eAccountImageTL;

    //declare EditTexts of AccountInsertActivity
    private EditText eAccountUsernameET, eAccountPasswordET, eAccountEmailET, eAccountNameET, eAccountImageET;

    //declare spinner of AccountInsertActivity
    private Spinner eAccountTypeSP;

    //declare the insert button of this activity
    private Button eAccountInsertB;

    //declare RequestQueue for volley http request
    private RequestQueue mRequestQueue;

    //declare progressBar
    private ProgressBar progressBar;

    //user type variable
    private String admin_ID, eAccountID, eAccountUsername, eAccountEmail, eAccountName, eAccountImage, eAccountType;

    private Bundle modifyBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_account);

        //getting Bundle Intent Values
        modifyBundle = getIntent().getExtras();
        if (!TextUtils.isEmpty(modifyBundle.getString(ADMIN_ID))) {
            admin_ID = modifyBundle.getString(ADMIN_ID);
            eAccountID = modifyBundle.getString(STUDENT_ID);
            eAccountUsername = modifyBundle.getString(STUDENT_USERNAME);
            eAccountEmail = modifyBundle.getString(STUDENT_EMAIL);
            eAccountName = modifyBundle.getString(STUDENT_NAME);
            eAccountImage = modifyBundle.getString(STUDENT_IMAGE);
            eAccountType = modifyBundle.getString(STUDENT_TYPE);
        } else {
            eAccountID = modifyBundle.getString(USER_ID);
            eAccountUsername = modifyBundle.getString(USER_USERNAME);
            eAccountEmail = modifyBundle.getString(USER_EMAIL);
            eAccountName = modifyBundle.getString(USER_NAME);
            eAccountImage = modifyBundle.getString(USER_IMAGE);
            eAccountType = modifyBundle.getString(USER_TYPE);

            if (eAccountType.matches("0")) {
                admin_ID = eAccountID;
            }
        }

        Log.d(TAG, "onCreate: " + modifyBundle);

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
        eAccountImageTL = findViewById(R.id.eAccountImage);

        //define EditText
        eAccountUsernameET = findViewById(R.id.eUsername_et);
        eAccountPasswordET = findViewById(R.id.ePassword_et);
        eAccountEmailET = findViewById(R.id.eEmail_et);
        eAccountNameET = findViewById(R.id.eName_et);
        eAccountImageET = findViewById(R.id.eImage_et);

        eAccountPasswordTL.setHint(getString(R.string.password_optional));

        //insert old values
        eAccountUsernameET.setText(eAccountUsername);
        eAccountEmailET.setText(eAccountEmail);
        eAccountNameET.setText(eAccountName);
        eAccountImageET.setText(eAccountImage);

        eAccountUsernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eAccountUsernameTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eAccountEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eAccountEmailTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eAccountNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eAccountNameTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        eAccountImageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eAccountImageTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //define type spinner
        eAccountTypeSP = findViewById(R.id.eAccountType_sp);


        if (TextUtils.isEmpty(modifyBundle.getString(ADMIN_ID))) {
            eAccountTypeSP.setVisibility(View.GONE);
        } else {

            // Initializing a String Array
            String[] types = new String[]{
                    "Admin",
                    "Student"
            };

            // Initializing an ArrayAdapter for spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, types);

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //Sets the Adapter used to provide the data which backs this Spinner.
            eAccountTypeSP.setAdapter(spinnerAdapter);

            switch (eAccountType) {
                case "0":
                    eAccountTypeSP.setSelection(0);
                    break;
                case "1":
                    eAccountTypeSP.setSelection(1);
                    break;
            }

            eAccountTypeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //use position value
                    switch (position) {
                        case 0:
                            eAccountType = "0";
                            break;
                        case 1:
                            eAccountType = "1";
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        //define progressbar
        progressBar = findViewById(R.id.eAccountProgressSpinner);

        mRequestQueue = Volley.newRequestQueue(this);

        eAccountInsertB = findViewById(R.id.eAccountInsert_b);
        eAccountInsertB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyAccount();
            }
        });

    }

    private void modifyAccount() {
        final String eAccountUsername, eAccountPassword, eAccountEmail, eAccountName, eAccountImage;
        eAccountUsername = eAccountUsernameET.getText().toString().trim();
        eAccountPassword = eAccountPasswordET.getText().toString().trim();
        eAccountEmail = eAccountEmailET.getText().toString().trim();
        eAccountName = eAccountNameET.getText().toString().trim();
        eAccountImage = eAccountImageET.getText().toString().trim();

        Log.d(TAG, "modifyAccount: " + eAccountImage);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //validating the inserted values
        if (TextUtils.isEmpty(eAccountUsername)) {
            eAccountUsernameTL.setErrorEnabled(true);
            eAccountUsernameTL.setError("please insert the Username");
            eAccountUsernameET.requestFocus();
            return;
        }
        if (!eAccountEmail.matches(emailPattern)) {
            eAccountEmailTL.setErrorEnabled(true);
            eAccountEmailTL.setError("please insert a valid Email");
            eAccountUsernameET.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(eAccountName)) {
            eAccountNameTL.setErrorEnabled(true);
            eAccountNameTL.setError("please insert the name of the student");
            return;
        }
        if (!TextUtils.isEmpty(eAccountImage)) {
            Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            boolean isValidImageURL = urlPattern.matcher(eAccountImage).matches();
            //check if the given link is a url
            if (!isValidImageURL) {
                eAccountImageTL.setErrorEnabled(true);
                eAccountImageTL.setError("Please insert valid image url");
                eAccountImageET.requestFocus();
                return;
            }
        }

        //if everything is fine make setup HTTP request
        eAccountInsertB.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_MODIFY_STUDENT_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                eAccountInsertB.setEnabled(true);
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    //if no error in response
                    if (!baseJSONObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                        if (TextUtils.isEmpty(modifyBundle.getString(ADMIN_ID))) {
                            //getting the user from the response
                            JSONObject userJson = baseJSONObject.getJSONObject("user");

                            //creating a new user object
                            User user = new User(
                                    userJson.getInt("id"),
                                    userJson.getString("username"),
                                    userJson.getString("email"),
                                    userJson.getString("name"),
                                    userJson.getString("image"),
                                    userJson.getInt("type"),
                                    userJson.getString("creation_date")
                            );

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(AccountModifyActivity.this).userLogin(user);

                            //finish Activity AccountModifyActivity
                            setResult(RESULT_OK);
                            finish();
                        }

                        //finish Activity AccountModifyActivity
                        setResult(RESULT_OK);
                        finish();


                    } else {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                eAccountInsertB.setEnabled(true);
                Toast.makeText(AccountModifyActivity.this, getString(R.string.error_no_data_received), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                if (!TextUtils.isEmpty(admin_ID)) {
                    pars.put("admin_ID", admin_ID);
                }
                if (!TextUtils.isEmpty(modifyBundle.getString(ADMIN_ID))) {
                    pars.put("type", eAccountType);
                }
                pars.put("student_ID", eAccountID);
                pars.put("username", eAccountUsername);
                if (!TextUtils.isEmpty(eAccountPassword)) {
                    pars.put("password", eAccountPassword);
                }
                pars.put("email", eAccountEmail);
                pars.put("name", eAccountName);
                pars.put("image", eAccountImage);

                return pars;
            }
        };
        mRequestQueue.add(request);

    }


}
