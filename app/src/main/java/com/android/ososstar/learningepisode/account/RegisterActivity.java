package com.android.ososstar.learningepisode.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ososstar.learningepisode.HomeStudentActivity;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //declare EditTexts of RegisterActivity
    EditText Username_et, Password_et, Email_et, Name_et;
    String StudentType = "1";
    String AdminType = "0";

    private ProgressBar progressBar;

    private RequestQueue mRequestQueue;

    private Button Register_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //define EditTexts of RegisterActivity
        Username_et = findViewById(R.id.r_username);
        Password_et = findViewById(R.id.r_password);
        Email_et = findViewById(R.id.r_email);
        Name_et = findViewById(R.id.r_name);

        mRequestQueue = Volley.newRequestQueue(this);

        //define Register Button
        Register_b = findViewById(R.id.r_register_b);
        Register_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar = findViewById(R.id.spinner);
                progressBar.setVisibility(View.VISIBLE);
                registerUser();
            }
        });

        //define login TextView to make intent to LoginActivity
        TextView r_login = findViewById(R.id.r_login);

        //setup intent to move from LoginActivity to RegisterActivity
        r_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //this Method to Register the user when triggered
    private void registerUser(){
        Register_b.setEnabled(false);

        final String username = Username_et.getText().toString().trim();
        final String password = Password_et.getText().toString().trim();
        final String email = Email_et.getText().toString().trim();
        final String name = Name_et.getText().toString().trim();

        //first we will do the validations
        if (TextUtils.isEmpty(username)) {
            Username_et.setError("Please enter username");
            Username_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Register_b.setEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Email_et.setError("Please enter your email");
            Email_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Register_b.setEnabled(true);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email_et.setError("Enter a valid email");
            Email_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Register_b.setEnabled(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Password_et.setError("Enter a password");
            Password_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Register_b.setEnabled(true);
            return;
        }
        //if it passes all the validations and everything is fine

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                if (response != null) {
                    //hiding the progressbar after completion
                    progressBar.setVisibility(View.GONE);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            //getting the user from the response
                            JSONObject userJson = obj.getJSONObject("user");

                            //creating a new user object
                            User user = new User(
                                    userJson.getInt("id"),
                                    userJson.getString("username"),
                                    userJson.getString("email"),
                                    userJson.getString("name"),
                                    userJson.getString("image"),
                                    userJson.getInt("type"),
                                    userJson.getString("date")
                            );

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(RegisterActivity.this).userLogin(user);

                            //starting the User Home activity
                            finish();
                            startActivity(new Intent(RegisterActivity.this, HomeStudentActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Set empty state text to display "No Data is Found"
                    Toast.makeText(RegisterActivity.this, "No Data is Found", Toast.LENGTH_SHORT).show();
                }

                VolleyLog.wtf(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

                SharedPrefManager.getInstance(RegisterActivity.this).setSSLStatus(1);

                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                Toast.makeText(RegisterActivity.this, "account is not properly registered", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> pars = new HashMap<>();
                pars.put("username", username);
                pars.put("email", email);
                pars.put("password", password);
                pars.put("name", name);
                pars.put("type", StudentType);

                return pars;
            }
        };

        mRequestQueue.add(request);
    }
}
