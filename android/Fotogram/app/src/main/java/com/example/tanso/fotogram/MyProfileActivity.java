package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.example.tanso.fotogram.Model.Base64Images;
import com.example.tanso.fotogram.Model.Image;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
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

        //Username
        Model model = Model.getInstance();
        final LoggedUser user = model.getLoggedUser();
        TextView usernameTV = findViewById(R.id.textViewUsername);
        usernameTV.setText(user.getUsername());

        //Profile picture
        final ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        if(user.getProfilePicture() != null)
            imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),user.getProfilePicture().getBitmap()));
        else
            imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),R.drawable.user_full));

        //Logout button
        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //User Wall
        final ListView lv = findViewById(R.id.userWall);

        //profile(user) REST call
        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";
        StringRequest followedRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("posts");
                            //Check if the user profile picture has changed
                            String pic = obj.getString("picture");
                            if(!pic.substring(0,16<=pic.length()? 16:pic.length()).equals(user.getProfilePicture().hash())){
                                user.updateProfilePicture(new Image(pic));
                                RoundedBitmapDrawable rbd = CircularBitmapDrawableFactory.create(getApplicationContext(), user.getProfilePicture().getBitmap());
                                imageViewProfilePicture.setImageDrawable(rbd);
                            }
                            ArrayList<Post> userWall = new ArrayList<Post>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                if(!j.getString("picture").equals("null"))
                                    userWall.add(new Post(user, new Image(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                else
                                    userWall.add(new Post(user, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                            }
                            Model.getInstance().setLoggedUserWall(userWall);
                            WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, userWall);
                            lv.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "profile(logged user) error: "+error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
                params.put("username", Model.getInstance().getLoggedUser().getUsername());
                return params;
            }
        };
        rq.add(followedRequest);
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
