package com.example.tanso.fotogram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.example.tanso.fotogram.Controller.FotogramAPI;
import com.example.tanso.fotogram.Controller.ResponseCode;
import com.example.tanso.fotogram.Model.Base64Images;
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

    public static int REFRESH = 0;

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;
    private SwipeRefreshLayout refreshLayout;
    private LoggedUser loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        //Set list header
        ListView userWallLV = findViewById(R.id.userWall);
        View header = getLayoutInflater().inflate(R.layout.myprofile_list_header,userWallLV, false);
        userWallLV.addHeaderView(header, null, false);

        //Set invisible empty wall header (just in case)
        View emptyListHeader = getLayoutInflater().inflate(R.layout.empty_list_header, userWallLV, false);
        userWallLV.addHeaderView(emptyListHeader, null, false);
        TextView emptyMessage = findViewById(R.id.empty_list_message);
        emptyMessage.setVisibility(View.GONE);
        emptyMessage.setText("This looks empty, go create your first post!");

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        Log.d("ajeje", "MyProfile - onCreate - setting nav to navigation_my_profile");
        nav.setSelectedItemId(R.id.navigation_my_profile);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //SetRefreshLayout
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileCall();
            }
        });

        //Username
        Model model = Model.getInstance();
        loggedUser = model.getLoggedUser();
        TextView usernameTV = findViewById(R.id.textViewUsername);
        usernameTV.setText(loggedUser.getUsername());

        //Logout button
        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Update profile picture button
        Button pictureUpdateButton = findViewById(R.id.buttonUpdatePicture);
        pictureUpdateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UpdateProfilePictureActivity.class));
            }
        });

        //Setup User Wall
        profileCall();

    }

    private void profileCall(){
        final ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        final ListView userWallLV = findViewById(R.id.userWall);
        final TextView emptyMessage = findViewById(R.id.empty_list_message);
        emptyMessage.setVisibility(View.GONE);

        HashMap<String,String> params = new HashMap<>();
        params.put("session_id", loggedUser.getSessionId());
        params.put("username", loggedUser.getUsername());
        FotogramAPI.makeAPICall(FotogramAPI.API.PROFILE, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("posts");

                            //Check if the loggedUser profile picture has changed
                            String pic = obj.getString("img");
                            if(!pic.equals("null"))
                                loggedUser.updateProfilePicture(Base64Images.base64toBitmap(pic));
                            if(loggedUser.getProfilePicture() != null)
                                imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(), loggedUser.getProfilePicture()));
                            else
                                imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),R.drawable.user_round_256));
                            ArrayList<Post> userWall = new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                if(!j.getString("img").equals("null"))
                                    userWall.add(new Post(loggedUser, Base64Images.base64toBitmap(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                else
                                    userWall.add(new Post(loggedUser, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                            }
                            if(userWall.size() == 0){
                                emptyMessage.setVisibility(View.VISIBLE);
                            }
                            WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, userWall);
                            userWallLV.setAdapter(adapter);
                            refreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                null
        );
    }

    private void logout(){

        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",loggedUser.getSessionId());
        FotogramAPI.makeAPICall(FotogramAPI.API.LOGOUT, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
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
                null
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Could update profile picture only
        if(intent != null && intent.getExtras() != null && intent.getExtras().getInt("mode") == REFRESH){
            Log.d("ajeje", "MyProfileActivity onRestart - REFRESH");
            refreshLayout.setRefreshing(true);
            profileCall();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        Log.d("ajeje", "MyProfile - onRestart - setting nav to navigation_my_profile");
        nav.setSelectedItemId(R.id.navigation_my_profile);
    }
}
