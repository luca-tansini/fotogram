package com.example.tanso.fotogram;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Model.Image;
import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUserActivity extends AppCompatActivity {

    private static final int TRIGGER_AUTO_COMPLETE = 42;
    private static final long AUTO_COMPLETE_DELAY = 250;
    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_search_user);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //Setup search field
        final EditText textSearch = findViewById(R.id.editTextSearch);
        final ListView suggestionList = findViewById(R.id.suggestionList);

        //Set textChangeListener
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //The handler is used to delay the REST call and only make it when the user stopped typing
        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage (Message msg){
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(textSearch.getText())) {
                        usersCall(textSearch.getText().toString());
                    }
                    else
                        suggestionList.setAdapter(null);
                }
                return false;
            }
        });

        //Set itemClickListener
        suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: controlla che non sia logged user
                User u = (User) parent.getAdapter().getItem(position);
                Intent userProfile = new Intent(getApplicationContext(), UserProfileActivity.class);
                userProfile.putExtra("username", u.getUsername());
                startActivity(userProfile);
            }
        });
    }

    private void usersCall(final String usernamestart){
        //users REST call
        RequestQueue rq = Model.getRequestQueue(getApplicationContext());
        String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/users";
        StringRequest followedRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray jsonArray = obj.getJSONArray("users");
                        ArrayList<User> users = new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++) {
                            JSONObject j = (JSONObject) jsonArray.get(i);
                            if(!j.getString("picture").equals("null"))
                                users.add(new User(j.getString("name"), new Image(j.getString("picture"))));
                            users.add(new User(j.getString("name"), null));
                        }
                        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(getApplicationContext(), R.layout.suggestion_entry, users);
                        ListView suggestionList = findViewById(R.id.suggestionList);
                        suggestionList.setAdapter(suggestionAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ajeje", "users error: "+error.networkResponse.toString());
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> params = new HashMap<>();
                params.put("session_id", Model.getInstance().getLoggedUser().getSessionId());
                params.put("usernamestart", usernamestart);
                return params;
            }
        };
        rq.add(followedRequest);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_search_user);
    }
}

