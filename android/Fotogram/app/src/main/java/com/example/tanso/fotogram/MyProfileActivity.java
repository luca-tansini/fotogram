package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        RoundedBitmapDrawable rbd = CircularBitmapDrawableFactory.create(getApplicationContext(),user.getProfilePicture());
        imageViewProfilePicture.setImageDrawable(rbd);

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
        RequestQueue rq = Volley.newRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Model.destroyInstance();
                        SharedPreferences settings = getSharedPreferences(getString(R.string.shared_preferences_filename), 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("sessionId");
                        editor.remove("username");
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finishAffinity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "error during logout!");
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
                return params;
            }
        };
        rq.add(sr);
    }

}
