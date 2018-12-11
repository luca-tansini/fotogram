package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

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

    private void startHome(String sessionId, String username){

        //CHIAMATE REST A PROFILE E WALL PER OTTENERE IMMAGINE DEL PROFILO, HOMEWALL E LOGGEDUSERWALL
        int profilePicture = R.drawable.cavallo_xs;
        List<User> following = new ArrayList<User>();
        User froggo = new User("froggo",R.drawable.rana_xs);
        User doggo  = new User("doggo",R.drawable.cane_xs);
        following.add(froggo);
        following.add(doggo);

        LoggedUser loggedUser = new LoggedUser(username, profilePicture, sessionId, following);

        Post mypost = new Post(loggedUser, R.drawable.acqua_xs, "watercircles", Timestamp.valueOf("2018-11-23 11:11:00"));
        List<Post> loggedUserWall = new ArrayList<Post>();
        loggedUserWall.add(mypost);

        List<Post> homeWall = new ArrayList<Post>();
        homeWall.add(new Post(froggo,R.drawable.rana_xs, "#FreePepe", Timestamp.valueOf("2018-11-23 12:23:00")));
        homeWall.add(mypost);
        homeWall.add(new Post(froggo,R.drawable.chitarra_xs, "Guitar", Timestamp.valueOf("2018-11-23 9:03:00")));
        homeWall.add(new Post(doggo,R.drawable.pastry_xs, "#food #sweets", Timestamp.valueOf("2018-11-22 21:37:00")));
        homeWall.add(new Post(froggo,R.drawable.palloncini_xs, "Palloncini", Timestamp.valueOf("2018-11-20 16:59:00")));
        homeWall.add(new Post(doggo,R.drawable.cane_xs, "#me #selfie", Timestamp.valueOf("2018-11-15 11:42:00")));

        //CREAZIONE DEL MODEL
        Model.buildInstance(loggedUser, homeWall, loggedUserWall);
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }

    private void login(){
        EditText usernameET = findViewById(R.id.editTextUsername);
        EditText passwordET = findViewById(R.id.editTextPassword);
        final String usr,pswd;
        usr = usernameET.getText().toString();
        pswd = passwordET.getText().toString();

        //Volley POST request
        RequestQueue rq = Volley.newRequestQueue(this);
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


    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}
