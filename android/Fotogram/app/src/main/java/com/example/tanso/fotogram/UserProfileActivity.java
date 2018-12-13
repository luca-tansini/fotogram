package com.example.tanso.fotogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Model.Image;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //recupera nome utente dall'Intent
        String username = getIntent().getExtras().getString("username");
        TextView usernameTV = findViewById(R.id.textViewUsername);
        usernameTV.setText(username);

        profileCall(username);

    }

    private void profileCall(final String username){
        //profile(user) REST call
        final ImageView imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);
        final ListView userWallLV = findViewById(R.id.userWall);
        RequestQueue rq = Model.getRequestQueue(this);
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";
        StringRequest followedRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("ajeje", response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        User user = new User(username, null);

                        //Set profile picture
                        String pic = obj.getString("img");
                        if(!pic.equals("null")) {
                            Image img = new Image(pic);
                            if(img != null) {
                                user.updateProfilePicture(img);
                                imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(), user.getProfilePicture().getBitmap()));
                            }
                        }
                        else
                            imageViewProfilePicture.setImageDrawable(CircularBitmapDrawableFactory.create(getApplicationContext(),R.drawable.user_full));

                        //Set (Un)Follow Button
                        Button buttonFollow = findViewById(R.id.buttonFollow);
                        if(Model.getInstance().getLoggedUser().getFollowing().contains(user)){
                            buttonFollow.setText("UNFOLLOW");
                            buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorUnfollow));
                        }
                        else{
                            buttonFollow.setText("FOLLOW");
                            buttonFollow.setBackgroundTintList(getResources().getColorStateList(R.color.colorFollow));
                        }

                        //Set userWall
                        JSONArray jsonArray = obj.getJSONArray("posts");
                        ArrayList<Post> userWall = new ArrayList<Post>();
                        for(int i=0; i<jsonArray.length(); i++) {
                            JSONObject j = (JSONObject) jsonArray.get(i);
                            if(!j.getString("img").equals("null"))
                                userWall.add(new Post(user, new Image(j.getString("img")), j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                            else
                                userWall.add(new Post(user, null, j.getString("msg"), Timestamp.valueOf(j.getString("timestamp"))));
                        }
                        Model.getInstance().setLoggedUserWall(userWall);
                        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, userWall);
                        userWallLV.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ajeje", "profile(user) error: "+error.toString());
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id",Model.getInstance().getLoggedUser().getSessionId());
                params.put("username", username);
                return params;
            }
        };
        rq.add(followedRequest);
    }

}
