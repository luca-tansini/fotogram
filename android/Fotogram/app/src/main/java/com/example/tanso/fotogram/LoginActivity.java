package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Is the user logged in already?
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
        String sessionId = settings.getString("sessionId", "");
        String username = settings.getString("username", "");
        if(!sessionId.equals("") && !username.equals("")){
            startHome(sessionId, username);
        }

        setContentView(R.layout.activity_login);
        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void startHome(String sessionId, String username){

        //CHIAMATE REST A PROFILE E WALL PER OTTENERE IMMAGINE DEL PROFILO, HOMEWALL E LOGGEDUSERWALL

        int profilePicture = R.drawable.cavallo_xs;
        List<User> following = new ArrayList<User>();
        User froggo = new User("froggo",R.drawable.rana_xs);
        User doggo  = new User("doggo",R.drawable.cane_xs);

        LoggedUser loggedUser = new LoggedUser(username, profilePicture, sessionId, following);

        Post mypost = new Post(loggedUser, R.drawable.acqua_xs, "watercircles", Timestamp.valueOf("2018-11-23 11:11:00"));
        List<Post> loggedUserWall = new ArrayList<Post>();
        loggedUserWall.add(mypost);

        List<Post> homeWall = new ArrayList<Post>();
        homeWall.add(new Post(froggo,R.drawable.rana_xs, "#FreePepe", Timestamp.valueOf("2018-11-23 12:23:00")));
        homeWall.add(mypost);
        homeWall.add(new Post(froggo,R.drawable.arance_xs, "Orange is the new black", Timestamp.valueOf("2018-11-23 9:03:00")));
        homeWall.add(new Post(doggo,R.drawable.pastry_xs, "#food #sweets", Timestamp.valueOf("2018-11-22 21:37:00")));
        homeWall.add(new Post(froggo,R.drawable.palloncini_xs, "Palloncini e palazzi", Timestamp.valueOf("2018-11-20 16:59:00")));
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

        String usr,pass;
        usr = usernameET.getText().toString();
        pass = passwordET.getText().toString();

        if(usr.equals("") || pass.equals("")){
            //TOAST RIEMPI I CAMPI!
            return;
        }

        if(usr.equals("bojack") && pass.equals("horseman")){
            //LOGIN CORRETTO
            String sessionId = "42";
            SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sessionId", sessionId);
            editor.putString("username", usr);
            editor.commit();
            startHome(sessionId, usr);
        }

        //LOGIN ERRATO, TOAST

    }

}
