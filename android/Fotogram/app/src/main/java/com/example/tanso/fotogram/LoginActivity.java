package com.example.tanso.fotogram;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Has the user logged in already?
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
        String sessionId = settings.getString("sessionId", "");
        String username = settings.getString("username", "");
        if(!sessionId.equals("") && !username.equals("")){
            startHome(sessionId, username);
        }

        setContentView(R.layout.activity_login);

        //Set login form
        final Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        loginButton.setEnabled(false);
        final TextView error = findViewById(R.id.textViewError);
        final EditText usernameET = findViewById(R.id.editTextUsername);
        final EditText passwordET = findViewById(R.id.editTextPassword);
        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0 && passwordET.getText().length() > 0)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
                error.setText("");
            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0 && usernameET.getText().length() > 0)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
                error.setText("");
            }
        });
    }

    private void login(){
        EditText usernameET = findViewById(R.id.editTextUsername);
        EditText passwordET = findViewById(R.id.editTextPassword);
        final String usr,pswd;
        usr = usernameET.getText().toString();
        pswd = passwordET.getText().toString();

        //Volley POST request
        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/login";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String sid) {
                        Log.d("ajeje", "sid: "+sid);
                        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("sessionId", sid);
                        editor.putString("username", usr);
                        editor.apply();
                        startHome(sid, usr);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "volley error: "+error.toString());
                        TextView errorTV = findViewById(R.id.textViewError);
                        errorTV.setText("invalid username or password");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("username",usr);
                params.put("password",pswd);
                return params;
            }
        };
        rq.add(sr);
    }

    private void startHome(final String sessionId, final String username){

        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/followed";
        StringRequest followedRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ajeje", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("followed");
                            List<User> following = new ArrayList<User>();
                            LoggedUser lu = null;
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                //Logged user data
                                if(j.get("name").equals(username))
                                    lu = new LoggedUser(username, j.getString("picture"), sessionId, following);
                                    //Other users data
                                else
                                    following.add(new User(j.getString("name"), j.getString("picture")));
                            }
                            if(lu != null) {
                                Model model = Model.getInstance();
                                model.setLoggedUser(lu);
                                Log.d("ajeje", "Model.getInstance().getLoggedUser().getSessionId(): "+Model.getInstance().getLoggedUser().getSessionId());
                                Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(home);
                                finish();
                            }
                            else{
                                Log.d("ajeje", "logged user not found?!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", error.networkResponse.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",sessionId);
                return params;
            }
        };
        rq.add(followedRequest);
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}
