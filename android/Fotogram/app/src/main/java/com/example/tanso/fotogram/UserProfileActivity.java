package com.example.tanso.fotogram;

import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Controller.FotogramAPI;
import com.example.tanso.fotogram.Controller.ResponseCode;
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

public class UserProfileActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //SetRefreshLayout
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                profileCall();
            }
        });

        //Set list header
        ListView userWallLV = findViewById(R.id.userWall);
        View header = getLayoutInflater().inflate(R.layout.user_profile_list_header, userWallLV, false);
        userWallLV.addHeaderView(header, null, false);

        //Retrieve username from Intent
        String username = getIntent().getExtras().getString("username");
        user = new User(username, null);
        TextView usernameTV = findViewById(R.id.textViewUsername);
        usernameTV.setText(username);

        profileCall();

    }

    private void profileCall(){
        //profile(user) REST call
        final ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        final ListView userWallLV = findViewById(R.id.userWall);

        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
        params.put("username", user.getUsername());
        FotogramAPI.makeAPICall(FotogramAPI.API.PROFILE, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            //Set profile picture
                            String pic = obj.getString("img");
                            if(!pic.equals("null")) {
                                Bitmap img = Base64Images.base64toBitmap(pic);
                                if(img != null) {
                                    user.updateProfilePicture(img);
                                    imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(), user.getProfilePicture()));
                                }
                            }
                            else
                                imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),R.drawable.user_round_256));

                            //Set (Un)Follow Button
                            final Button buttonFollow = findViewById(R.id.buttonFollow);
                            if(Model.getInstance().getLoggedUser().getFollowing().containsKey(user.getUsername())){
                                buttonFollow.setText("UNFOLLOW");
                                buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorUnfollow));
                            }
                            else{
                                buttonFollow.setText("FOLLOW");
                                buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorFollow));
                            }
                            buttonFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(buttonFollow.getText().equals("FOLLOW"))
                                        follow();
                                    else
                                        unfollow();
                                }
                            });

                            //Set userWall
                            JSONArray jsonArray = obj.getJSONArray("posts");
                            ArrayList<Post> userWall = new ArrayList<Post>();
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject j = (JSONObject) jsonArray.get(i);
                                if(!j.getString("img").equals("null"))
                                    userWall.add(new Post(user, Base64Images.base64toBitmap(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                                else
                                    userWall.add(new Post(user, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
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

    private void follow() {

        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
        params.put("username", user.getUsername());
        FotogramAPI.makeAPICall(FotogramAPI.API.FOLLOW, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        Model.getInstance().getLoggedUser().getFollowing().put(user.getUsername(),user);
                        Button buttonFollow = findViewById(R.id.buttonFollow);
                        buttonFollow.setText("UNFOLLOW");
                        buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorUnfollow));
                    }
                },
                null
        );
    }

    private void unfollow() {

        HashMap<String,String> params = new HashMap<>();
        params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
        params.put("username", user.getUsername());
        FotogramAPI.makeAPICall(FotogramAPI.API.UNFOLLOW, getApplicationContext(), params,
                new ResponseCode() {
                    @Override
                    public void run(String response) {
                        Model.getInstance().getLoggedUser().getFollowing().remove(user.getUsername());
                        Button buttonFollow = findViewById(R.id.buttonFollow);
                        buttonFollow.setText("FOLLOW");
                        buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorFollow));
                    }
                },
                null
        );
    }
}
