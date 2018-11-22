package com.example.tanso.fotogram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        List<Post> wall = new ArrayList<Post>();
        wall.add(new Post(new User("froggo",R.drawable.rana_s),R.drawable.aurora_boreale, "Che bella l'aurora boreale!", "3 hours ago"));
        wall.add(new Post(new User("doggo",R.drawable.cane_s),R.drawable.palazzi_m, "Auuuuuuu", "yesterday"));
        wall.add(new Post(new User("froggo",R.drawable.rana_s),R.drawable.palloncini_m, "Cra Cra palloncini", "3 days ago"));

        ListView lv = findViewById(R.id.wall);
        WallAdapter adapter = new WallAdapter(getApplicationContext(), R.layout.wall_entry, wall);
        lv.setAdapter(adapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BottomNavigationView nav = findViewById(R.id.navigation);
        nav.setSelectedItemId(R.id.navigation_home);
    }
}
