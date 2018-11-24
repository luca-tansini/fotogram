package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_my_profile);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //Setta nome utente, immagine profilo
        Model model = Model.getInstance();
        LoggedUser user = model.getLoggedUser();
        TextView textViewusername = findViewById(R.id.textViewUsername);
        textViewusername.setText(user.getUsername());
        ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        imageViewProfilePicture.setImageResource(user.getProfilePicture());

        //Listview
        ListView lv = findViewById(R.id.userWall);

        //Chiamata REST per UserWall
        ArrayList<Post> userWall = new ArrayList<Post>();
        for(Post p: model.getHomeWall()){
            if(p.getUser().equals(user))
                userWall.add(p);
        }

        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, userWall);
        lv.setAdapter(adapter);

        //Button Logout
        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_my_profile);
    }

    private void logout(){
        Model.destroyInstance();
        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("sessionId");
        editor.remove("username");
        editor.commit();
        startActivity(new Intent(this,LoginActivity.class));
        finishAffinity();
    }

}
