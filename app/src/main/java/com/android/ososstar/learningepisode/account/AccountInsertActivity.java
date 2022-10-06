package com.android.ososstar.learningepisode.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AccountInsertActivity extends AppCompatActivity {

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
    private String eAccountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_account);

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

        //define type spinner
        eAccountTypeSP = findViewById(R.id.eAccountType_sp);

        //remove error signal if user is typing
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

        eAccountPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eAccountPasswordTL.setErrorEnabled(false);
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


        // Initializing a String Array
        String[] types = new String[]{
                "unspecified",
                "Admin",
                "Student"
        };

        // Initializing an ArrayAdapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, types);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Sets the Adapter used to provide the data which backs this Spinner.
        eAccountTypeSP.setAdapter(spinnerAdapter);

        eAccountTypeSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //use position value
                switch (position) {
                    case 0:
                        eAccountType = "";
                        break;
                    case 1:
                        eAccountType = "0";
                        break;
                    case 2:
                        eAccountType = "1";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //define insert Button
        eAccountInsertB = findViewById(R.id.eAccountInsert_b);

        progressBar = findViewById(R.id.eAccountProgressSpinner);

        mRequestQueue = Volley.newRequestQueue(this);

        //calling insertNewUser method on insert button click if there is connect available
        eAccountInsertB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isNetworkAvailable(AccountInsertActivity.this)) {
                    insertNewUser();
                }
            }
        });

    }

    private void insertNewUser() {
        //variables to be filled by the user
        final String eAccountUsername, eAccountPassword, eAccountEmail, eAccountName, eAccountImage;
        eAccountUsername = eAccountUsernameET.getText().toString().trim();
        eAccountPassword = eAccountPasswordET.getText().toString().trim();
        eAccountEmail = eAccountEmailET.getText().toString().trim();
        eAccountName = eAccountNameET.getText().toString().trim();
        eAccountImage = eAccountImageET.getText().toString().trim();

        //validating required data
        if (TextUtils.isEmpty(eAccountUsername)) {
            eAccountUsernameTL.setErrorEnabled(true);
            eAccountUsernameTL.setError("Please Fill the Username Field");
            eAccountUsernameET.requestFocus();
            return;
        }
        //validating required data
        if (TextUtils.isEmpty(eAccountPassword)) {
            eAccountPasswordTL.setErrorEnabled(true);
            eAccountPasswordTL.setError("Please Fill the Password Field");
            eAccountPasswordET.requestFocus();
            return;
        }
        //validating required data
        if (TextUtils.isEmpty(eAccountEmail)) {
            eAccountEmailTL.setErrorEnabled(true);
            eAccountEmailTL.setError("Please Fill the Username Field");
            eAccountEmailET.requestFocus();
            return;
        }
        //validating required data
        if (TextUtils.isEmpty(eAccountName)) {
            eAccountNameTL.setErrorEnabled(true);
            eAccountNameTL.setError("Please Fill the Username Field");
            eAccountNameET.requestFocus();
            return;
        }
        //validating required data
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
        //validating required data
        if (TextUtils.isEmpty(eAccountType)) {
            TextView errorText = (TextView) eAccountTypeSP.getSelectedView();
            errorText.setError("Please Specify The user type");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        eAccountInsertB.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject baseJSONObject = new JSONObject(response);

                    //if no error in response
                    if (!baseJSONObject.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();

                        //finish activity and refresh user list
                        setResult(RESULT_OK, new Intent());
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), baseJSONObject.getString("message"), Toast.LENGTH_SHORT).show();
                        eAccountInsertB.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VolleyLog.wtf(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AccountInsertActivity.this, getString(R.string.error_no_data_received), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                eAccountInsertB.setEnabled(true);
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
                HashMap<String, String> pars = new HashMap<>();
                pars.put("username", eAccountUsername);
                pars.put("password", eAccountPassword);
                pars.put("email", eAccountEmail);
                pars.put("name", eAccountName);
                if (!TextUtils.isEmpty(eAccountImage)) {
                    pars.put("image_URL", eAccountImage);
                }
                pars.put("type", eAccountType);
                return pars;
            }
        };
        mRequestQueue.add(request);

    }


}
