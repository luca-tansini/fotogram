package com.example.tanso.fotogram;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.User;

public class SearchUserActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;

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
        AppCompatAutoCompleteTextView textSearch = findViewById(R.id.editTextSearch);
        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(this, android.R.layout.simple_dropdown_item_1line, Model.getInstance().getLoggedUser().getFollowing());
        textSearch.setAdapter(suggestionAdapter);
        textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = (User) parent.getAdapter().getItem(position);
                Intent userprofile = new Intent(getApplicationContext(), UserProfileActivity.class);
                userprofile.putExtra("username", u.getUsername());
                startActivity(userprofile);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_search_user);
    }
}

