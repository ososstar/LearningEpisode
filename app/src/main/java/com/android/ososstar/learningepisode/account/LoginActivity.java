package com.android.ososstar.learningepisode.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.ososstar.learningepisode.ConnectivityHelper;
import com.android.ososstar.learningepisode.HomeAdminActivity;
import com.android.ososstar.learningepisode.HomeStudentActivity;
import com.android.ososstar.learningepisode.HttpsTrustManager;
import com.android.ososstar.learningepisode.R;
import com.android.ososstar.learningepisode.SharedPrefManager;
import com.android.ososstar.learningepisode.URLs;
import com.android.ososstar.learningepisode.sslService;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    //declare EditTexts of LoginActivity
    EditText l_username_et, l_password_et;

    TextInputLayout usernameTL, passwordTL;

    /**
     * Volley Method to make login request
     */
    private RequestQueue mRequestQueue;
    private ProgressBar progressBar;

    private boolean isLoggedIn;
    private Button Login_b;

    public void supportSSL() {
//        if (Build.VERSION.SDK_INT > 16 && Build.VERSION.SDK_INT < 20 || SharedPrefManager.getInstance(this).getSSLStatus()) {}
        Intent sslIntent = new Intent(this, sslService.class);
        startService(sslIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //support ssl for older devices
        supportSSL();

        //if the user is already logged in we will directly start the Home activity for admin or student
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            switch (SharedPrefManager.getInstance(this).getUser().getType()) {
                case 0:
                    startActivity(new Intent(this, HomeAdminActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, HomeStudentActivity.class));
                    break;
            }
            return;
        }

        progressBar = findViewById(R.id.login_spinner);

        //define TextInputLayouts of Login
        usernameTL = findViewById(R.id.l_Username_TL);
        passwordTL = findViewById(R.id.l_password_TL);

        //define EditTexts of LoginActivity
        l_username_et = findViewById(R.id.l_username);
        l_password_et = findViewById(R.id.l_password);

        l_username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        l_password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordTL.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //define login Button
        Login_b = findViewById(R.id.l_login_b);

        mRequestQueue = Volley.newRequestQueue(this);

        //if user presses on login, calling the method login
        Login_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityHelper.isNetworkAvailable(getApplicationContext())) {
                    userLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //define register TextView to make intent to RegisterActivity on click
        TextView l_register = findViewById(R.id.l_register);

        //setup intent to move from LoginActivity to RegisterActivity
        l_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void userLogin() {
        //first getting the values
        final String username = l_username_et.getText().toString().trim();
        final String password = l_password_et.getText().toString().trim();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            usernameTL.setErrorEnabled(true);
            usernameTL.setError("Please enter your username");
            l_username_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Login_b.setEnabled(true);
            return;

        }

        if (TextUtils.isEmpty(password)) {
            passwordTL.setErrorEnabled(true);
            passwordTL.setError("Please enter your password");
            l_password_et.requestFocus();
            progressBar.setVisibility(View.GONE);
            Login_b.setEnabled(true);
            return;
        }

        //if everything is fine
        Login_b.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        HttpsTrustManager.allowAllSSL();
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                if (response != null) {
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
                                    userJson.getString("creation_date")
                            );

                            int type = user.getType();
                            Log.v("type = ", Integer.toString(type));

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

                            //starting the User activity
                            switch (type) {
                                case 0:
                                    //case of admin
                                    finish();
                                    startActivity(new Intent(getBaseContext(), HomeAdminActivity.class));
                                    break;
                                case 1:
                                    //case of student
                                    finish();
                                    startActivity(new Intent(getBaseContext(), HomeStudentActivity.class));
                                    break;
                            }

                        } else {
                            Login_b.setEnabled(true);
                            Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Set empty state text to display "No Data is Found"
                    Toast.makeText(LoginActivity.this, "No Data is Found", Toast.LENGTH_SHORT).show();
                }

                VolleyLog.wtf(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Login_b.setEnabled(true);

                SharedPrefManager.getInstance(LoginActivity.this).setSSLStatus(1);
                supportSSL();

                error.printStackTrace();
                // Set empty state text to display "No Users is found."
                Toast.makeText(LoginActivity.this, "Error: connection failed", Toast.LENGTH_SHORT).show();
            }
        }) {
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
                pars.put("password", password);
                return pars;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }
        };

        mRequestQueue.add(request);
    }
}
