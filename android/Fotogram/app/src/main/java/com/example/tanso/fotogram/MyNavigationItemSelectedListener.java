package com.example.tanso.fotogram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

public class MyNavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Context context;

    public MyNavigationItemSelectedListener(Context context){
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.navigation_upload:
                if(context.getClass().equals(UploadActivity.class))
                    return true;
                Intent upload = new Intent(context, UploadActivity.class);
                context.startActivity(upload);
                return true;

            case R.id.navigation_home:
                if(context.getClass().equals(HomeActivity.class))
                    return true;
                Intent home = new Intent(context, HomeActivity.class);
                context.startActivity(home);
                return true;

            case R.id.navigation_dashboard:
                return true;

            case R.id.navigation_notifications:
                return true;

        }
        return false;
    }

}
