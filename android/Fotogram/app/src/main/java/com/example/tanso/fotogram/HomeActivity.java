package com.example.tanso.fotogram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Controller.FotogramAPI;
import com.example.tanso.fotogram.Controller.ResponseCode;
import com.example.tanso.fotogram.Model.Base64Images;
import com.example.tanso.fotogram.Model.LoggedUser;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;
    private SwipeRefreshLayout refreshLayout;
    private LoggedUser loggedUser;

    public static int REFRESH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Cache logged user data
        loggedUser = Model.getInstance().getLoggedUser();

        //SetRefreshLayout
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                followedCall();
            }
        });

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);


        //Set wall listener
        ListView wall = findViewById(R.id.wall);
        wall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post p = (Post) parent.getAdapter().getItem(position);
                User u = p.getUser();
                if(u.getUsername().equals(loggedUser.getUsername())){
                    Intent myprofile = new Intent(getApplicationContext(), MyProfileActivity.class);
                    myprofile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    getApplicationContext().startActivity(myprofile);
                }
                else{
                    Intent userprofile = new Intent(getApplicationContext(), UserProfileActivity.class);
                    userprofile.putExtra("username", u.getUsername());
                    getApplicationContext().startActivity(userprofile);
                }
            }
        });

        //Show Wall
        followedCall();

    }

    /*
    followedCall retrieves followed users profile pictures from server
    then calls wallCall to download posts and show them
    */
    private void followedCall(){
        refreshLayout.setRefreshing(true);
        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",loggedUser.getSessionId());
        FotogramAPI.makeAPICall(FotogramAPI.API.FOLLOWED, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("followed");
                            HashMap<String,User> following = new HashMap<>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                //Logged user data
                                if(j.get("name").equals(loggedUser.getUsername())) {
                                    if (!j.getString("picture").equals("null"))
                                        loggedUser.updateProfilePicture(Base64Images.base64toBitmap(j.getString("picture")));
                                    loggedUser.setFollowing(following);
                                }//Other users data
                                else {
                                    String name = j.getString("name");
                                    if (!j.getString("picture").equals("null"))
                                        following.put(name, new User(name, Base64Images.base64toBitmap(j.getString("picture"))));
                                    else
                                        following.put(name, new User(name, null));
                                }
                            }
                            wallCall();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                null);
    }

    /*
    wallCall retrieves posts from server
    then calls showWall to show them
     */
    private void wallCall(){
        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",loggedUser.getSessionId());
        FotogramAPI.makeAPICall(FotogramAPI.API.WALL, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("posts");
                            List<Post> wall = new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                String usr = j.getString("user");
                                User u = usr.equals(loggedUser.getUsername())? loggedUser : loggedUser.getFollowing().get(usr);
                                if(u != null)
                                    if (!j.getString("img").equals("null"))
                                        wall.add(new Post(u, Base64Images.base64toBitmap(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                    else
                                        wall.add(new Post(u, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                else
                                    Log.d("ajeje", "error: user("+usr+") not in following?");
                            }
                            showWall(wall);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                null
        );
    }

    private void showWall(List<Post> wall){
        //Set listview adapter
        ListView lv = findViewById(R.id.wall);
        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, wall);
        lv.setAdapter(adapter);
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Only updates wall (followed users should always be up to date)
        if(intent != null && intent.getExtras() != null && intent.getExtras().getInt("mode") == REFRESH){
            Log.d("ajeje", "HomeActivity onRestart - REFRESH");
            refreshLayout.setRefreshing(true);
            wallCall();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
    }
}
