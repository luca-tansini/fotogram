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
                upload.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(upload);
                return true;

            case R.id.navigation_home:
                if(context.getClass().equals(HomeActivity.class))
                    return true;
                Intent home = new Intent(context, HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(home);
                return true;

            case R.id.navigation_search_user:
                if(context.getClass().equals(SearchUserActivity.class))
                    return true;
                Intent search = new Intent(context, SearchUserActivity.class);
                search.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(search);
                return true;

            case R.id.navigation_my_profile:
                if(context.getClass().equals(MyProfileActivity.class))
                    return true;
                Intent myprofile = new Intent(context, MyProfileActivity.class);
                myprofile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(myprofile);
                return true;

        }
        return false;
    }
}
