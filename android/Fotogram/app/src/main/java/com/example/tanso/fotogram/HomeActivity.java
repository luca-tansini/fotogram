package com.example.tanso.fotogram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tanso.fotogram.Model.Model;
import com.example.tanso.fotogram.Model.Post;
import com.example.tanso.fotogram.Model.User;

public class HomeActivity extends AppCompatActivity {

    private MyNavigationItemSelectedListener myNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Bottom navigation bar management
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
        myNavigationItemSelectedListener = new MyNavigationItemSelectedListener(this);
        nav.setOnNavigationItemSelectedListener(myNavigationItemSelectedListener);

        //Listview
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
