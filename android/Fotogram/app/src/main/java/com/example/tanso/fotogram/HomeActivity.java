package com.example.tanso.fotogram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Model.Base64Images;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Model model = Model.getInstance();
        //Followed REST call
        followedCall(model.getLoggedUser().getSessionId(), model.getLoggedUser().getUsername());

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //Wall REST call
        wallCall();
    }

    private void followedCall(final String sessionId, final String username){

        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/followed";
        StringRequest followedRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("followed");
                            HashMap<String,User> following = new HashMap<>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                //Logged user data
                                if(j.get("name").equals(username)) {
                                    if (!j.getString("picture").equals("null"))
                                        Model.getInstance().getLoggedUser().updateProfilePicture(Base64Images.base64toBitmap(j.getString("picture")));
                                    Model.getInstance().getLoggedUser().setFollowing(following);
                                }//Other users data
                                else {
                                    String name = j.getString("name");
                                    if (!j.getString("picture").equals("null"))
                                        following.put(name, new User(name, Base64Images.base64toBitmap(j.getString("picture"))));
                                    else
                                        following.put(name, new User(name, null));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "followed error: "+error.networkResponse.toString());
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

    private void wallCall(){
        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/wall";
        StringRequest wallRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("posts");
                            List<Post> wall = new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                String usr = j.getString("user");
                                User u = Model.getInstance().getLoggedUser().getFollowing().get(usr);
                                if(u != null)
                                    if (!j.getString("img").equals("null"))
                                        wall.add(new Post(u, Base64Images.base64toBitmap(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                    else
                                        wall.add(new Post(u, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                else
                                    Log.d("ajeje", "error: user("+usr+") not in following?");
                            }
                            Model.getInstance().setHomeWall(wall);
                            showWall();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", "wall error: "+error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
                return params;
            }
        };
        rq.add(wallRequest);
    }

    private void showWall(){
        //Set listview
        Model model = Model.getInstance();
        ListView lv = findViewById(R.id.wall);
        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, model.getHomeWall());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post p = (Post) parent.getAdapter().getItem(position);
                User u = p.getUser();
                if(u.getUsername().equals(Model.getInstance().getLoggedUser().getUsername())){
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
    }
}
